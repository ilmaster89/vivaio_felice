package com.prova.vivaio_testing;


import java.sql.Date;
import java.util.ArrayList;

public class PossessoPatenti {
	
	Integer id;
	Integer id_patente;
	ArrayList<Integer> id_dipendente;
	Date data_possesso;
	
	public PossessoPatenti(Integer id, Integer id_patente, ArrayList<Integer> id_dipendente, Date data_possesso) {
		super();
		this.id = id;
		this.id_patente = id_patente;
		this.id_dipendente = id_dipendente;
		this.data_possesso = data_possesso;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId_patente() {
		return id_patente;
	}

	public void setId_patente(Integer id_patente) {
		this.id_patente = id_patente;
	}

	public ArrayList<Integer> getId_dipendente() {
		return id_dipendente;
	}

	public void setId_dipendente(ArrayList<Integer> id_dipendente) {
		this.id_dipendente = id_dipendente;
	}

	public Date getData_possesso() {
		return data_possesso;
	}

	public void setData_possesso(Date data_possesso) {
		this.data_possesso = data_possesso;
	}
	
	public String toString() {
		return "Il Dipendente numero:" +id_dipendente+ " possiede la patente di tipo: " +id_patente+ " dal giorno: " + data_possesso;
	}
	

}
