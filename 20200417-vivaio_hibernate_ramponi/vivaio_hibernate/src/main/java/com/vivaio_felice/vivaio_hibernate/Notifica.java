package com.vivaio_felice.vivaio_hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
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

	@NotBlank(message = "Campo obbligatorio.")
	String descrizione;

	@ManyToOne
	Prenotazione prenotazione;

	@ManyToOne
	Auto auto;

	@ManyToOne
	@JoinColumn(name = "causale")
	CausaleNotifica causaleNotifica;

	Integer conferma;

	public Notifica() {
	}

	public Notifica(Dipendente dipendente, @NotNull @Size(max = 250) String descrizione, @NotNull Integer conferma,
			CausaleNotifica causaleNotifica) {

		this.dipendente = dipendente;
		this.descrizione = descrizione;
		this.conferma = conferma;
		this.causaleNotifica = causaleNotifica;
	}

	public Notifica(@NotNull @Size(max = 250) String descrizione, Auto auto, @NotNull Integer conferma,
			CausaleNotifica causaleNotifica) {
		super();

		this.descrizione = descrizione;
		this.auto = auto;
		this.conferma = conferma;
		this.causaleNotifica = causaleNotifica;
	}

	public Notifica(Dipendente dipendente, @NotNull @Size(max = 250) String descrizione, Prenotazione prenotazione,
			@NotNull Integer conferma, CausaleNotifica causaleNotifica) {
		this.dipendente = dipendente;
		this.descrizione = descrizione;
		this.prenotazione = prenotazione;
		this.conferma = conferma;
		this.causaleNotifica = causaleNotifica;
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

	public CausaleNotifica getCausaleNotifica() {
		return causaleNotifica;
	}

	public void setCausaleNotifica(CausaleNotifica causaleNotifica) {
		this.causaleNotifica = causaleNotifica;
	}

	@Override
	public String toString() {
		return "Notifica [id=" + id + ", dipendente=" + dipendente + ", descrizione=" + descrizione + ", conferma="
				+ conferma + "]";
	}

}
