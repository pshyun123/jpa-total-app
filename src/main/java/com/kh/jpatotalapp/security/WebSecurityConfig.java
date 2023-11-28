package com.kh.jpatotalapp.security;

import com.kh.jpatotalapp.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Component

// CORS 정책을 허용하는 보안 설정을 구성
// 필터로 거름. 맨마지막에 생성.
public class WebSecurityConfig implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증 실패 시 처리할 클래스
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler; // 인가 실패 시 처리할 클래스
    @Bean // BCryptPasswordEncoder를 빈으로 등록,패스워드를 안전하게 암호화
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 객체를 Bean으로 등록
    }

    @Bean   // SecurityFilterChain 객체를 Bean으로 등록하여 Spring Security의 주요 설정 구성.
            // HttpSecurity를 통해 HTTP 요청에 대한 다양한 보안 설정을 정의, JwtSecurityConfig를 적용하여 JWT 인증 설정을 추가

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .httpBasic()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션을 쓰지 않아서 없어!
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**", "/ws/**", "/movies/**").permitAll()
                .antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception").permitAll()//보안 필요 없이 접근 가능한 애들. 컨트롤러 경로. **은 뒤에 무엇이 오든
                .anyRequest().authenticated()
                .and()
                .apply(new JwtSecurityConfig(tokenProvider)) // 토큰 프로바이더의 정보를 불러와서 토큰 생성!?????
                .and()
                .cors(); // .and().cors() 추가 된 부분
        // CORS 설정을 허용하여 다른 도메인에서의 요청도 허용.
        // addCorsMappings 메서드를 통해 허용할 도메인, 허용할 메서드 및 헤더, 인증 정보 허용 여부 등을 설정

        return http.build();

        //.httpBasic(): HTTP Basic 인증을 사용함을 설정합니다. 이는 클라이언트가 요청 시 베이직 인증 헤더를 제공해야 함을 의미합니다.
        //.csrf().disable(): CSRF(Cross-Site Request Forgery) 보호를 비활성화합니다. RESTful API에서는 일반적으로 CSRF를 사용하지 않습니다.
        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS): 세션 관리 정책을 설정합니다. STATELESS로 설정하여 서버에 세션을 생성하지 않고 상태를 유지하지 않도록 합니다.
        //.exceptionHandling(): 예외 처리를 설정합니다.
        //.authenticationEntryPoint(jwtAuthenticationEntryPoint): 인증 실패 시 처리할 JwtAuthenticationEntryPoint를 지정합니다.
        //.accessDeniedHandler(jwtAccessDeniedHandler): 인가 실패 시 처리할 JwtAccessDeniedHandler를 지정합니다.
        //.authorizeRequests(): 요청에 대한 권한을 설정합니다.
        //.antMatchers("/auth/**", "/ws/**", "/movies/**").permitAll(): 특정 URL 패턴에 대해 모든 사용자에게 접근을 허용합니다.
        //.antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception").permitAll(): Swagger 관련 리소스에 대해서도 모든 사용자에게 접근을 허용합니다.
        //.anyRequest().authenticated(): 나머지 모든 요청에 대해서는 인증된 사용자만 접근을 허용합니다.
        //.apply(new JwtSecurityConfig(tokenProvider)): JwtSecurityConfig를 적용합니다. 이는 JWT 관련 보안 설정을 추가하는데 사용되며, 앞에서 정의한 JwtFilter를 등록하여 JWT 인증을 처리합니다.
        //.cors(): CORS 설정을 허용하여 다른 도메인에서의 요청도 허용합니다. addCorsMappings 메서드를 통해 허용할 도메인, 허용할 메서드 및 헤더, 인증 정보 허용 여부 등을 설정합니다.
        //마지막으로, http.build()를 호출하여 SecurityFilterChain을 생성하여 반환합니다. 이를 통해 Spring Security의 설정이 적용된 보안 필터 체인이 생성됩니다.
    }
    @Override  // 메소드 오버라이딩, localhost:3000 번으로 들어오는 요청 허가
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

//@CrossOrigin(origins = CORS_ORIGIN) 컴트롤러에서 안하는 이유는 여기서 먼저 거쳐가기 때문이다.
//.cors();
//    @Override  // 메소드 오버라이딩, localhost:3000 번으로 들어오는 요청 허가
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000")
//                .allowedMethods("*")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }