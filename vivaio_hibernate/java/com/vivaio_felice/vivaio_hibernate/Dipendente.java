package com.vivaio_felice.vivaio_hibernate;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
	@Column(name = "user_name")
	String user;

	@NotNull
	@Size(max = 45)
	String password;

	public Dipendente(@NotNull Integer id, Livello livello, @NotNull @Size(max = 45) String nome,
			@NotNull @Size(max = 45) String cognome, @NotNull @Size(max = 45) String user_name,
			@NotNull @Size(max = 45) String password) {
		this.id = id;
		this.livello = livello;
		this.nome = nome;
		this.cognome = cognome;
		this.user = user_name;
		this.password = password;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public @NotNull Livello getLivello() {
		return livello;
	}

	public void setLivello(@NotNull Livello livello) {
		this.livello = livello;
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

	public boolean moreThanTwo() {
		if (livello.getId() >= 2)
			return true;
		return false;
	}

	public boolean moreThanThree() {
		if (livello.getId() >= 3)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "Dipendente [id=" + id + ", id_livello=" + livello.getId() + "nome=" + nome + ", cognome=" + cognome
				+ ", user_name=" + user + ", password=" + password + "]";
	}

}
