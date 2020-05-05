package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.SedeDipendente;

public interface SedeDipendenteDao extends CrudRepository<SedeDipendente, Integer> {

	Optional<SedeDipendente> findById(Integer id);

	SedeDipendente findByDipendenteId(Integer id);

	List<SedeDipendente> findBySedeId(Integer id);

}
