package com.vivaio_felice.vivaio_hibernate.dao;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.SpesaManutenzione;

public interface SpesaManutenzioneDao extends CrudRepository<SpesaManutenzione, Integer> {

	Optional<SpesaManutenzione> findById(Integer id);

	List<SpesaManutenzione> findByAutoId(Integer id);

	List<SpesaManutenzione> findBySpesa(Double spesa);

	List<SpesaManutenzione> findByDataSpesa(Date dataSpesa);

	List<SpesaManutenzione> findByDescrizione(String descrizione);

}
