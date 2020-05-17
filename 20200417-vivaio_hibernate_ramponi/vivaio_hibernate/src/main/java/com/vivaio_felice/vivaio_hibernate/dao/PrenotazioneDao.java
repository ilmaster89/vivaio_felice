package com.vivaio_felice.vivaio_hibernate.dao;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
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

	@Query(value = "select * from prenotazioni where dipendente_id = :idDip and causale_id = 3 and km is null order by data_fine desc limit 1", nativeQuery = true)
	public Prenotazione ultima(Integer idDip);

	@Query(value = "select * from prenotazioni where auto_id = :idAuto and km is not null order by id desc limit 1", nativeQuery = true)
	public Prenotazione precedente(Integer idAuto);

}
