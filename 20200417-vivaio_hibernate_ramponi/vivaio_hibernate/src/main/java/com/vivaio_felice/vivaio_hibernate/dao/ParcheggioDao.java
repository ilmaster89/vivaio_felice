package com.vivaio_felice.vivaio_hibernate.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vivaio_felice.vivaio_hibernate.Parcheggio;

public interface ParcheggioDao extends CrudRepository<Parcheggio, Integer> {

	Optional<Parcheggio> findById(Integer id);

	List<Parcheggio> findByAutoId(Integer id);

	List<Parcheggio> findBySedeId(Integer id);

	List<Parcheggio> findByDataParch(LocalDate dataParch);

}
