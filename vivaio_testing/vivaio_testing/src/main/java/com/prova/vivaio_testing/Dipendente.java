package com.prova.vivaio_testing;

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

	public Dipendente(Integer id, Integer id_livello, String nome, String cognome, String user_name, String password) {
		this.id = id;
		this.id_livello = id_livello;
		this.nome = nome;
		this.cognome = cognome;
		this.user_name = user_name;
		this.password = password;
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

}
