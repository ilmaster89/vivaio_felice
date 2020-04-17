package com.vivaio_felice.vivaio_hibernate.dao;

import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Sede;

public interface SedeDao extends CrudRepository<Sede, Integer> {

	//<Optional> Sede findById(Integer id);

	<List> Sede findByRegione(String regione);

	<List> Sede findByCitta(String citta);

}
