package com.vivaio_felice.vivaio_hibernate.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.SpesaManutenzione;

public interface SpesaManutenzioneDao extends CrudRepository<SpesaManutenzione, Integer> {

	@Query("select s from SpesaManutenzione s where id = :id")
	SpesaManutenzione spesaDaId(Integer id);

	List<SpesaManutenzione> findByAutoId(Integer id);

	List<SpesaManutenzione> findBySpesa(Double spesa);

	List<SpesaManutenzione> findByDataSpesa(Date dataSpesa);

	@Query("select s from SpesaManutenzione s where auto_id = :idAuto and spesa is null")
	public List<SpesaManutenzione> manutenzioniSede(Integer idAuto);

	@Query(value = "select spesa from spesa_manutenzione where auto_id = :idAuto and data_spesa between :data1 and :data2 order by data_spesa", nativeQuery = true)
	List<Integer> speseAuto(Integer idAuto, LocalDate data1, LocalDate data2);

	@Query(value = "select data_spesa from spesa_manutenzione where auto_id = :idAuto and data_spesa between :data1 and :data2 order by data_spesa", nativeQuery = true)
	List<Date> dateManutenzione(Integer idAuto, LocalDate data1, LocalDate data2);

}
