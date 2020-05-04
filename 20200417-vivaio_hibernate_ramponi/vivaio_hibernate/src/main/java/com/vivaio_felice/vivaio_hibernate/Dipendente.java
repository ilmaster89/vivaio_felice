package com.vivaio_felice.vivaio_hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "dipendenti")
public class Dipendente {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@NotNull
	Integer id;

	// servono annotations?
	@NotNull
	@OneToOne
	Livello livello;

	@NotNull
	@Size(max = 45)
	String nome;

	@NotNull
	@Size(max = 45)
	String cognome;

	@NotNull
	@Size(max = 45)
	@Column(name = "user_name", unique = true)
	String user;

	@NotNull
	@Size(max = 45)
	String password;

	@NotNull
	@OneToOne(mappedBy = "dipendente", cascade = CascadeType.ALL)
	SedeDipendente sedeDipendente;

	@NotNull
	@Transient
	@OneToMany(mappedBy = "dipendente", cascade = CascadeType.ALL)
	List<PossessoPatenti> possessoPatenti = new ArrayList<>();

//	@NotNull
//	@Transient
//	@OneToOne(mappedBy = "dipAttuale", cascade = CascadeType.ALL)
//	PossessoPatenti possAttuale;

	public Dipendente() {

	}

	public Dipendente(@NotNull Integer id, @NotNull Livello livello, @NotNull @Size(max = 45) String nome,
			@NotNull @Size(max = 45) String cognome, @NotNull @Size(max = 45) String user_name,
			@NotNull @Size(max = 45) String password) {
		this.id = id;
		this.livello = livello;
		this.nome = nome;
		this.cognome = cognome;
		this.user = user_name;
		this.password = password;
	}

//	public PossessoPatenti getPossAttuale() {
//		return possAttuale;
//	}
//
//	public void setPossAttuale(PossessoPatenti possAttuale) {
//		this.possAttuale = possAttuale;
//	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public @NotNull Livello getLivello() {
		return livello;
	}

	public void setLivello(@NotNull Livello id_livello) {
		this.livello = id_livello;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user_name) {
		this.user = user_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean impiegato() {
		if (livello.getId() >= 2)
			return true;
		return false;
	}

	public boolean responsabile() {
		if (livello.getId() >= 3)
			return true;
		return false;
	}

	public SedeDipendente getSedeDipendente() {
		return sedeDipendente;
	}

	public void setSedeDipendente(SedeDipendente sedeDipendente) {
		this.sedeDipendente = sedeDipendente;
	}

	public List<PossessoPatenti> getPossessoPatenti() {
		return possessoPatenti;
	}

	public void setPossessoPatenti(List<PossessoPatenti> possessoPatenti) {
		this.possessoPatenti = possessoPatenti;
	}

	public void setPossessoPatenti(PossessoPatenti possessoPatenti) {
		this.possessoPatenti.add(possessoPatenti);
	}

	@Override
	public String toString() {
		return "Dipendente [id=" + id + ", id_livello=" + livello.getId() + ", nome=" + nome + ", cognome=" + cognome
				+ ", user_name=" + user + ", password=" + password + " possessoPatenti=" + possessoPatenti + "]";
	}

}