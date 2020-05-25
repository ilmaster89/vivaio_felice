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

	Dipendente dipendente;

	@NotNull
	@Size(max = 250)
	String descrizione;

	@ManyToOne
	Prenotazione prenotazione;

	@ManyToOne
	Auto auto;

	@NotNull
	Integer conferma;

	public Notifica() {
	}

	public Notifica(Integer id, Dipendente dipendente, @NotNull @Size(max = 250) String descrizione,
			@NotNull Integer conferma) {
		super();
		this.id = id;
		this.dipendente = dipendente;
		this.descrizione = descrizione;
		this.conferma = conferma;
	}

	public Notifica(@NotNull @Size(max = 250) String descrizione, Auto auto, @NotNull Integer conferma) {
		super();

		this.descrizione = descrizione;
		this.auto = auto;
		this.conferma = conferma;
	}

	public Notifica(Dipendente dipendente, @NotNull @Size(max = 250) String descrizione, Prenotazione prenotazione,
			@NotNull Integer conferma) {
		this.dipendente = dipendente;
		this.descrizione = descrizione;
		this.prenotazione = prenotazione;
		this.conferma = conferma;
	}

	public Prenotazione getPrenotazione() {
		return prenotazione;
	}

	public void setPrenotazione(Prenotazione prenotazione) {
		this.prenotazione = prenotazione;
	}

	public Auto getAuto() {
		return auto;
	}

	public void setAuto(Auto auto) {
		this.auto = auto;
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
