package com.prova.vivaio_testing;

import java.sql.Date;

public class Auto {

	Integer id;
	Integer idPatente;
	String carburante;
	String marca;
	String modello;
	String targa;
	Integer kw;
	Integer tara;
	Date dataAssicurazione;

	public Auto(Integer id, Integer idPatente, String carburante, String marca, String modello, String targa,
			Integer kw, Integer tara, Date dataAssicurazione) {
		super();
		this.id = id;
		this.idPatente = idPatente;
		this.carburante = carburante;
		this.marca = marca;
		this.modello = modello;
		this.targa = targa;
		this.kw = kw;
		this.tara = tara;
		this.dataAssicurazione = dataAssicurazione;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdPatente() {
		return idPatente;
	}

	public void setIdPatente(Integer idPatente) {
		this.idPatente = idPatente;
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

	public Integer getKw() {
		return kw;
	}

	public void setKw(Integer kw) {
		this.kw = kw;
	}

	public Integer getTara() {
		return tara;
	}

	public void setTara(Integer tara) {
		this.tara = tara;
	}

	public Date getDataAssicurazione() {
		return dataAssicurazione;
	}

	public void setDataAssicurazione(Date dataAssicurazione) {
		this.dataAssicurazione = dataAssicurazione;
	}

	public String toString() {
		return marca + " " + modello;
	}

}
