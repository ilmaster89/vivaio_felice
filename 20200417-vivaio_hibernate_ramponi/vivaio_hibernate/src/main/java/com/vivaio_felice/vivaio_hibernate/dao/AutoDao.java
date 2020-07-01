package com.vivaio_felice.vivaio_hibernate.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Auto;

public interface AutoDao extends CrudRepository<Auto, Integer> {

	@Query("select s from Auto s where id = :id")
	Auto autoDaId(Integer id);

	List<Auto> findByPatenteId(Integer id);

	List<Auto> findByCarburanteId(Integer id);

	List<Auto> findByMarca(String marca);

	List<Auto> findByModello(String modello);

	Auto findByTarga(String targa);

	List<Auto> findByKw(Double kw);

	List<Auto> findByTara(Double tara);

	List<Auto> findByDataAss(Date dataAss);

	@Query(value = "select auto.* from parcheggio join auto on auto.id = parcheggio.auto_id where sede_id = :idSede and disponibilita = 0 and data_parch= date(now())", nativeQuery = true)
	public List<Auto> autoInSede(Integer idSede);

	// conta quante auto sono presenti in VIVAIO FELICE
	@Query(value = "select COUNT(id) from auto where disponibilita = 0", nativeQuery = true)
	public Integer quantitaAuto();

	// quali auto hanno l'assicurazione in scadenza entro il mese?
	@Query(value = "select * from auto where disponibilita = 0 and date_add(data_assicurazione, INTERVAL 1 YEAR) <= date_add(now(), INTERVAL 1 MONTH)", nativeQuery = true)
	public List<Auto> autoInScadenza();

	// auto disponibili in generale (una sorta di findAll con disponibilità)
	@Query(value = "select * from auto where disponibilita = 0", nativeQuery = true)
	List<Auto> autoDisponibili();

}
