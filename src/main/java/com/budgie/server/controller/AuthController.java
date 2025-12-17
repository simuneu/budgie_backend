package com.budgie.server.controller;

import com.budgie.server.dto.*;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.security.JwtProvider;
import com.budgie.server.service.AuthService;
import com.budgie.server.service.NaverLoginService;
import com.budgie.server.service.SocialLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${front.redirect.uri}")
    private String frontUri;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.uri.redirect}")
    private String redirectUri;

    private final AuthService authService;
    private final SocialLoginService socialLoginService;
    private final JwtProvider jwtProvider;
    private final NaverLoginService naverLoginService;

    @Autowired
    public AuthController(AuthService authService, SocialLoginService socialLoginService,
                          JwtProvider jwtProvider, NaverLoginService naverLoginService){
        this.authService = authService;
        this.socialLoginService = socialLoginService;
        this.jwtProvider = jwtProvider;
        this.naverLoginService = naverLoginService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequestDto requestDto){
        log.debug("회원가입 요청:{}", requestDto);

        try{
            UserEntity savedUser = authService.signup(requestDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.ok(savedUser));
        }catch (RuntimeException e){
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.okMessage(e.getMessage()));
        }
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        try{
            AuthResponseDto responseDto   = authService.login(requestDto);
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", responseDto.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .domain(".budgie.fit")
                    .maxAge(jwtProvider.getRefreshTokenExpirationSeconds())
                    .build();
            response.addHeader("Set-Cookie", refreshCookie.toString());

            return ResponseEntity.ok(Map.of(
                    "accessToken", responseDto.getAccessToken(),
                    "grantType", responseDto.getGrantType()
            ));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.okMessage(e.getMessage()));
        }
    }

    //카카오 소셜 로그인
    @GetMapping("/kakao/loginstart")
    public RedirectView redirectToKakao() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code";
        log.debug("카카오 로그인 리다이렉트: {}", kakaoAuthUrl);
        return new RedirectView(kakaoAuthUrl);
    }

    @GetMapping("/kakao")
    public void kakaoLogin(@RequestParam("code")String code, HttpServletResponse response) throws IOException{
        try{
            AuthResponseDto auth = socialLoginService.kakaoLogin(code);

            String accessToken = auth.getAccessToken();
            String refreshToken = auth.getRefreshToken();

            String redirectUrl = frontUri+"/oauth/callback"+
                    "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8) +
                    "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);
        }catch (Exception e){
            String errorRedirectUrl = frontUri + "/login?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(errorRedirectUrl);
        }
    }

    //네이버 소셜 로그인
    @GetMapping("/naver/loginstart")
    public RedirectView redirectNaver(HttpSession session){
        String state = jwtProvider.generateStateToken();
        session.setAttribute("naver_oauth_state", state);
        String naverAuthUrl = "https://nid.naver.com/oauth2.0/authorize" +
                "?response_type=code" +
                "&client_id=" + naverClientId +
                "&redirect_uri=" + redirectUri +
                "&state=" + state;
        return new RedirectView(naverAuthUrl);
    }

    @GetMapping("/naver")
    public void naverLogin (@RequestParam("code") String code, @RequestParam("state") String state,
                            HttpSession session, HttpServletResponse response) throws IOException{
        String savedState = (String) session.getAttribute("naver_oauth_state");

        if (savedState == null || !savedState.equals(state)){
            log.warn("Naver login failed: State token mismatch.");
            String errorRedirectUrl = frontUri + "/login?error=" + URLEncoder.encode("State token mismatch.", StandardCharsets.UTF_8);
            response.sendRedirect(errorRedirectUrl);
            return;
        }
        try {
            AuthResponseDto auth = socialLoginService.naverLogin(code, state);

            String accessToken = auth.getAccessToken();
            String refreshToken = auth.getRefreshToken();

            String redirectUrl = frontUri + "/oauth/callback" +
                    "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8) +
                    "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("네이버 로그인 처리 중 에러 발생: {}", e.getMessage(), e);
            String errorRedirectUrl = frontUri + "/login?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(errorRedirectUrl);
        }
    }


    //토큰 갱신 refresh기반 access재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                                HttpServletResponse response){

        if(refreshToken == null || refreshToken.isBlank()){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.okMessage("refreshToken이 없습니다."));
        }

        try{
            AuthResponseDto newToken = authService.refreshAccessToken(refreshToken);

            ResponseCookie newCookie = ResponseCookie.from("refreshToken", newToken.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .domain(".budgie.fit")
                    .maxAge(jwtProvider.getRefreshTokenExpirationSeconds())
                    .build();

            response.addHeader("Set-Cookie", newCookie.toString());

            return ResponseEntity.ok(Map.of(
                    "accessToken", newToken.getAccessToken(),
                    "grantType", newToken.getGrantType()
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.okMessage(e.getMessage()));
        }
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.okMessage("logout success"));
    }

    //비밀번호 재설정(임시비번)
    @PostMapping("/password/reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody  EmailRequestDto dto){
        authService.setPasswordResetCode(dto.getEmail());
        return ResponseEntity.ok(ApiResponse.okMessage("리셋 코드 전송 완료"));
    }

    //비밀번호 재설정 완료
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody  PasswordResetRequestDto dto){
        authService.resetPassword(dto);
        return ResponseEntity.ok(ApiResponse.okMessage("비번 재설정 완료"));
    }

    //탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(Principal principal, @RequestBody DeleteAccountRequestDto dto){
        Long userId = Long.parseLong(principal.getName());
        authService.deleteAccount(userId, dto.getPassword());

        return ResponseEntity.ok(ApiResponse.okMessage("회원 탈퇴가 완료되었습니다."));
    }

    //탈퇴 검증
    @PostMapping("/check-password")
    public ResponseEntity<?> checkPassword(Principal principal, @RequestBody DeleteAccountRequestDto dto){
        Long userId = Long.parseLong(principal.getName());

        boolean matches = authService.checkPassword(userId, dto.getPassword());
        if(!matches){
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.okMessage("비밀번호가 일치하지 않습니다."));
        }
        return ResponseEntity.ok(ApiResponse.okMessage("탈퇴 고!"));
    }
}
