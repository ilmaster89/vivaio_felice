package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Dipendente;

public interface DipendenteDao extends CrudRepository<Dipendente, Integer> {

	List<Dipendente> findByLivelloId(Integer id);

	List<Dipendente> findByNome(String nome);

	List<Dipendente> findByCognome(String cognome);

	List<Dipendente> findByUser(String user);

	List<Dipendente> findByPassword(String password);

	Optional<Dipendente> findById(Integer id);

}