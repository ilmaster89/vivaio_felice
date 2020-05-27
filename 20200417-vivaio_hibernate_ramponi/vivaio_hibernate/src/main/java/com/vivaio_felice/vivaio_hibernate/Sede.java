package com.vivaio_felice.vivaio_hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sede")
public class Sede {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@NotNull(message = "campo obbligatorio")
	String regione;

	@NotNull(message = "campo obbligatorio")
	String citta;

	@OneToMany(mappedBy = "sede", cascade = CascadeType.ALL)
	List<SedeDipendente> sedeDipendente = new ArrayList<>();

	@OneToMany(mappedBy = "sede", cascade = CascadeType.ALL)
	List<Parcheggio> parcheggio = new ArrayList<>();

	public Sede() {
	}

	public Sede(Integer id, @NotNull String regione, @NotNull String citta) {
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

	@Override
	public String toString() {
		return "Sede [id=" + id + ", regione=" + regione + ", citta=" + citta + "]";
	}

}
