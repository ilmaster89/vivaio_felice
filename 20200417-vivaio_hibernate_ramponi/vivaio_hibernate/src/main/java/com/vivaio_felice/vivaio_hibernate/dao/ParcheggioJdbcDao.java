package com.vivaio_felice.vivaio_hibernate.dao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.Parcheggio;

@Repository
public class ParcheggioJdbcDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	AutoDao autoDao;
	@Autowired
	SedeDao sedeDao;

	public List<Parcheggio> parcheggioDomani(Integer idAuto) {

		return jdbcTemplate.query("select * from parcheggio where auto_id = ? and data_parch = ?",
				new Object[] { idAuto, LocalDate.now().plus(1, ChronoUnit.DAYS) },
				(rs, rowNum) -> new Parcheggio(rs.getInt("id"), autoDao.findById(rs.getInt("auto_id")).get(),
						sedeDao.findById(rs.getInt("sede_id")).get(), rs.getDate("data_parch").toLocalDate()));
	}

}
