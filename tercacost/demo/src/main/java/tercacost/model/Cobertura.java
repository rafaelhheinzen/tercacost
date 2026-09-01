package tercacost.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coberturas")
public class Cobertura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @OneToMany(mappedBy = "cobertura", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PerfilTerca> perfis;

    
    @ManyToOne(cascade = CascadeType.ALL) // Adicionado CascadeType.ALL
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;


    private double comprimentoTerca;
    private double espacamentoTercas;
    private double Lb;
    private double cargaTelha;
    private double cargaPainelSolar;
    private double sobrecargaPadrao;
    private double ventoPressao;

    @Column(name = "dataCalculo", nullable = false, updatable = false)
    private LocalDateTime dataCalculo = LocalDateTime.now();
    

    // =========================================================================
    // --- GETTERS E SETTERS MANDATÓRIOS (Adicione ou gere este bloco abaixo) ---
    // =========================================================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public double getComprimentoTerca() { return comprimentoTerca; }
    public void setComprimentoTerca(double comprimentoTerca) { this.comprimentoTerca = comprimentoTerca; }

    public double getEspacamentoTercas() { return espacamentoTercas; }
    public void setEspacamentoTercas(double espacamentoTercas) { this.espacamentoTercas = espacamentoTercas; }

    public double getLb() { return Lb; }
    public void setLb(double Lb) { this.Lb = Lb; }

    public double getCargaTelha() { return cargaTelha; }
    public void setCargaTelha(double cargaTelha) { this.cargaTelha = cargaTelha; }

    public double getCargaPainelSolar() { return cargaPainelSolar; }
    public void setCargaPainelSolar(double cargaPainelSolar) { this.cargaPainelSolar = cargaPainelSolar; }

    public double getSobrecargaPadrao() { return sobrecargaPadrao; }
    public void setSobrecargaPadrao(double sobrecargaPadrao) { this.sobrecargaPadrao = sobrecargaPadrao; }

    public double getVentoPressao() { return ventoPressao; }
    public void setVentoPressao(double ventoPressao) { this.ventoPressao = ventoPressao; }

    public LocalDateTime getDataCalculo() { return dataCalculo; }
    public void setDataCalculo(LocalDateTime dataCalculo) { this.dataCalculo = dataCalculo; }
}
