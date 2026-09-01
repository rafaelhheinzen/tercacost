package tercacost.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
 // Procure por esta linha no Usuario.java do pacote entities e mude para:
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senha;

    private String login;

    public Usuario() {}

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    
    
    public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}



	@Column(name = "criado_em", nullable = false, updatable = false)
    private java.time.LocalDateTime criadoEm;
    
    
    public java.time.LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(java.time.LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @PrePersist
    protected void onCreate() {
        this.criadoEm = java.time.LocalDateTime.now();
    }




}