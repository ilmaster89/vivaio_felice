package com.vivaio_felice.vivaio_hibernate.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Prenotazione;

public interface PrenotazioneDao extends CrudRepository<Prenotazione, Integer> {

	@Query(value = "select * from prenotazioni where id = :id", nativeQuery = true)
	Prenotazione prenoDaId(Integer id);

//	List<Prenotazione> findByDipendenteId(Integer id);
//
	List<Prenotazione> findByAutoId(Integer id);
//
//	List<Prenotazione> findByCausaleId(Integer id);
//
//	List<Prenotazione> findByDataInizio(Date dataInizio);
//
//	List<Prenotazione> findByDataFine(Date dataFine);
//
//	List<Prenotazione> findByKm(Integer km);

	@Query(value = "select * from prenotazioni where dipendente_id = :idDip and causale_id = 3 and km is null and data_fine <= :data order by data_fine desc limit 1", nativeQuery = true)
	public Prenotazione ultima(Integer idDip, LocalDate data);

	@Query(value = "select * from prenotazioni where auto_id = :idAuto and km is not null order by id desc limit 1", nativeQuery = true)
	public Prenotazione precedente(Integer idAuto);

	@Query(value = "select * from prenotazioni where auto_id = :idAuto", nativeQuery = true)
	public List<Prenotazione> prenoAuto(Integer idAuto);

	@Query(value = "select * from prenotazioni where data_fine <= :data and km is null", nativeQuery = true)
	public List<Prenotazione> prenotazioniPerControlloKm(LocalDate data);

	@Query(value = "select * from prenotazioni where dipendente_id = :idDip and causale_id = 3 and data_inizio >= :data", nativeQuery = true)
	public List<Prenotazione> prenotazioniDelDip(Integer idDip, LocalDateTime data);

	@Query(value = "select km from prenotazioni where auto_id = :idAuto and causale_id = 3 and data_fine between :data1 and :data2 order by data_fine", nativeQuery = true)
	public List<Integer> kmPerGrafico(Integer idAuto, LocalDateTime data1, LocalDateTime data2);

	@Query(value = "select data_fine from prenotazioni where auto_id = :idAuto and causale_id = 3 and data_fine between :data1 and :data2 order by data_fine", nativeQuery = true)
	public List<Date> dataPerGrafico(Integer idAuto, LocalDateTime data1, LocalDateTime data2);

	@Query(value = "select km from prenotazioni where auto_id = :idAuto and data_fine <= :data order by data_fine desc limit 1", nativeQuery = true)
	public Integer kmPrecedenti(Integer idAuto, LocalDateTime data);

	@Query(value = "select * from prenotazioni where auto_id = :idAuto and data_inizio >= now()", nativeQuery = true)
	public List<Prenotazione> prenotazioniFuture(Integer idAuto);

	@Query(value = "select km from prenotazioni where auto_id = :idAuto and data_inizio between :data1 and :data2   and km is not null  limit 1", nativeQuery = true)
	public Integer kmIniziali(Integer idAuto, LocalDateTime data1, LocalDateTime data2);

	@Query(value = "select km from prenotazioni where auto_id = :idAuto  and data_inizio between :data1 and :data2 and km is not null order by id desc limit 1", nativeQuery = true)
	public Integer kmFinali(Integer idAuto, LocalDateTime data1, LocalDateTime data2);

	@Query(value = "select * from prenotazioni where data_inizio between :data and now() and causale_id = 3 order by data_inizio", nativeQuery = true)
	public List<Prenotazione> prenoPassate(LocalDate data);

}
