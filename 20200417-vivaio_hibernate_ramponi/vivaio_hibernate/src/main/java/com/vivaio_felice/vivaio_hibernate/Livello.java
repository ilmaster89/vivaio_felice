package com.vivaio_felice.vivaio_hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "livelli")
public class Livello {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@NotNull
	Integer id;

	@NotNull(message = "campo obbligatorio")
	@Size(max = 45, message = "non può superare i 45 caratteri")
	String mansione;

	public Livello() {
	}

	public Livello(@NotNull Integer id, @NotNull @Size(max = 45) String mansione) {
		this.id = id;
		this.mansione = mansione;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMansione() {
		return mansione;
	}

	public void setMansione(String mansione) {
		this.mansione = mansione;
	}

}
