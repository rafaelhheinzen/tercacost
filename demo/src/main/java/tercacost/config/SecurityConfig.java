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
                    
                    // 🌟 ADICIONE ESTA LINHA: Garante que as requisições GET de leitura de projetos passem sem travas se autenticadas via Basic Auth
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
}
