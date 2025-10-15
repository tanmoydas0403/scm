package com.cm.config;

import com.cm.service.impl.SecurityCustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthFailureHandler authFailureHandler;

    @Autowired
    private SecurityCustomUserDetailsService detailsService;

    @Autowired
    private  OAuthAuthenticationSuccessHandler handler;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(detailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable) // Temporarily disable CSRF for development
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/api/**").authenticated() // Protect API endpoints // Pages under /user require authentication
                        .anyRequest().permitAll() // All other requests are permitted
                )
                .formLogin(formLogin -> {
                    formLogin.loginPage("/login")
                            .loginProcessingUrl("/authenticate")
                            .successForwardUrl("/user/dashboard")
                            //.failureForwardUrl("/login?error=true")
                            .usernameParameter("email")
                            .passwordParameter("password")
                            .failureHandler(authFailureHandler);
                });

                

        http.logout(logoutFrom ->{
            logoutFrom.logoutUrl("/do-logout")
            .logoutSuccessUrl("/login?logout=true");
        });

        http.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(handler);
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
