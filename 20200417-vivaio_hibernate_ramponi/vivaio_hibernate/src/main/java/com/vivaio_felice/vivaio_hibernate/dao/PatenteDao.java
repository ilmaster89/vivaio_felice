package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Patente;

public interface PatenteDao extends CrudRepository<Patente, Integer> {

	Optional<Patente> findById(Integer id);

	Patente findByTipologia(String tipologia);

	@Query(value = "select id from patenti where tipologia like '%B%'", nativeQuery = true)
	public Integer idB();

	@Query(value = "select id from patenti where tipologia like '%C%'", nativeQuery = true)
	public Integer idC();

}
