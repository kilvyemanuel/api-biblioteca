package com.projetos.biblioteca.config;

import com.projetos.biblioteca.auth.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final HandlerExceptionResolver resolver;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtFilter = jwtFilter;
        this.resolver = resolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/exemplares").hasRole("BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.POST, "/categorias/**" , "/autores/**", "/livros/**").hasRole("BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.PUT, "/categorias/**", "/autores/**", "/livros/**").hasRole("BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/**", "/autores/**", "/livros/**").hasRole("BIBLIOTECARIO")
                        .requestMatchers("/livros/{livroId}/exemplares").hasRole("BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.GET, "/categorias/**", "/autores/**", "/livros/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, authException) -> {
                            resolver.resolveException(req, res, null, authException);
                        })
                        .accessDeniedHandler((req, res, accessDeniedException) -> {
                            resolver.resolveException(req, res, null, accessDeniedException);
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("BIBLIOTECARIO")
                .role("BIBLIOTECARIO").implies("MEMBRO")
                .build();
    }
}
