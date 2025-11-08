package com.budgie.server.service;

import com.budgie.server.dto.*;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.UserRepository;
import com.budgie.server.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final KakaoLoginService kakaoLoginService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NaverLoginService naverLoginService;

    @Transactional
    public AuthResponseDto kakaoLogin(String code){
        log.info("카카오 로그인 요청 코드 수신:{}", code);
        KakaoTokenResponseDto kakaoToken = kakaoLoginService.getKakaoAccessToken(code);
        KakaoUserInfoResponseDto userInfo = kakaoLoginService.getKakaoUserInfo(kakaoToken.getAccessToken());

        String userEmail = userInfo.getKakao_account().getEmail();
        String userNickname = userInfo.getKakao_account().getProfile().getNickname();

        UserEntity loginUser = authService.findOrCreateUser(userEmail, userNickname);

        Long userId = loginUser.getUserId();

        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        return AuthResponseDto.builder()
                .email(userEmail)
                .nickname(userNickname)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(jwtProvider.getGrantType())
                .accessTokenExpiresIn(jwtProvider.getAccessTokenExpirationTime())
                .build();
    }

    //네이버
    @Transactional
    public AuthResponseDto naverLogin(String code, String state){
        //네이버 엑세스 토큰 획득
        NaverTokenResponseDto naverToken = naverLoginService.getNaverAccessToken(code, state);

        //토큰에서 사용자 정보 가져오기
        NaverUserInfoResponseDto userInfo = naverLoginService.getNaverUserInfo(naverToken.getAccessToken());

        //사용자 정보 디비 저장, 로그인
        String userEmail = userInfo.getResponse().getEmail();
        String userNickname = userInfo.getResponse().getNickname();

        UserEntity loginUser = authService.findOrCreateUser(userEmail, userNickname);

        Long userId = loginUser.getUserId();

        //자체토큰 설정
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        return AuthResponseDto.builder()
                .email(userEmail)
                .nickname(userNickname)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(jwtProvider.getGrantType())
                .accessTokenExpiresIn(jwtProvider.getAccessTokenExpirationTime())
                .build();
    }

}
