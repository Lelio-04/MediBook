package it.unisa.medibook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disabilitiamo CSRF (non serve per queste chiamate API semplici)
                .csrf(csrf -> csrf.disable())
                // Configuriamo CORS per accettare React
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Autorizziamo le richieste
                .authorizeHttpRequests(auth -> auth
                        // Permettiamo a TUTTI di accedere alle API di login e registrazione
                        .requestMatchers("/api/**").permitAll()
                        // Qualsiasi altra richiesta richiederebbe autenticazione (ma per ora lasciamo libero /api/**)
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // Configurazione esplicita per dire "Sì, accetta localhost:3000"
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permetti al frontend React di accedere
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        // Permetti i metodi GET, POST, PUT, DELETE, OPTIONS
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permetti tutti gli header
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}