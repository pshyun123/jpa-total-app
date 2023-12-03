package com.kh.jpatotalapp.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//토큰 만료 되었을 때 401에러!!
@Component// 클래스를 빈으로 등록 // 인증 실패시 401 에러 리턴할 클래스
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException{
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);// 인증에 실패하면 401 Unauthorized 에러를 리턴
    }
}
