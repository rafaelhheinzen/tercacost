package Service;

public class ResultadoVerificacaoDTO {
    private double mrd; // kN.m
    private double vrd; // kN
    private boolean aprovadoMomento;
    private boolean aprovadoCortante;
    private boolean aprovadoGeral;

    public ResultadoVerificacaoDTO(double mrd, double vrd, double msd, double vsd) {
        this.mrd = mrd;
        this.vrd = vrd;
        this.aprovadoMomento = msd <= mrd;
        this.aprovadoCortante = vsd <= vrd;
        this.aprovadoGeral = this.aprovadoMomento && this.aprovadoCortante;
    }

    // Getters
    public double getMrd() { return mrd; }
    public double getVrd() { return vrd; }
    public boolean isAprovadoMomento() { return aprovadoMomento; }
    public boolean isAprovadoCortante() { return aprovadoCortante; }
    public boolean isAprovadoGeral() { return aprovadoGeral; }
}
