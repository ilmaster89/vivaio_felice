package com.vivaio_felice.vivaio_hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.vivaio_felice.vivaio_hibernate.customValidators.Targa;

@Entity
@Table(name = "auto")
public class Auto {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@OneToOne
	@JoinColumn(name = "patente_id")
	Patente patente;

	@OneToOne
	@JoinColumn(name = "carburante_id")
	Carburante carburante;

	@NotBlank(message = "Campo obbligatorio")
	String marca;

	@NotBlank(message = "Campo obbligatorio")
	String modello;

	@Targa
	@Column(unique = true)
	String targa;

	@NotNull(message = "Campo obbligatorio.")
	Double kw;

	@NotNull(message = "Campo obbligatorio.")
	Double tara;

	@Column(name = "data_assicurazione")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	Date dataAss;

	@Transient
	@OneToMany(mappedBy = "auto", cascade = CascadeType.ALL)
	@JoinColumn(name = "auto_id")
	List<Parcheggio> parcheggio = new ArrayList<>();

	@Column(name = "km_iniziali")
	Integer kmIniziali = 0;

	public Auto() {
	}

	public Auto(Integer id, @NotNull Patente patente, @NotNull Carburante carburante,
			@NotNull @Size(max = 45) String marca, @NotNull @Size(max = 45) String modello,
			@NotNull @Size(max = 45) String targa, @NotNull Double kw, @NotNull Double tara, @NotNull Date dataAss) {
		super();
		this.id = id;
		this.patente = patente;
		this.carburante = carburante;
		this.marca = marca;
		this.modello = modello;
		this.targa = targa;
		this.kw = kw;
		this.tara = tara;
		this.dataAss = dataAss;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Patente getPatente() {
		return patente;
	}

	public void setPatente(Patente patente) {
		this.patente = patente;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Carburante getCarburante() {
		return carburante;
	}

	public void setCarburante(Carburante carb) {
		this.carburante = carb;
	}

	public List<Parcheggio> getParcheggio() {
		return parcheggio;
	}

	public void setParcheggio(List<Parcheggio> parcheggio) {
		this.parcheggio = parcheggio;
	}

	public String getModello() {
		return modello;
	}

	public void setModello(String modello) {
		this.modello = modello;
	}

	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	public Double getKw() {
		return kw;
	}

	public void setKw(Double kw) {
		this.kw = kw;
	}

	public Double getTara() {
		return tara;
	}

	public void setTara(Double tara) {
		this.tara = tara;
	}

	public Date getDataAss() {
		return dataAss;
	}

	public void setDataAss(Date data_assicurazione) {
		this.dataAss = data_assicurazione;
	}

	public Integer getKmIniziali() {
		return kmIniziali;
	}

	public void setKmIniziali(Integer kmIniziali) {
		this.kmIniziali = kmIniziali;
	}

	public boolean okForNeoP() {
		// costanti dalla motorizzazione, da non modificare
		if ((this.getKw() / ((this.getTara() + 75) / 1000) <= 55) && this.getKw() <= 70)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return marca + " " + modello + " " + targa;
	}

}
