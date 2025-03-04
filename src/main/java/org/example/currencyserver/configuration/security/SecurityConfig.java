package org.example.currencyserver.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String CSP = "script-src 'nonce-{RANDOM}'; object-src 'none'; base-uri 'none';";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(Customizer.withDefaults())
                .headers(headers -> headers.contentSecurityPolicy(
                        contentSecurityPolicyConfig -> contentSecurityPolicyConfig.policyDirectives(CSP)));
        return http.build();
    }
}
