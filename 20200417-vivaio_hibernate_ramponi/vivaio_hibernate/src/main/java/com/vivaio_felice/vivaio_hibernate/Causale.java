package com.vivaio_felice.vivaio_hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "causale")
public class Causale {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@NotBlank(message = "Campo obbligatorio.")
	String descrizione;

	public Causale() {
	}

	public Causale(Integer id, @NotNull String descrizione) {
		this.id = id;
		this.descrizione = descrizione;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public String toString() {
		return "Causale [id=" + id + ", descrizione=" + descrizione + "]";
	}

}
