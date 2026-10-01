package tercacost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CalculoRequest {
    @JsonProperty("tipoPerfil")
    private String tipoPerfil;
    @JsonProperty("alturaAlma")
    private double alturaAlma;
    @JsonProperty("larguraAba")
    private double larguraAba;
    @JsonProperty("larguraEnrijecedor")
    private double larguraEnrijecedor;
    @JsonProperty("espessuraChapa")
    private double espessuraChapa;
    @JsonProperty("descricao")
    private String descricao;
    @JsonProperty("fy")
    private double fy;
    @JsonProperty("fu")
    private double fu;
    @JsonProperty("E")
    private double E;
    @JsonProperty("G")
    private double G;
    @JsonProperty("Cb")
    private double cb;
    @JsonProperty("usuarioId")
    private Long usuarioId;
    @JsonProperty("id")
    private Long id;





    // --- NOVOS CAMPOS EXCLUSIVOS DA OBRA ---
    @JsonProperty("cargaPermanente")
    private double cargaPermanente; // kg/m²
    @JsonProperty("sobrecarga")
    private double sobrecarga;     // kg/m²
    @JsonProperty("vento")
    private double vento;          // kg/m²
    @JsonProperty("espacamento")
    private double espacamento;     // metros (s)
    @JsonProperty("vao")
    private double vao;             // metros (L)
    @JsonProperty("lb")
    private double lb;              // mm (Comprimento destravado)

    // Gere os Getters e Setters de todos na sua IDE...
    public String getTipoPerfil() { return tipoPerfil; }
    public void setTipoPerfil(String tipoPerfil) { this.tipoPerfil = tipoPerfil; }
    public double getAlturaAlma() { return alturaAlma; }
    public void setAlturaAlma(double alturaAlma) { this.alturaAlma = alturaAlma; }
    public double getLarguraAba() { return larguraAba; }
    public void setLarguraAba(double larguraAba) { this.larguraAba = larguraAba; }
    public double getLarguraEnrijecedor() { return larguraEnrijecedor; }
    public void setLarguraEnrijecedor(double larguraEnrijecedor) { this.larguraEnrijecedor = larguraEnrijecedor; }
    public double getEspessuraChapa() { return espessuraChapa; }
    public void setEspessuraChapa(double espessuraChapa) { this.espessuraChapa = espessuraChapa; }
    public double getFy() { return fy; }
    public void setFy(double fy) { this.fy = fy; }
    public double getFu() { return fu; }
    public void setFu(double fu) { this.fu = fu; }
    public double getE() { return E; }
    public void setE(double E) { this.E = E; }
    public double getG() { return G; }
    public void setG(double G) { this.G = G; }
    public double getCb() { return cb; }
    public void setCb(double cb) { this.cb = cb; }
    public double getCargaPermanente() { return cargaPermanente; }
    public void setCargaPermanente(double cargaPermanente) { this.cargaPermanente = cargaPermanente; }
    public double getSobrecarga() { return sobrecarga; }
    public void setSobrecarga(double sobrecarga) { this.sobrecarga = sobrecarga; }
    public double getVento() { return vento; }
    public void setVento(double vento) { this.vento = vento; }
    public double getEspacamento() { return espacamento; }
    public void setEspacamento(double espacamento) { this.espacamento = espacamento; }
    public double getVao() { return vao; }
    public void setVao(double vao) { this.vao = vao; }
    public double getLb() { return lb; }
    public void setLb(double lb) { this.lb = lb; }
    public Long getUsuarioId() {
        return usuarioId;
    }
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
