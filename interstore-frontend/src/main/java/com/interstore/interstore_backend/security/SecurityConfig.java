package com.interstore.interstore_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //autenticazione
                        .requestMatchers("/api/auth/**").permitAll()

                        //prodotti
                        .requestMatchers(HttpMethod.GET, "/api/prodotti/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/prodotti/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/prodotti/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/prodotti/**").hasRole("ADMIN")

                        //carrello:accessibile solo agli utenti autenticati
                        .requestMatchers(HttpMethod.GET, "/api/cart/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/cart/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/cart/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/**").hasRole("USER")

                        //test
                        .requestMatchers("/api/test/user").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/test/admin").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
