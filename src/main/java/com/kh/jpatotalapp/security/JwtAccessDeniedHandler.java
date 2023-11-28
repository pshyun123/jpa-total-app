package com.kh.jpatotalapp.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//AccessDeniedHandler 인터페이스를 구현한 클래스, 권한이 없는 경우의 처리를 담당
@Component // 스프링 컨텍스트에 Bean으로 등록
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override// AccessDeniedHandler 인터페이스를 구현한 메서드 재정의
    // 권한이 없는 경우 403 Forbidden 에러를 클라이언트로 전송
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN); // 권한이 없으면 403 Forbidden 에러를 리턴
    }
}