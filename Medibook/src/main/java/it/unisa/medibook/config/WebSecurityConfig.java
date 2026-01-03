package it.unisa.medibook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disabilitiamo il CSRF (bloccherebbe i form POST delle JSP)
                .csrf(csrf -> csrf.disable())

                // 2. Autorizzazioni: PERMETTIAMO TUTTO
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // 3. Disabilitiamo il form di login automatico di Spring (usiamo il tuo jsp)
                .formLogin(form -> form.disable())

                // 4. Disabilitiamo il logout automatico (lo gestiamo noi)
                .logout(logout -> logout.disable());

        return http.build();
    }
}