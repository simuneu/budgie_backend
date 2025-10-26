package com.budgie.server.service;

import com.budgie.server.dto.AuthResponseDto;
import com.budgie.server.dto.KakaoTokenResponseDto;
import com.budgie.server.dto.KakaoUserInfoResponseDto;
import com.budgie.server.dto.TokenResponseDto;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.UserRepository;
import com.budgie.server.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public AuthResponseDto kakaoLogin(String code){
        KakaoTokenResponseDto kakaoToken = kakaoLoginService.getKakaoAccessToken(code);
        KakaoUserInfoResponseDto userInfo = kakaoLoginService.getKakaoUserInfo(kakaoToken.getAccessToken());

        String userEmail = userInfo.getKakao_account().getEmail();
        String userNickname = userInfo.getKakao_account().getProfile().getNickname();

        UserEntity loginUser = authService.findOrCreateUser(userEmail, userNickname);

        Long userId = loginUser.getUserId();

        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        Instant expiryDate = jwtProvider.getRefreshTokenExpiryDate();
        refreshTokenService.saveOrUpdate(loginUser, refreshToken, expiryDate);

        //응담시간 dto
        TokenResponseDto tokenInfo = TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(jwtProvider.getGrantType())
                .accessTokenExpiresIn(jwtProvider.getAccessTokenExpirationTime())
                .build();
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
