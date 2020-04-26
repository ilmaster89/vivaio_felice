package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Carburante;

public interface CarburanteDao extends CrudRepository<Carburante, Integer> {

	Optional<Carburante> findById(Integer id);

	Carburante findByTipologia(String tipologia);

}
