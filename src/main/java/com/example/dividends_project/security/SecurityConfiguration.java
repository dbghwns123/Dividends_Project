package com.example.dividends_project.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter authenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/**/signup", "/**/signin").permitAll()
                .and()
                .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/h2-console/**");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration {
//
//    private final JwtAuthenticationFilter authenticationFilter;
//
//    public SecurityConfiguration(JwtAuthenticationFilter authenticationFilter) {
//        this.authenticationFilter = authenticationFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .httpBasic(httpBasic -> httpBasic.disable()) // 기본 인증 비활성화
//                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/signup", "/auth/signin").permitAll() // 회원가입, 로그인 요청 허용
//                        .requestMatchers("/h2-console/**").permitAll() // H2-console 허용
//                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
//                )
//                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // H2-console 허용 설정
//                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//}




