package com.prova.vivaio_testing;


import java.sql.Date;
import java.util.ArrayList;

public class Parcheggio {

	Integer id;
	Integer id_sede;
	ArrayList<Integer> id_auto;
	Date data_parch;
	
	public Parcheggio(Integer id, Integer id_sede, ArrayList<Integer> id_auto, Date data_parch) {
		super();
		this.id = id;
		this.id_sede = id_sede;
		this.id_auto = id_auto;
		this.data_parch = data_parch;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId_sede() {
		return id_sede;
	}

	public void setId_sede(Integer id_sede) {
		this.id_sede = id_sede;
	}

	public ArrayList<Integer> getId_auto() {
		return id_auto;
	}

	public void setId_auto(ArrayList<Integer> id_auto) {
		this.id_auto = id_auto;
	}

	public Date getData_parch() {
		return data_parch;
	}

	public void setData_parch(Date data_parch) {
		this.data_parch = data_parch;
	}

	@Override
	public String toString() {
		return "Nella sede numero:" + id_sede + "si trova l'auto: " + id_auto + "dal giorno:" + data_parch;
	}

}
