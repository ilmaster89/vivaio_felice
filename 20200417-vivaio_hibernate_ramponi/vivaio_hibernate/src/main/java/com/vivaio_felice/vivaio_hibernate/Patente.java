package com.vivaio_felice.vivaio_hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "patenti")
public class Patente {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@NotNull(message = "campo obbligatorio")
	String tipologia;

	@NotNull
	@OneToMany(mappedBy = "patente", cascade = CascadeType.ALL)
	List<PossessoPatenti> possessoPatenti = new ArrayList<>();

	public Patente() {
	}

	public Patente(Integer id, @NotNull String tipologia) {
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

	public List<PossessoPatenti> getPossessoPatenti() {
		return possessoPatenti;
	}

	public void setPossessoPatenti(List<PossessoPatenti> possessoPatenti) {
		this.possessoPatenti = possessoPatenti;
	}

	@Override
	public String toString() {
		return "Patente [id=" + id + ", tipologia=" + tipologia + "]";
	}

}
