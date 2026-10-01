package tercacost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultadoVerificacaoDTO {
    @JsonProperty("mrd") private double mrd;
    @JsonProperty("vrd") private double vrd;
    @JsonProperty("msd") private double msd; // Adicionado de volta para renderização
    @JsonProperty("vsd") private double vsd; // Adicionado de volta para renderização
    @JsonProperty("aprovadoMomento") private boolean aprovadoMomento;
    @JsonProperty("aprovadoCortante") private boolean aprovadoCortante;
    @JsonProperty("aprovadoGeral") private boolean aprovadoGeral;
    @JsonProperty("area") private double area;
    @JsonProperty("pesoPorMetro") private double pesoPorMetro;

    // --- NOVOS CAMPOS ---
    @JsonProperty("flechaReal") private double flechaReal;
    @JsonProperty("flechaLimite") private double flechaLimite;
    @JsonProperty("aprovadoFlecha") private boolean aprovadoFlecha;
    @JsonProperty("relatorioDiagnostico") private String relatorioDiagnostico;

    public ResultadoVerificacaoDTO(double mrd, double vrd, double msd, double vsd, 
                                   double area, double pesoPorMetro, double flechaReal, 
                                   double flechaLimite, String relatorioDiagnostico) {
        this.mrd = mrd;
        this.vrd = vrd;
        this.msd = msd;
        this.vsd = vsd;
        this.area = area;
        this.pesoPorMetro = pesoPorMetro;
        this.flechaReal = flechaReal;
        this.flechaLimite = flechaLimite;
        this.aprovadoMomento = msd <= mrd;
        this.aprovadoCortante = vsd <= vrd;
        this.aprovadoFlecha = flechaReal <= flechaLimite;
        // Agora o projeto só aprova de forma geral se passar no ELU (forças) E no ELS (flechas)
        this.aprovadoGeral = this.aprovadoMomento && this.aprovadoCortante && this.aprovadoFlecha;
        this.relatorioDiagnostico = relatorioDiagnostico;
    }

    // Getters normais...
    public double getMrd() { return mrd; }
    public double getVrd() { return vrd; }
    public double getMsd() { return msd; }
    public double getVsd() { return vsd; }
    public boolean isAprovadoMomento() { return aprovadoMomento; }
    public boolean isAprovadoCortante() { return aprovadoCortante; }
    public boolean isAprovadoGeral() { return aprovadoGeral; }
    public double getArea() { return area; }
    public double getPesoPorMetro() { return pesoPorMetro; }
    public double getFlechaReal() { return flechaReal; }
    public double getFlechaLimite() { return flechaLimite; }
    public boolean isAprovadoFlecha() { return aprovadoFlecha; }
    public String getRelatorioDiagnostico() { return relatorioDiagnostico; }
}
