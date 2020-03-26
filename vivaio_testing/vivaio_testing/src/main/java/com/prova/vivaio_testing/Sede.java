package com.prova.vivaio_testing;

public class Sede {

	Integer id;
	String regione;
	String citta;
	
	public Sede(Integer id, String regione, String citta) {
		super();
		this.id = id;
		this.regione = regione;
		this.citta = citta;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getRegione() {
		return regione;
	}


	public void setRegione(String regione) {
		this.regione = regione;
	}


	public String getCitta() {
		return citta;
	}


	public void setCitta(String citta) {
		this.citta = citta;
	}
	
	
}
