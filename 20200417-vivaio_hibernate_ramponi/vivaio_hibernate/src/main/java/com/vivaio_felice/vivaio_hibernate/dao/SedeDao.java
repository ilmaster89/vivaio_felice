package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Sede;

public interface SedeDao extends CrudRepository<Sede, Integer> {

	Optional<Sede> findById(Integer id);

	List<Sede> findByRegione(String regione);

	List<Sede> findByCitta(String citta);

	@Query("select s from Sede s where id != :idSede and id != 13")
	public List<Sede> sediEccetto(Integer idSede);

	@Query("select s from Sede s where id = :idSede")
	public Sede sedeSingola(Integer idSede);

	@Query(value = "select id from sede where regione like '%tutte%'", nativeQuery = true)
	Integer tutteLeSedi();

}
