package br.com.avaliacaocurricular.config;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.Assert;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/admin/entrar").permitAll()
                        .requestMatchers("/admin/**").hasRole("COORDENACAO")
                        .anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/admin/entrar")
                        .loginProcessingUrl("/admin/entrar")
                        .defaultSuccessUrl("/admin/dashboard")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/admin/sair")
                        .logoutSuccessUrl("/admin/entrar?logout"))
                .headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; base-uri 'self'; form-action 'self'; "
                                + "img-src 'self' data:; frame-ancestors 'none'; object-src 'none'")))
                .build();
    }

    @Bean
    Pbkdf2PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    InMemoryUserDetailsManager coordenacao(
            @Value("${app.admin.username}") String username,
            @Value("${app.admin.password}") String password,
            Pbkdf2PasswordEncoder encoder) {
        Assert.hasText(username, "Informe APP_ADMIN_USERNAME para a conta da coordenação.");
        Assert.hasText(password, "Informe APP_ADMIN_PASSWORD para a conta da coordenação.");
        return new InMemoryUserDetailsManager(User.withUsername(username)
                .password(encoder.encode(password))
                .roles("COORDENACAO")
                .build());
    }
}
