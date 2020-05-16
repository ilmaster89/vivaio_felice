package com.vivaio_felice.vivaio_hibernate.dao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.Auto;
import com.vivaio_felice.vivaio_hibernate.Carburante;
import com.vivaio_felice.vivaio_hibernate.Patente;

@Repository
public class AutoJdbcDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	CarburanteDao carbDao;
	@Autowired
	PatenteDao patDao;

	public List<Auto> autoInSede(Integer idSede) {

		return jdbcTemplate.query(
				"select auto.* from parcheggio join auto on auto.id = parcheggio.auto_id where sede_id = ? and data_parch=?",
				new Object[] { idSede, LocalDate.now() },
				(rs, rowNum) -> new Auto(rs.getInt("id"), patDao.findById(rs.getInt("patente_id")).get(),
						carbDao.findById(rs.getInt("carburante_id")).get(), rs.getString("marca"),
						rs.getString("modello"), rs.getString("targa"), rs.getDouble("kw"), rs.getDouble("tara"),
						rs.getDate("data_assicurazione")));

	}

}
