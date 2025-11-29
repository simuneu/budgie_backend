package com.budgie.server.service;

import com.budgie.server.dto.KakaoTokenResponseDto;
import com.budgie.server.dto.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final RestTemplate restTemplate;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    public KakaoTokenResponseDto getKakaoAccessToken(String code){
        String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

        log.info(" 카카오 토큰 요청 시작 — 인가 코드 = {}", code);
        log.info(" redirect_uri = {}", redirectUri);

        //헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //바디
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        log.info(" 카카오 토큰 요청 Body = {}", body);

        //http entity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenResponseDto> response = restTemplate.exchange(
                KAKAO_TOKEN_URL,
                HttpMethod.POST,
                request,
                KakaoTokenResponseDto.class
        );
        log.info(" 카카오 토큰 RAW 응답 = {}", response);
        if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
            log.info(" 카카오 accessToken = {}", response.getBody().getAccessToken());
            log.info(" 카카오 refreshToken = {}", response.getBody().getRefreshToken());
            return response.getBody();
        }
        throw new RuntimeException("카카오 access token 획득 실패"+response.getStatusCode());
    }

    public KakaoUserInfoResponseDto getKakaoUserInfo(String kakaoAccessToken){
        String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
        log.info(" 카카오 유저 정보 요청 AccessToken = {}", kakaoAccessToken);

        //헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);

        //http엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //rest template - get요청
        ResponseEntity<KakaoUserInfoResponseDto> response = restTemplate.exchange(
                KAKAO_USER_INFO_URL,
                HttpMethod.GET,
                entity,
                KakaoUserInfoResponseDto.class
        );
        log.info(" 카카오 유저 정보 RAW 응답 = {}", response);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            log.info(" 카카오 유저 정보 획득 성공 userId = {}", response.getBody().getId());
            return response.getBody();
        }
        throw new RuntimeException("카카오 사용자 정보 획득 실패");
    }
}
