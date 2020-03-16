package com.prova.vivaio_testing;

import java.sql.Timestamp;

public class Prenotazione {

	Integer id;
	Integer idDip;
	Integer idAuto;
	Integer idCausale;
	Timestamp dataInizio;
	Timestamp dataFine;
	Integer km;
	String marca;
	String modello;
	String targa;

	public Prenotazione(Integer id, Integer idDip, Integer idAuto, Integer idCausale, Timestamp dataInizio,
			Timestamp dataFine, Integer km, String marca, String modello, String targa) {
		this.id = id;
		this.idDip = idDip;
		this.idAuto = idAuto;
		this.idCausale = idCausale;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.km = km;
		this.marca = marca;
		this.modello = modello;
		this.targa = targa;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdDip() {
		return idDip;
	}

	public void setIdDip(Integer idDip) {
		this.idDip = idDip;
	}

	public Integer getIdAuto() {
		return idAuto;
	}

	public void setIdAuto(Integer idAuto) {
		this.idAuto = idAuto;
	}

	public Integer getIdCausale() {
		return idCausale;
	}

	public void setIdCausale(Integer idCausale) {
		this.idCausale = idCausale;
	}

	public Timestamp getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Timestamp dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Timestamp getDataFine() {
		return dataFine;
	}

	public void setDataFine(Timestamp dataFine) {
		this.dataFine = dataFine;
	}

	public Integer getKm() {
		return km;
	}

	public void setKm(Integer km) {
		this.km = km;
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

}