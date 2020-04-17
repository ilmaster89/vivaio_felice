package com.vivaio_felice.vivaio_hibernate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "auto")
public class Auto {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@NotNull
	@OneToOne
	Patente patente;

	@NotNull
	@Size(max = 45)
	String carburante;

	@NotNull
	@Size(max = 45)
	String marca;

	@NotNull
	@Size(max = 45)
	String modello;

	@NotNull
	@Size(max = 45)
	@Column(unique = true)
	String targa;

	@NotNull
	Double kw;

	@NotNull
	Double tara;

	@NotNull
	@Column(name = "data_assicurazione")
	Date dataAss;

	@OneToMany(mappedBy = "auto", cascade = CascadeType.ALL)
	List<Parcheggio> parcheggio = new ArrayList<>();

	public Auto() {
	}

	public Auto(Integer id, Patente patente, @NotNull @Size(max = 45) String carburante,
			@NotNull @Size(max = 45) String marca, @NotNull @Size(max = 45) String modello,
			@NotNull @Size(max = 45) String targa, @NotNull Double kw, @NotNull Double tara,
			@NotNull Date data_assicurazione) {
		this.id = id;
		this.patente = patente;
		this.carburante = carburante;
		this.marca = marca;
		this.modello = modello;
		this.targa = targa;
		this.kw = kw;
		this.tara = tara;
		this.dataAss = data_assicurazione;
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

	public String getCarburante() {
		return carburante;
	}

	public void setCarburante(String carburante) {
		this.carburante = carburante;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
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

	public Date getData_assicurazione() {
		return dataAss;
	}

	public void setData_assicurazione(Date data_assicurazione) {
		this.dataAss = data_assicurazione;
	}

	@Override
	public String toString() {
		return "Auto [id=" + id + ", patente=" + patente + ", carburante=" + carburante + ", marca=" + marca
				+ ", modello=" + modello + ", targa=" + targa + ", kw=" + kw + ", tara=" + tara
				+ ", data_assicurazione=" + dataAss + "]";
	}

}
