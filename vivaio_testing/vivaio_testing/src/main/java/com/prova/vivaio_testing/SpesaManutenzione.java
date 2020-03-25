package com.prova.vivaio_testing;


import java.sql.Date;

public class SpesaManutenzione {

	Integer id;
	Integer id_auto;
	Double spesa;
	Date data_spesa;
	String descrizione;
	
	public SpesaManutenzione(Integer id, Integer id_auto, Double spesa, Date data_spesa, String descrizione) {
		super();
		this.id = id;
		this.id_auto = id_auto;
		this.spesa = spesa;
		this.data_spesa = data_spesa;
		this.descrizione = descrizione;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId_auto() {
		return id_auto;
	}

	public void setId_auto(Integer id_auto) {
		this.id_auto = id_auto;
	}

	public Double getSpesa() {
		return spesa;
	}

	public void setSpesa(Double spesa) {
		this.spesa = spesa;
	}

	public Date getData_spesa() {
		return data_spesa;
	}

	public void setData_spesa(Date data_spesa) {
		this.data_spesa = data_spesa;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String toString() {
		return "Manutenzione N°" +id+" su auto N°" +id_auto+", motivazione: " + descrizione;
	}
}
