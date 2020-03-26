package com.prova.vivaio_testing;

import java.util.ArrayList;

public class Sede_dip {

	Integer id;
	ArrayList<Integer> id_dipendente;
	Integer id_sede;

	public Sede_dip(Integer id, Integer id_dipendente, Integer id_sede) {
		super();
		this.id = id;
		this.id_sede = id_sede;
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

}
