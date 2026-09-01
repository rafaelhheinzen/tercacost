package tercacost.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "ProjetoModel") 
@Table(name = "projetos")
public class Projeto {
	

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Cobertura> coberturas;

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O MySQL cuidará dos IDs automaticamente
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private tercacost.entities.Usuario usuario;


    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public tercacost.entities.Usuario getUsuario() { 
        return usuario; 
    }
    
    public void setUsuario(tercacost.entities.Usuario usuario) { 
        this.usuario = usuario; 
    }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
