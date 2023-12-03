package com.kh.jpatotalapp.service;

import com.kh.jpatotalapp.dto.MemberReqDto;
import com.kh.jpatotalapp.dto.MemberResDto;
import com.kh.jpatotalapp.dto.TokenDto;
import com.kh.jpatotalapp.entity.Member;

import com.kh.jpatotalapp.entity.RefreshToken;
import com.kh.jpatotalapp.jwt.TokenProvider;
import com.kh.jpatotalapp.repository.MemberRepository;
import com.kh.jpatotalapp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;



//회원 가입과 로그인 기능을 처리
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManagerBuilder managerBuilder; // 인증을 담당하는 클래스
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public MemberResDto signup(MemberReqDto requestDto) { // 회원가입
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }
        Member member = requestDto.toEntity(passwordEncoder);
        return MemberResDto.of(memberRepository.save(member));
    }
    public TokenDto login(MemberReqDto requestDto) { // 로그인
//        try {
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();
        log.info("authenticationToken: {}", authenticationToken);

        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication: {}", authentication);// AuthenticationManager를 사용하여 인증 수행

//        // TokenProvider를 사용하여 토큰 생성
//        return tokenProvider.generateTokenDto(authentication);



        // 추가수정 > 로그인할 때마다 새로운 Refresh Token을 발급, 해당 토큰을 DB에 저장
        // 이전에 발급된 Refresh Token은 무효화되며, 사용자는 새로운 Refresh Token을 받아 새로운 세션을 유지
        TokenDto token = tokenProvider.generateTokenDto(authentication); //TokenDto에는 Access Token과 Refresh Token이 포함

        // refreshToKen DB에 저장
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()->new RuntimeException("존재하지 않는 이메일입니다."));

        // 이미 db에 해당 계정으로 저장된 refreshToken 정보가 있다면 삭제
        log.info("Exists by member: {}", refreshTokenRepository.existsByMember(member));//사용자 이메일로 DB에서 해당 사용자를 조회
        if(refreshTokenRepository.existsByMember(member)) {
            refreshTokenRepository.deleteByMember(member);
        }

        //설명 필요!!!
        //새로운 RefreshToken을 생성하고, 이를 현재 사용자와 연결합니다.
        // 약간 특이한데, encodedToken.concat("=")를 통해 이전에 생성된 Refresh Token을 가져와서 = 문자열을 덧붙입니다.
        // 이는 특정한 이유로 인코딩이 적용된 경우를 고려한 조치로 보입니다.
        RefreshToken refreshToken = new RefreshToken();
        String encodedToken = token.getRefreshToken();
        refreshToken.setRefreshToken(encodedToken.concat("="));
        refreshToken.setRefreshTokenExpiresIn(token.getRefreshTokenExpiresIn());
        refreshToken.setMember(member);

        refreshTokenRepository.save(refreshToken); // DB에 새 RefreshToken 저장

        return token;

    }

    // refreshToken 검증
    public TokenDto refreshAccessToken(String refreshToken) {
        try{
            if (tokenProvider.validateToken(refreshToken)){
                return tokenProvider.generateTokenDto(tokenProvider.getAuthentication(refreshToken));
            }
        }catch (RuntimeException e) {
            log.error("토큰 유효성 검증 중 예외발생 : {}", e.getMessage());
        }
        return null;
    }
}












