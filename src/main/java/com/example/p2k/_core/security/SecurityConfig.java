package com.example.p2k._core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration // spring security filter(SecurityConfig)가 스프링 필터체인에 등록된다.
@EnableMethodSecurity
public class    SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.headers().frameOptions().sameOrigin(); // h2-console 접속위해서 필요한 설정
        httpSecurity.csrf(
                        AbstractHttpConfigurer::disable
                )
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"),
                                                new AntPathRequestMatcher("/css/**"),
                                                new AntPathRequestMatcher("/js/**"),
                                                new AntPathRequestMatcher("/assets/**"),
                                                new AntPathRequestMatcher("/"),
                                                new AntPathRequestMatcher("/favicon.ico"),
                                                new AntPathRequestMatcher("/resources/**"),
                                                new AntPathRequestMatcher("/error"),
                                                new AntPathRequestMatcher("/@sweetalert2"),
                                                new AntPathRequestMatcher("/user/check-email/**"),
                                                new AntPathRequestMatcher("/user/login"),
                                                new AntPathRequestMatcher("/user/join")).permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        formlogin -> formlogin
                                .loginPage("/user/login")
                                .loginProcessingUrl("/user/login")
                                .usernameParameter("email")
                                .defaultSuccessUrl("/home", true)
                                .permitAll()
                )
                .logout(
                        Logout -> Logout
                                .logoutUrl("/user/logout")
                                .logoutSuccessUrl("/")
                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .loginPage("/user/login")
                                .defaultSuccessUrl("/home", true)
                                .userInfoEndpoint()
                                .userService(customOAuth2UserService)
                );

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
