package com.prova.vivaio_testing;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

//la classe Dipendente (e questo varrà per tutte le altre) riprende perfettamente i dati che ci sono nella tabella di MySQL. 
//Ciò è essenziale per poter trasformare una tupla in oggetto.

public class Dipendente {

	// id e id_livello sono Integer e non int, perché altrimenti quando si carica la
	// prima pagina crea problemi a causa di un NullPointer. Probabilmente perché
	// dicendogli di creare la lista dipendenti dopo il GetMapping parte senza id e
	// id_livello, che essendo null per quella frazione di secondo non possono
	// essere convertiti in int.
	Integer id;
	Integer id_livello;
	String nome;
	String cognome;
	String user_name;
	String password;
	Integer idPatente; // da sostituire una volta creati i MODEL con una variabile di tipo Patente
	Integer idSede; // da sostituire una volta creati i MODEL con una variabile di tipo Sede
	Date dataPossesso;

	public Dipendente(Integer id, Integer id_livello, String nome, String cognome, String user_name, String password,
			Integer idPatente, Integer idSede, Date dataPossesso) {
		this.id = id;
		this.id_livello = id_livello;
		this.nome = nome;
		this.cognome = cognome;
		this.user_name = user_name;
		this.password = password;
		this.idPatente = idPatente;
		this.idSede = idSede;
		this.dataPossesso = dataPossesso;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getId_livello() {
		return id_livello;
	}

	public void setId_livello(int id_livello) {
		this.id_livello = id_livello;
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

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return nome + ' ' + cognome;

	}

	public Integer getIdPatente() {
		return idPatente;
	}

	public void setIdPatente(Integer idPatente) {
		this.idPatente = idPatente;
	}

	public Date getDataPossesso() {
		return dataPossesso;

	}

	public void setDataPossesso(Date dataPossesso) {
		this.dataPossesso = dataPossesso;
	}

	public Integer getIdSede() {
		return idSede;
	}

	public void setIdSede(Integer idSede) {
		this.idSede = idSede;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setId_livello(Integer id_livello) {
		this.id_livello = id_livello;
	}

	public boolean isNeoP() {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);

		java.util.Date annoFa = cal.getTime();
		if (dataPossesso.after(annoFa))
			return true;

		return false;

	}

	public boolean moreThanTwo() {
		if (id_livello >= 2)
			return true;
		return false;
	}

	public boolean moreThanThree() {
		if (id_livello >= 3)
			return true;
		return false;
	}

}
