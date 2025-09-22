package br.sp.gov.fatec.ubs.backend.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Value("${spring.security.enabled}")
    private boolean securityEnabled;

    private final JwtAuthenticationFilter jwtRequestFilter;

    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        if (securityEnabled) {
            http
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth", "/error", "/auth/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    );
        } else {
            http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable());
        }
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedOriginPatterns(List.of("http://localhost:4200", "https://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}