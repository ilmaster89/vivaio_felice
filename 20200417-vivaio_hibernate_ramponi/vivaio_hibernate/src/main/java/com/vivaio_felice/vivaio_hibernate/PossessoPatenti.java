package com.vivaio_felice.vivaio_hibernate;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "possesso_patenti")
public class PossessoPatenti {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer id;

	@ManyToOne
	@JoinColumn
	Dipendente dipendente;

	@ManyToOne
	@JoinColumn
	Patente patente;

	@NotNull
	@Column(name = "data_possesso")
	Date dataPoss;

	public PossessoPatenti() {
	}

	public PossessoPatenti(@NotNull Dipendente dipendente, @NotNull Patente patente, @NotNull Date dataPoss) {
		this.dipendente = dipendente;
		this.patente = patente;
		this.dataPoss = dataPoss;
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

	public Patente getPatente() {
		return patente;
	}

	public void setPatente(Patente patente) {
		this.patente = patente;
	}

	public Date getDataPoss() {
		return dataPoss;
	}

	public void setDataPoss(Date dataPoss) {
		this.dataPoss = dataPoss;
	}

	@Override
	public String toString() {
		return "PossessoPatenti [id=" + id + ", dipendente=" + dipendente + ", patente=" + patente + ", dataPoss="
				+ dataPoss + "]";
	}

}
