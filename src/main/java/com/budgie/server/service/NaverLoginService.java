package com.budgie.server.service;

import com.budgie.server.dto.NaverTokenResponseDto;
import com.budgie.server.dto.NaverUserInfoResponseDto;
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
public class NaverLoginService {
    private final RestTemplate restTemplate;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.uri.redirect}")
    private String redirectUri;

    @Value("${naver.uri.token.url}")
    private String tokenUri;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.uri.user.info.url}")
    private String userInfoUri;

    public NaverTokenResponseDto getNaverAccessToken(String code, String state){

        log.debug("네이버 토큰 요청 시작: code={}, state={}", code, state);
        log.debug(" 네이버 redirect_uri = {}", redirectUri);

        //헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //바디
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("state", state);

        log.debug(" 네이버 토큰 요청 Body = {}", body);

        //http entity
        HttpEntity<MultiValueMap<String, String>>request = new HttpEntity<>(body, headers);
        //rest template post요청
        ResponseEntity<NaverTokenResponseDto> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                request,
                NaverTokenResponseDto.class
        );

        log.debug(" 네이버 토큰 RAW 응답 = {}", response);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            log.debug(" 네이버 accessToken = {}", response.getBody().getAccessToken());
            return response.getBody();
        }
        throw new RuntimeException("네이버 Access Token 획득 실패");
    }
    public NaverUserInfoResponseDto getNaverUserInfo(String NaverAccessToken) {
        log.debug(" 네이버 유저 정보 요청 AccessToken = {}", NaverAccessToken);

        //헤더 설정 (토큰 추가)
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + NaverAccessToken);

        //HTTP 엔터티 생성 (헤더만)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // resttemplate으로 get요청
        ResponseEntity<NaverUserInfoResponseDto> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                entity,
                NaverUserInfoResponseDto.class
        );
        log.debug("네이버 유저 정보 RAW 응답 = {}", response);


        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            log.debug(" 네이버 유저 정보 획득 성공: email = {}", response.getBody().getResponse().getEmail());

            return response.getBody();
        }
        throw new RuntimeException("네이버 사용자 정보 획득 실패");
    }
}
