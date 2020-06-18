package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Dipendente;

public interface DipendenteDao extends CrudRepository<Dipendente, Integer> {

	List<Dipendente> findByLivelloId(Integer id);

	List<Dipendente> findByNome(String nome);

	List<Dipendente> findByCognome(String cognome);

	List<Dipendente> findByUser(String user);

	List<Dipendente> findByPassword(String password);

	@Query(value = "select * from dipendenti where id = :idDip", nativeQuery = true)
	public Dipendente dipDaId(Integer idDip);

	@Query("select s from Dipendente s where user_name = :user and password = :pass")
	public Dipendente login(String user, String pass);

	@Query(value = "select * from dipendenti join sede_dip on dipendenti.id = sede_dip.dipendente_id where sede_id = :idSede", nativeQuery = true)
	public List<Dipendente> dipendentiInSede(Integer idSede);

	// conta quanti dipendenti sono presenti in VIVAIO FELICE
	@Query(value = "select COUNT(id) from dipendenti", nativeQuery = true)
	public Integer quantitaDip();

}