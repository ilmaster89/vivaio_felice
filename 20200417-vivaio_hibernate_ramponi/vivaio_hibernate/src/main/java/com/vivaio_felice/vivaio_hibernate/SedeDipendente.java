package com.vivaio_felice.vivaio_hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sede_dip")
public class SedeDipendente {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@OneToOne
	@JoinColumn
	Dipendente dipendente;

	@ManyToOne
	@JoinColumn
	Sede sede;

	public SedeDipendente() {
	}

	public SedeDipendente(Integer id, Dipendente dipendente, Sede sede) {
		this.id = id;
		this.dipendente = dipendente;
		this.sede = sede;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Dipendente getDipendente() {
		return dipendente;
	}

	public void setDipendente(Dipendente dipendente) {
		this.dipendente = dipendente;
	}

	public Sede getSede() {
		return sede;
	}

	public void setSede(Sede sede) {
		this.sede = sede;
	}

	@Override
	public String toString() {
		return "SedeDipendente [id=" + id + ", id_dipendente=" + dipendente.getId() + ", id_sede=" + sede.getId() + "]";
	}

}
