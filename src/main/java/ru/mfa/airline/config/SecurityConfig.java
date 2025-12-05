package ru.mfa.airline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/flights/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/airports/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/aircrafts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/passengers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/cancel").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/flights").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/airports").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/airports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/airports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/aircrafts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/aircrafts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/aircrafts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/passengers").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/passengers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/passengers/**").hasRole("ADMIN")
                        .requestMatchers("/api/bookings/**").hasRole("ADMIN")
                        .requestMatchers("/api/airline/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
