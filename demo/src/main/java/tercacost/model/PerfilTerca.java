package tercacost.model;

import jakarta.persistence.*;

@Entity
@Table(name = "perfis_tercas")
public class PerfilTerca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cobertura_id", nullable = false)
    private Cobertura cobertura;

    @Column(nullable = false)
    private String tipoPerfil; // Tratado como string básica mapeando com o ENUM do MySQL

    private double alturaAlma;
    private double larguraAba;
    private double larguraEnrijecedor;
    private double espessuraChapa;
    private double fy;
    private double fu;
    private double E;
    private double G;
    private double Cb;
    private double mrd;
    private double vrd;
    private double flechaReal;
    private double taxaMomento;
    private double taxaCortante;
    private double taxaFlecha;
    private boolean aprovado;
    
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Cobertura getCobertura() {
		return cobertura;
	}
	public void setCobertura(Cobertura cobertura) {
		this.cobertura = cobertura;
	}
	public String getTipoPerfil() {
		return tipoPerfil;
	}
	public void setTipoPerfil(String tipoPerfil) {
		this.tipoPerfil = tipoPerfil;
	}
	public double getAlturaAlma() {
		return alturaAlma;
	}
	public void setAlturaAlma(double alturaAlma) {
		this.alturaAlma = alturaAlma;
	}
	public double getLarguraAba() {
		return larguraAba;
	}
	public void setLarguraAba(double larguraAba) {
		this.larguraAba = larguraAba;
	}
	public double getLarguraEnrijecedor() {
		return larguraEnrijecedor;
	}
	public void setLarguraEnrijecedor(double larguraEnrijecedor) {
		this.larguraEnrijecedor = larguraEnrijecedor;
	}
	public double getEspessuraChapa() {
		return espessuraChapa;
	}
	public void setEspessuraChapa(double espessuraChapa) {
		this.espessuraChapa = espessuraChapa;
	}
	public double getFy() {
		return fy;
	}
	public void setFy(double fy) {
		this.fy = fy;
	}
	public double getFu() {
		return fu;
	}
	public void setFu(double fu) {
		this.fu = fu;
	}
	public double getE() {
		return E;
	}
	public void setE(double e) {
		E = e;
	}
	public double getG() {
		return G;
	}
	public void setG(double g) {
		G = g;
	}
	public double getCb() {
		return Cb;
	}
	public void setCb(double cb) {
		Cb = cb;
	}
	public double getMrd() {
		return mrd;
	}
	public void setMrd(double mrd) {
		this.mrd = mrd;
	}
	public double getVrd() {
		return vrd;
	}
	public void setVrd(double vrd) {
		this.vrd = vrd;
	}
	public double getFlechaReal() {
		return flechaReal;
	}
	public void setFlechaReal(double flechaReal) {
		this.flechaReal = flechaReal;
	}
	public double getTaxaMomento() {
		return taxaMomento;
	}
	public void setTaxaMomento(double taxaMomento) {
		this.taxaMomento = taxaMomento;
	}
	public double getTaxaCortante() {
		return taxaCortante;
	}
	public void setTaxaCortante(double taxaCortante) {
		this.taxaCortante = taxaCortante;
	}
	public double getTaxaFlecha() {
		return taxaFlecha;
	}
	public void setTaxaFlecha(double taxaFlecha) {
		this.taxaFlecha = taxaFlecha;
	}
	public boolean isAprovado() {
		return aprovado;
	}
	public void setAprovado(boolean aprovado) {
		this.aprovado = aprovado;
	}

    
}
