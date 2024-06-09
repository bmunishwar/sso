package com.example.ssodemo.config;

import com.example.ssodemo.client.CustomTokenResponseClient;
import com.example.ssodemo.handler.OAuth2AuthenticationFailureHandler;
import com.example.ssodemo.handler.OAuth2AuthenticationSuccessHandler;
import com.example.ssodemo.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomTokenResponseClient customTokenResponseClient;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final OAuth2AuthenticationFailureHandler failureHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomTokenResponseClient customTokenResponseClient,
                          OAuth2AuthenticationSuccessHandler successHandler,
                          OAuth2AuthenticationFailureHandler failureHandler,
                          ClientRegistrationRepository clientRegistrationRepository,
                          CustomOAuth2UserService customOAuth2UserService) {
        this.customTokenResponseClient = customTokenResponseClient;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/", "/login", "/css/**", "/js/**", "/favicon.ico").permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .loginPage("/login")
                    .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint
                            .authorizationRequestResolver(new PKCEAuthorizationRequestResolver(clientRegistrationRepository))
                    )
                    .tokenEndpoint(tokenEndpoint ->
                        tokenEndpoint
                            .accessTokenResponseClient(customTokenResponseClient)
                    )
                    .userInfoEndpoint(userInfoEndpoint ->
                        userInfoEndpoint
                            .userService(customOAuth2UserService)
                    )
                    .successHandler(successHandler)
                    .failureHandler(failureHandler)
            )
            .logout(logout -> 
                logout
                    .logoutUrl("/logout")
                    .addLogoutHandler(new SecurityContextLogoutHandler())
                    .logoutSuccessUrl("/")
            );
        return http.build();
    }
}
