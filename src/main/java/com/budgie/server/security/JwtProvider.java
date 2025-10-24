package com.budgie.server.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret.key}") //application.properties
    private String SECRET_KEY;

    @Value("${jwt.expiration.time}")
    private long EXPIRATION_TIME;

    @Value("${jwt.refresh.expiration.time}")
    private long REFRESH_EXPIRATION_TIME;

    private static final String BEARER_TYPE = "Bearer";

    private final Map<SecretKey, JwtParser> parserCache = new ConcurrentHashMap<>();

    //시크릿 키 디코딩으로 key객체 변환
    private SecretKey getSigningKey(){
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //jwt parser로 인스턴스 캐싱해서 반환
    private JwtParser getParser(){
        SecretKey key = getSigningKey();
        return parserCache.computeIfAbsent(key, k-> Jwts.parser()
                .verifyWith(k)
                .build());
    }

    //access토큰 생성
    public String createAccessToken(long userId){
        Date now = new Date();
        //만료 시간
        long expirationTimeMs = now.getTime()+ EXPIRATION_TIME;
        Date expiration = new Date(expirationTimeMs);

        return Jwts.builder()
                .signWith(getSigningKey(), Jwts.SIG.HS512) //알고리즘 키
                .setSubject(String.valueOf(userId))//토큰 사용자
                .setIssuer("budgie")//토큰 발급자
                .setIssuedAt(now)//발급 시간
                .setExpiration(expiration)//만료 시간
                .compact(); //생성, 직렬화
    }

    //refresh 토큰 생성
    public String createRefreshToken(long userId){
        Date now = new Date();
        long expirationTimeMs = now.getTime()+ REFRESH_EXPIRATION_TIME;
        Date expiration = new Date(expirationTimeMs);

       try{
          String token = Jwts.builder()
                  .signWith(getSigningKey(), Jwts.SIG.HS512)
                  .setSubject(String.valueOf(userId))
                  .setIssuer("budgie")
                  .setIssuedAt(now)
                  .setExpiration(expiration)
                  .compact();
          log.info("리프레시 토큰이 성공적으로 생성됨:{}", userId);
          return token;
       }catch (Exception e){
           log.error("리프레시 토큰을 생성하는 데 실패함 {} 이유는: {}", userId, e.getMessage(), e);
            return null;
       }
    }

    //jwt 토큰 검증, 사용자 id추출
    public String validateAndGetUserId(String token){
        try{
            Claims claims = getParser()
                    .parseSignedClaims(token)
                    .getPayload();
            log.info("토큰 검증 성공, 사용자 id:{}", claims.getSubject());
            return claims.getSubject();
        }catch (SecurityException | MalformedJwtException e ) {
            log.error("유효하지 않은 JWT 서명: {}", e.getMessage());
        }catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT 클레임 문자열이 비어 있음: {}", e.getMessage());
        }
        log.warn("토큰 검증 실패");
        return null;
    }

    //토큰 유효성 검증
    public boolean validateToken(String token){
        return validateAndGetUserId(token) != null;
    }

    //토큰에서 userId푸풀 후 객체 생성
    public Authentication getAuthentication(String token){
        Claims claims = getParser()
                .parseSignedClaims(token)
                .getPayload();
        String userId = claims.getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    //토큰에서 userId추출
    public Long getUserIdFromToken(String token){
        try{
            String userId = getParser()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Long.parseLong(userId);
        }catch (Exception e){
            log.error("토큰에서 사용자 정보를 추출하는 데 실패:{}", e.getMessage());
            throw new RuntimeException("토큰에서 사용자 정보를 추출하는 데 실패");
        }
    }
    public long getRefreshTokenExpirationTime(){
        return REFRESH_EXPIRATION_TIME;
    }

    public long getAccessTokenExpirationTime(){
        return EXPIRATION_TIME;
    }

    public String getGrantType(){
        return BEARER_TYPE;
    }

}
