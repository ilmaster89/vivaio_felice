package com.vivaio_felice.vivaio_hibernate;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.vivaio_felice.vivaio_hibernate.customValidators.DataPrenotazione;

@Entity
@Table(name = "prenotazioni")
public class Prenotazione {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@OneToOne
	Dipendente dipendente;

	@OneToOne
	Auto auto;

	@OneToOne
	Causale causale;

	@Column(name = "data_inizio")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	Date dataInizio;

	@Column(name = "data_fine")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	Date dataFine;

	// transient perché non devono essere considerati attributi, verranno usati per
	// il toString o per metodi sotto
	@Transient
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	@Transient
	SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
	@Transient
	SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy");

	Integer km;

	public Prenotazione() {
	}

	public Prenotazione(Integer id, Dipendente dipendente, Auto auto, Causale causale, @NotNull Date dataInizio,
			Date dataFine, Integer km) {
		this.id = id;
		this.dipendente = dipendente;
		this.auto = auto;
		this.causale = causale;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.km = km;
	}

	public Prenotazione(Integer id, String marca, String modello, String targa, Date dataInizio, Date dataFine,
			Integer km) {

		this.id = id;
		this.auto.marca = marca;
		this.auto.modello = modello;
		this.auto.targa = targa;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.km = km;

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

	public Auto getAuto() {
		return auto;
	}

	public void setAuto(Auto auto) {
		this.auto = auto;
	}

	public Causale getCausale() {
		return causale;
	}

	public void setCausale(Causale causale) {
		this.causale = causale;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	public Integer getKm() {
		return km;
	}

	public void setKm(Integer km) {
		this.km = km;
	}

	@Override
	public String toString() {
		return auto.getMarca() + " " + auto.getModello() + " " + auto.getTarga() + " "
				+ sdf.format(this.getDataInizio()) + "-" + sdf2.format(this.getDataFine());
	}

	// restituisce una data già formattata in modo da usarla nei grafici
	public String dataFormattata() {
		return sdf3.format(this.getDataInizio());
	}
}
