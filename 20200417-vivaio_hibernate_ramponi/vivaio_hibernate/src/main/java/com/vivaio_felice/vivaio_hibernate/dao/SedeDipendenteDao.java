package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.SedeDipendente;

public interface SedeDipendenteDao extends CrudRepository<SedeDipendente, Integer> {

	Optional<SedeDipendente> findById(Integer id);

	SedeDipendente findByDipendenteId(Integer id);

	List<SedeDipendente> findBySedeId(Integer id);

	@Query(value = "select dipendente_id from sede_dip where sede_id = :idSede", nativeQuery = true)
	List<Integer> dipendentiInSede(Integer idSede);

}
