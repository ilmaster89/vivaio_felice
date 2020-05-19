package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Causale;

public interface CausaleDao extends CrudRepository<Causale, Integer> {

	@Query("select s from Causale s where id = :id")
	Causale causaleDaId(Integer id);

	List<Causale> findByDescrizione(String descrizione);

	@Query("select s from Causale s where id != :lavoro")
	public List<Causale> causaliEccetto(Integer lavoro);

	@Query(value = "select id from causale where descrizione like '%lavoro%'", nativeQuery = true)
	Integer idLavoro();

}
