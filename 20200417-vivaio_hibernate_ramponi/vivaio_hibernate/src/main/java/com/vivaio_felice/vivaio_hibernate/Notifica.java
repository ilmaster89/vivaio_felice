package com.vivaio_felice.vivaio_hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "notifiche")
public class Notifica {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@ManyToOne
	@NotNull
	Dipendente dipendente;

	@NotNull
	@Size(max = 250)
	String descrizione;

	@NotNull
	Integer conferma;

	public Notifica() {
	}

	public Notifica(Integer id, @NotNull Dipendente dipendente, @NotNull @Size(max = 250) String descrizione,
			@NotNull Integer conferma) {
		super();
		this.id = id;
		this.dipendente = dipendente;
		this.descrizione = descrizione;
		this.conferma = conferma;
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

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Integer getConferma() {
		return conferma;
	}

	public void setConferma(Integer conferma) {
		this.conferma = conferma;
	}

	@Override
	public String toString() {
		return "Notifica [id=" + id + ", dipendente=" + dipendente + ", descrizione=" + descrizione + ", conferma="
				+ conferma + "]";
	}

}