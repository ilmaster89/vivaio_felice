package com.vivaio_felice.vivaio_hibernate.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Auto;

public interface AutoDao extends CrudRepository<Auto, Integer> {

	Optional<Auto> findById(Integer id);

	List<Auto> findByPatenteId(Integer id);

	List<Auto> findByCarburanteId(Integer id);

	List<Auto> findByMarca(String marca);

	List<Auto> findByModello(String modello);

	Auto findByTarga(String targa);

	List<Auto> findByKw(Double kw);

	List<Auto> findByTara(Double tara);

	List<Auto> findByDataAss(Date dataAss);

	@Query(value = "select auto.* from parcheggio join auto on auto.id = parcheggio.auto_id where sede_id = :idSede and data_parch= :data", nativeQuery = true)
	public List<Auto> autoInSede(Integer idSede, LocalDate data);
}
