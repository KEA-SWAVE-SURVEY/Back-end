package com.example.demo.util.OAuth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
public class OAuthConfig {
    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        http.oauth2Login()
                .authorizationEndpoint()
                .baseUri("/login");

        return http.build();
    }
}
