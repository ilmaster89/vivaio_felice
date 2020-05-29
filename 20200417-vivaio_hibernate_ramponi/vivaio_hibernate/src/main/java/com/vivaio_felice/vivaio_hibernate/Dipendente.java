package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "dipendenti")
public class Dipendente {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@OneToOne
	Livello livello;

	@NotBlank(message = "Campo obbligatorio.")
	String nome;

	@NotBlank(message = "Campo obbligatorio.")
	String cognome;

	@NotBlank(message = "Campo obbligatorio.")
	@Column(name = "user_name", unique = true)
	String user;

	@NotBlank(message = "Campo obbligatorio.")
	String password;

	@OneToOne(mappedBy = "dipendente", cascade = CascadeType.ALL)
	SedeDipendente sedeDipendente;

	@Transient
	@OneToMany(mappedBy = "dipendente", cascade = CascadeType.ALL)
	List<PossessoPatenti> possessoPatenti;

	@OneToMany(mappedBy = "dipendente", cascade = CascadeType.ALL)
	List<Notifica> notifiche;

	public Dipendente() {

	}

	public Dipendente(@NotBlank(message = "Campo obbligatorio.") String nome,
			@NotBlank(message = "Campo obbligatorio.") String cognome) {
		super();
		this.nome = nome;
		this.cognome = cognome;
	}

	public Dipendente(Integer id, Livello livello, @NotBlank(message = "Campo obbligatorio.") String nome,
			@NotBlank(message = "Campo obbligatorio.") String cognome,
			@NotBlank(message = "Campo obbligatorio.") String user,
			@NotBlank(message = "Campo obbligatorio.") String password) {

		this.id = id;
		this.livello = livello;
		this.nome = nome;
		this.cognome = cognome;
		this.user = user;
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

	public List<Notifica> getNotifiche() {
		return notifiche;
	}

	public void setNotifiche(List<Notifica> notifiche) {
		this.notifiche = notifiche;
	}

	public boolean neoP(List<PossessoPatenti> posPat, Integer idPat) {
		for (PossessoPatenti pp : posPat) {
			if (pp.getPatente().getId() == idPat
					&& pp.getDataPoss().toLocalDate().isAfter(LocalDate.now().minus(1, ChronoUnit.YEARS)))
				return true;
		}
		return false;
	}

	public boolean patenteC(List<PossessoPatenti> posPat, Integer idPat) {
		for (PossessoPatenti pp : posPat) {
			// cambiare costante con query annotation
			if (pp.getPatente().getId() == idPat)
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Dipendente [id=" + id + ", id_livello=" + livello.getId() + ", nome=" + nome + ", cognome=" + cognome
				+ ", user_name=" + user + ", password=" + password + " possessoPatenti=" + possessoPatenti + "]";
	}

}
