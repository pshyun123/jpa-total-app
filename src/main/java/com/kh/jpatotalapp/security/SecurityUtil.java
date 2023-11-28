package com.kh.jpatotalapp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


//SecurityContextHolder를 사용하여 현재 인증된 사용자의 정보를 가져오는 유틸리티 클래스
public class SecurityUtil {
    private SecurityUtil(){ // 외부에서 인스턴스 생성을 막기 위한 private 생성자

    }
    // Security Context의 Authentication 객체를 이용해 '회원의 정보 가져옴'
    // 컨트롤러가 이녀석을 부름
        public static Long getCurrentMemberId() {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
            }
            return Long.parseLong(authentication.getName());
        }

    // Security Context의 Authentication 객체를 이용해 회원 이메일 가져옴
        public static String getCurrentMemberEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
            }
            return authentication.getName();
        }


    // Security Context의 Authentication 객체를 이용해 회원 이름 가져옴
    public static String getCurrentMemberName() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }
        return authentication.getPrincipal().toString();
    }
}

