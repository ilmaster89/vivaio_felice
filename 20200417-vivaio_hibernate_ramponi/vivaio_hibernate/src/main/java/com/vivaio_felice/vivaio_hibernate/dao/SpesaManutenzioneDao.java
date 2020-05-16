package com.vivaio_felice.vivaio_hibernate.dao;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.SpesaManutenzione;

public interface SpesaManutenzioneDao extends CrudRepository<SpesaManutenzione, Integer> {

	Optional<SpesaManutenzione> findById(Integer id);

	List<SpesaManutenzione> findByAutoId(Integer id);

	List<SpesaManutenzione> findBySpesa(Double spesa);

	List<SpesaManutenzione> findByDataSpesa(Date dataSpesa);

	@Query("select s from SpesaManutenzione s where auto_id = :idAuto and spesa is null")
	public List<SpesaManutenzione> manutenzioniSede(Integer idAuto);

}
