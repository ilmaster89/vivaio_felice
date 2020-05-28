package com.vivaio_felice.vivaio_hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "carburanti")
public class Carburante {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@NotBlank(message = "Campo obbligatorio.")
	String tipologia;

	public Carburante() {
	}

	public Carburante(Integer id, @NotNull String tipologia) {
		super();
		this.id = id;
		this.tipologia = tipologia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTipologia() {
		return tipologia;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	@Override
	public String toString() {
		return "Carburante [id=" + id + ", tipologia=" + tipologia + "]";
	}

}
