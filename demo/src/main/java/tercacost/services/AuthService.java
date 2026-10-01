package tercacost.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tercacost.entities.Usuario;
import tercacost.repositories.UsuarioRepository;

@Service
// 🌟 ADICIONADO: Implementa UserDetailsService para o Spring Security saber validar o login
public class AuthService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeta o criptografador definido no SecurityConfig

    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado.");
        }
        usuario.setCriadoEm(java.time.LocalDateTime.now()); 
        
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            usuario.setLogin(usuario.getEmail());
        }

        // 🌟 SEGURANÇA: Criptografa a senha antes de salvar na coluna senha_hash do MySQL
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // 🌟 SEGURANÇA: Compara a senha digitada com o hash criptografado do banco
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta.");
        }

        return usuario;
    }

    // 🌟 MÉTODO MANDATÓRIO DO SPRING SECURITY: Carrega o usuário do banco pelo e-mail
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha()) // Retorna o hash da senha para o Spring validar
                .roles("USER")
                .build();
    }
}
