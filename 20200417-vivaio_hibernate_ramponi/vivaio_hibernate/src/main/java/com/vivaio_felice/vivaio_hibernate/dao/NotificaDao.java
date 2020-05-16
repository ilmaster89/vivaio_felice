package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Notifica;

public interface NotificaDao extends CrudRepository<Notifica, Integer> {

	Optional<Notifica> findById(Integer id);

	List<Notifica> findByDipendenteId(Integer idDip);

	List<Notifica> findByConferma(Integer conferma);

	@Query("select s from Notifica s where dipendente_id = :idDip and conferma = 0")
	public List<Notifica> notificheDip(Integer idDip);

}
