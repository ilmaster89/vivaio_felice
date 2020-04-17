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
@Table(name = "parcheggio")
public class Parcheggio {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn
	Auto auto;

	@NotNull
	@ManyToOne
	@JoinColumn
	Sede sede;

	@NotNull
	@Column(name = "data_parch")
	Date dataParch;

	public Parcheggio() {
	}

	public Parcheggio(Integer id, @NotNull Auto auto, @NotNull Sede sede, @NotNull Date dataParch) {
		this.id = id;
		this.auto = auto;
		this.sede = sede;
		this.dataParch = dataParch;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Auto getAuto() {
		return auto;
	}

	public void setAuto(Auto auto) {
		this.auto = auto;
	}

	public Sede getSede() {
		return sede;
	}

	public void setSede(Sede sede) {
		this.sede = sede;
	}

	public Date getDataParch() {
		return dataParch;
	}

	public void setDataParch(Date dataParch) {
		this.dataParch = dataParch;
	}

	@Override
	public String toString() {
		return "Parcheggio [id=" + id + ", auto=" + auto + ", sede=" + sede + ", dataParch=" + dataParch + "]";
	}

}
