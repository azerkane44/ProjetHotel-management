package com.example.springhotel.configuration;

import com.example.springhotel.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final MyUserDetailsService userDetailsService;

    public SecurityConfig(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // ❌ Pas de CSRF pour une API
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ CORS activé (config globale)
                .cors(cors -> {})

                .authorizeHttpRequests(auth -> auth
                        // ✅ Préflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ Auth & register
                        .requestMatchers("/api/v1/login", "/api/v1/register").permitAll()

                        // ✅ ADMIN (temporairement ouvert)
                        .requestMatchers("/api/admin/**").permitAll()

                        // ✅ API publique
                        .requestMatchers("/api/**").permitAll()

                        // 🔒 le reste (plus tard)
                        .anyRequest().permitAll()
                )

                // ❌ PAS de formLogin pour React
                .formLogin(AbstractHttpConfigurer::disable)

                // ❌ PAS de logout HTML
                .logout(AbstractHttpConfigurer::disable)

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder
    ) throws Exception {

        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
