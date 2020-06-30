package com.vivaio_felice.vivaio_hibernate.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Parcheggio;

public interface ParcheggioDao extends CrudRepository<Parcheggio, Integer> {

	Optional<Parcheggio> findById(Integer id);

	List<Parcheggio> findByAutoId(Integer id);

	List<Parcheggio> findBySedeId(Integer id);

	List<Parcheggio> findByDataParch(LocalDate dataParch);

	@Query("select s from Parcheggio s where auto_id = :idAuto and data_parch = :data")
	public Parcheggio parchDomani(Integer idAuto, LocalDate data);

	@Query(value = "select * from parcheggio where auto_id = :idAuto order by id desc limit 1", nativeQuery = true)
	public Parcheggio ultimoParch(Integer idAuto);

	@Query(value = "select sede_id from parcheggio where auto_id = :idAuto and data_parch = :data", nativeQuery = true)
	public Integer sedeOdierna(Integer idAuto, LocalDate data);

	@Query(value = "select * from parcheggio where auto_id = :idAuto and data_parch between now() and :data1 and sede_id != :idSede", nativeQuery = true)
	public List<Parcheggio> trasferimentoContrastante(Integer idAuto, LocalDateTime data1, Integer idSede);

	@Query(value = "select * from parcheggio where auto_id = :idAuto and data_parch between date(:data1) and date(:data2) and sede_id != :idSede", nativeQuery = true)
	public List<Parcheggio> trasfContrastanti(Integer idAuto, Date data1, Date data2, Integer idSede);
}
