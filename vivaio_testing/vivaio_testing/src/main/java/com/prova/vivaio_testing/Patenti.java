package com.prova.vivaio_testing;

public class Patenti {

	Integer id;
	String tipologia;
	
	public Patenti(Integer id, String tipologia) {
		super();
		this.id = id;
		this.tipologia = tipologia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTipologia() {
		return tipologia;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	
	
}
