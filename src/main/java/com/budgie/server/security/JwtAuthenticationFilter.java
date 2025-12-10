package com.budgie.server.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.debug("AUTH HEADER = {}", request.getHeader("Authorization"));
        log.debug("REQUEST URI = {}", request.getRequestURI());

        String header = request.getHeader("Authorization");


        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);

            log.debug("######## 요청 URI: {}", request.getRequestURI());
            log.debug("########추출된 토큰: {}", token);

            try {
            if(jwtProvider.validateToken(token)){
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("########jwt인증 성공, userId:{}", authentication.getName());
            }else {
                log.warn("########jwt 검증 실패");
            }
        }catch(ExpiredJwtException e) {
                log.warn("######## 액세스 토큰 만료, 401 반환: {}", e.getMessage());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                    "{\"code\":\"TOKEN_EXPIRED\",\"message\":\"Access token expired\"}"
                );
                return;
            }
        }else{
            log.trace("########요청에 Authorization 헤더없음:{}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
