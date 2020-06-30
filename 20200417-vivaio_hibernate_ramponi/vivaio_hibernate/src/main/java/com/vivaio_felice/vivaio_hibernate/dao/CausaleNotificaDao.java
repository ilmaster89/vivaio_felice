package com.vivaio_felice.vivaio_hibernate.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.CausaleNotifica;

@Repository
public interface CausaleNotificaDao extends CrudRepository<CausaleNotifica, Integer> {

	@Query(value = "select * from causale_notifica where id = :id", nativeQuery = true)
	public CausaleNotifica causaleDaInserire(Integer id);

	@Query(value = "select id from causale_notifica where descrizione like '%Assicurazione%'", nativeQuery = true)
	public Integer notPerAssicurazione();

	@Query(value = "select id from causale_notifica where descrizione like '%Kilometri%'", nativeQuery = true)
	public Integer notPerKilometri();

	@Query(value = "select id from causale_notifica where descrizione like '%Prenotazione%'", nativeQuery = true)
	public Integer notPerPrenotazione();

	@Query(value = "select id from causale_notifica where descrizione like '%Generale%'", nativeQuery = true)
	public Integer notGenerale();

}
