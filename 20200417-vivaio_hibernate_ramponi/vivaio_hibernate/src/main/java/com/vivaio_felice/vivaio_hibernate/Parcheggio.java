package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

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

	@Column(name = "data_parch")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate dataParch;

	public Parcheggio() {
	}

	public Parcheggio(Integer id, @NotNull Auto auto, @NotNull Sede sede, @NotNull LocalDate dataParch) {
		this.id = id;
		this.auto = auto;
		this.sede = sede;
		this.dataParch = dataParch;
	}

	public Parcheggio(@NotNull Auto auto, @NotNull Sede sede, LocalDate dataParch) {
		super();
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

	public LocalDate getDataParch() {
		return dataParch;
	}

	public void setDataParch(LocalDate dataParch) {
		this.dataParch = dataParch;
	}

	@Override
	public String toString() {
		return "Parcheggio [id=" + id + ", auto=" + auto + ", sede=" + sede + ", dataParch=" + dataParch + "]";
	}

	public LocalDate dataTrasferimento(Integer idSede) {
		if (this.getDataParch().isAfter(LocalDate.now()) && this.getSede().getId() != idSede)
			return this.getDataParch();
		return null;
	}

}
