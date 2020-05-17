package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Causale;

public interface CausaleDao extends CrudRepository<Causale, Integer> {

	Optional<Causale> findById(Integer id);

	List<Causale> findByDescrizione(String descrizione);

	@Query("select s from Causale s where id != :lavoro")
	public List<Causale> causaliEccetto(Integer lavoro);

}
