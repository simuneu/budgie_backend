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
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

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
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        }catch (RuntimeException e){
            log.error("회원 가입 실패:{}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto requestDto){
        try{
            AuthResponseDto responseDto   = authService.login(requestDto);
            return ResponseEntity.ok().body(responseDto);
        }catch (Exception e){
            ResponseDto responseDto = ResponseDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.status(401).body(responseDto);
        }
    }

    //카카오 소셜 로그인
    @GetMapping("/kakao/loginstart")
    public RedirectView redirectToKakao() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code";
        log.info("카카오 로그인 리다이렉트: {}", kakaoAuthUrl);
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


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody LogoutRequestDto requestDto){
        Long userId = requestDto.getUserId();
        try{
            log.info("로그아웃 요청 수신. userId:{}", userId);
            authService.logout(userId);
            ResponseDto responseDto = ResponseDto.builder()
                    .message("로그아웃 성공")
                    .build();
            return ResponseEntity.ok().body(responseDto);
        }catch (Exception e){
            log.error("로그아웃 중 오류 발생 :"+e.getMessage());
            ResponseDto responseDto = ResponseDto.builder()
                    .error("로그아웃 처리 실패: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    //토큰 갱신 -access+refresh발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequestDto requestDto){
        String refreshToken = requestDto.getRefreshTokenDto();
        try{
            log.info("access token 갱신 요청 수신");
            AuthResponseDto responseDto = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok().body(responseDto);
        } catch (RuntimeException e) {
            log.error("토큰 갱신 실패"+e.getMessage());
            ResponseDto responseDto = ResponseDto.builder()
                    .error("토큰 갱신 실패"+e.getMessage())
                    .build();
            //401/403
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        }
    }
}
