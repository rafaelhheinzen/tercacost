package tercacost.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desativa CSRF para aceitar requisições do Front-end
            .cors(Customizer.withDefaults()) // Ativa as configurações de CORS
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/api/calculo/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/projetos/**").permitAll() 
                    .anyRequest().authenticated()
                )
            .httpBasic(Customizer.withDefaults()); // Ativa a autenticação básica para validação

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Define o padrão de criptografia seguro de senhas
    }

    // 🌟 ADICIONE ESTE BLOCO ABAIXO: Ele diz ao Spring quais domínios externos podem acessar sua API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permite requisições vindas do seu domínio no GitHub Pages
        configuration.setAllowedOrigins(List.of(
    "https://rafaelhheinzen.github.io/tercacost",
    "http://localhost:5500",
    "http://127.0.0.1:5500"
));
        
        // Permite os métodos HTTP mais comuns
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permite os cabeçalhos padrão enviados pelo fetch do JavaScript
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        
        // Permite o envio de cookies ou credenciais caso precise futuramente
        configuration.setAllowCredentials(true);

        
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica essa regra em todas as rotas da API
        return source;
    }
}
