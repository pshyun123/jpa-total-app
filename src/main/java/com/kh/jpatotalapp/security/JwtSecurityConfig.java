package com.kh.jpatotalapp.security;

import com.kh.jpatotalapp.jwt.JwtFilter;
import com.kh.jpatotalapp.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 왜!!! 어떤 보안 설정을 담당하나요? -> jwtfilter에 대한 설정을 여기서 해줌 여기서 설정해준 값에 따라 jwtfilter가 바뀜
// SecurityConfigurerAdapter를 상속받아서 만들어진 JWT 기반의 보안 설정을 담당하는 클래스
@RequiredArgsConstructor//클래스에 필요한 생성자를 자동 생성.
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    //DefaultSecurityFilterChain은 보안 필터 체인을 나타내고, HttpSecurity는 해당 보안 구성이 어떤 HTTP 보안 설정을 변경할지를 나타냄
    //Spring Security의 구성을 확장, 사용자 정의 보안 설정 추가가능.

    private final TokenProvider tokenProvider;
    // TokenProvider를 주입받아 JwtSecurityConfig 인스턴스를 생성하는 생성자


    //configure(HttpSecurity http): 보안 설정을 구성하는 메서드, JWT를 사용하기 위한 JwtFilter 객체를 생성하고,
    // 이를 UsernamePasswordAuthenticationFilter 앞에 추가함으로써 JWT 기반의 인증을 구현하고 보안 설정을 완성

    // HttpSecurity 설정을 구성하는 메서드
    @Override
    public void configure(HttpSecurity http) {
        JwtFilter customFilter = new JwtFilter(tokenProvider); // JwtFilter 객체 생성, 토큰제공자와 함께 초기화
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        // JwtFilter를 UsernamePasswordAuthenticationFilter 전에 추가해서 보안설정 구성
    }
}