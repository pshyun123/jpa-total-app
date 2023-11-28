package com.kh.jpatotalapp.jwt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//들어온 토큰 검증
//인증된 사용자만 접근할 수 있도록 모든 HTTP 요청에 대해 JWT 토큰의 유효성을 검사, 유효한 경우 해당 토큰에 대한 인증 정보를 설정
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { //한 번만 실행되는 필터를 구현하는 클래스.
    // AUTHORIZATION_HEADER와 BEARER_PREFIX: 헤더의 JWT 토큰을 추출하기 위한 상수 값
    // AUTHORIZATION_HEADER는 헤더 이름, BEARER_PREFIX는 JWT 토큰 앞에 붙는 접두사
    public static final String AUTHORIZATION_HEADER = "Authorization";// 토큰을 요청. 헤더의 Authorization 키에 담아서 전달
    public static final String BEARER_PREFIX = "Bearer "; // 토큰 앞에 붙는 문자열

    private final TokenProvider tokenProvider; // 토큰 생성 및 검증 담당하는 TokenProvider

    private String resolveToken(HttpServletRequest request) {//HttpServletRequest에서 Authorization 헤더를 통해 JWT 토큰을 추출하는 메서드
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); //헤더에서 토큰 꺼내오기
        if (bearerToken !=null && bearerToken.startsWith(BEARER_PREFIX)){// 토큰이 존재하고, 토큰 앞에 지정된 문자열이 존재하면
            return bearerToken.substring(7); // 토큰 앞 문자열 제거하고 토큰 반환
        }
        return null;
    }

    // doFilterInternal: 실제 필터링 작업을 수행하는 메서드
    // JWT 토큰의 유효성을 검사, 유효한 경우 해당 토큰으로부터 인증 정보를 추출하여 SecurityContextHolder에 설정하는 역할을 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        // HTTP 요청에서 JWT 토큰 추출
        String jwt = resolveToken(request);
        // 추출한 토큰이 비어있지 않고, 토큰이 유효한 경우
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
            // 토큰을 사용하여 Authentication 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            // SecurityContextHolder에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
