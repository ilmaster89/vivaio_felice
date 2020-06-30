package com.vivaio_felice.vivaio_hibernate.dao;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.PossessoPatenti;

public interface PossessoPatentiDao extends CrudRepository<PossessoPatenti, Integer> {

	Optional<PossessoPatenti> findById(Integer id);

	List<PossessoPatenti> findByDipendenteId(Integer id);

	List<PossessoPatenti> findByPatenteId(Integer id);

	List<PossessoPatenti> findByDataPoss(Date dataPoss);

	@Override
	List<PossessoPatenti> findAll();

	// il dipendente ha già una determinata patente?
	@Query(value = "select * from possesso_patenti where dipendente_id = :idDip and patente_id = :idPat", nativeQuery = true)
	public List<PossessoPatenti> patentePrecedente(Integer idDip, Integer idPat);
}
