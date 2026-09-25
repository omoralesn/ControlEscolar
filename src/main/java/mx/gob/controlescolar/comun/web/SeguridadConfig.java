package mx.gob.controlescolar.comun.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import mx.gob.controlescolar.acceso.web.EntradaSeguridad;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SeguridadConfig {

    private final EntradaSeguridad entrada;

    @Bean
    SecurityFilterChain filtro(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/entrar", "/css/**", "/actuator/health").permitAll()
                        .requestMatchers("/planes/asignaturas").hasRole("PLATAFORMA")
                        .requestMatchers("/acceso", "/acceso/**", "/escuelas", "/escuelas/**", "/checklist")
                        .hasRole("SUPER")
                        .requestMatchers("/padres", "/padres/**").hasRole("TUTOR")
                        .requestMatchers("/panel").authenticated()
                        .anyRequest().hasAnyRole("ESCUELA", "SUPER", "PLATAFORMA"))
                .formLogin(form -> form
                        .loginPage("/entrar")
                        .successHandler(entrada)
                        .failureHandler(entrada)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/entrar"))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
