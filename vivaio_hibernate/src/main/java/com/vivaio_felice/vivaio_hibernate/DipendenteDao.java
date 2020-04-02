package com.vivaio_felice.vivaio_hibernate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface DipendenteDao extends CrudRepository<Dipendente, Integer> {

	List<Dipendente> findByLivello(Integer livello);

	List<Dipendente> findByNome(String nome);

	List<Dipendente> findByCognome(String cognome);

	List<Dipendente> findByUser_name(String user_name);

	List<Dipendente> findByPassword(String password);

	// optional perché esiste una classe superiore?
	Optional<Dipendente> findById(Integer id);

}
