package com.vivaio_felice.vivaio_hibernate.dao;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Prenotazione;

public interface PrenotazioneDao extends CrudRepository<Prenotazione, Integer> {

	Optional<Prenotazione> findById(Integer id);

	List<Prenotazione> findByDipendenteId(Integer id);

	List<Prenotazione> findByAutoId(Integer id);

	List<Prenotazione> findByCausaleId(Integer id);

	List<Prenotazione> findByDataInizio(Date dataInizio);

	List<Prenotazione> findByDataFine(Date dataFine);

	List<Prenotazione> findByKm(Integer km);
}
