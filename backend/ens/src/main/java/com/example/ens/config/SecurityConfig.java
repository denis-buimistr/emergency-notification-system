package com.example.ens.config;

import com.example.ens.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())

                .authorizeHttpRequests(auth -> auth

                        // HTML + статика
                        .requestMatchers(
                                "/", "/index.html",
                                "/auth.html", "/map.html", "/admin.html",
                                "/css/**", "/js/**", "/images/**", "/static/**"
                        ).permitAll()

                        // AUTH API — полностью разрешено
                        .requestMatchers("/api/auth/**").permitAll()

                        // Google auth
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()

                        // админская зона — только ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // остальные API требуют любой токен
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().permitAll()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.getWriter().write("Unauthorized");
                        })
                )

                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/api/auth/google/success", true)
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
