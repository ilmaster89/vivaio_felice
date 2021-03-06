package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Livello;

public interface LivelloDao extends CrudRepository<Livello, Integer> {

	List<Livello> findByMansione(String mansione);

	Optional<Livello> findById(Integer id);

}
