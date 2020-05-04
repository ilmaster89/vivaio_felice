package com.vivaio_felice.vivaio_hibernate;

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

import org.springframework.beans.factory.annotation.Autowired;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleDao;
import com.vivaio_felice.vivaio_hibernate.dao.DipendenteDao;

@Entity
@Table(name = "prenotazioni")
public class Prenotazione {

	@Autowired
	@Transient
	DipendenteDao dipendenteDao;
	@Autowired
	@Transient
	AutoDao autoDao;
	@Autowired
	@Transient
	CausaleDao causaleDao;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@OneToOne
	Dipendente dipendente;

	@OneToOne
	Auto auto;

	@OneToOne
	Causale causale;

	@NotNull
	@Column(name = "data_inizio")
	Date dataInizio;

	@Column(name = "data_fine")
	Date dataFine;

	Integer km;

	public Prenotazione() {
	}

	public Prenotazione(Integer id, Integer idDipendente, Integer idAuto, Integer idCausale, @NotNull Date dataInizio,
			Date dataFine, Integer km) {

		this.id = id;
		this.dipendente = dipendenteDao.findById(idDipendente).get();
		this.auto = autoDao.findById(idAuto).get();
		this.causale = causaleDao.findById(idCausale).get();
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.km = km;

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
		return "Prenotazione [id=" + id + ", dipendente=" + dipendente + ", auto=" + auto + ", causale=" + causale
				+ ", dataInizio=" + dataInizio + ", dataFine=" + dataFine + ", km=" + km + "]";
	}

}