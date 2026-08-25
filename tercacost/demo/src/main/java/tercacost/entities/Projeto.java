package tercacost.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Projeto {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
    private String descricao;
    private double MSD;
    private double VSD;
    private double Lb;
    private double Cb;
    private String nomedoPerfil;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public double getMSD() {
		return MSD;
	}
	public void setMSD(double mSD) {
		MSD = mSD;
	}
	public double getVSD() {
		return VSD;
	}
	public void setVSD(double vSD) {
		VSD = vSD;
	}
	public double getLb() {
		return Lb;
	}
	public void setLb(double lb) {
		Lb = lb;
	}
	public double getCb() {
		return Cb;
	}
	public void setCb(double cb) {
		Cb = cb;
	}
	public String getNomedoPerfil() {
		return nomedoPerfil;
	}
	public void setNomedoPerfil(String nomedoPerfil) {
		this.nomedoPerfil = nomedoPerfil;
	}
 
    
    
    
}