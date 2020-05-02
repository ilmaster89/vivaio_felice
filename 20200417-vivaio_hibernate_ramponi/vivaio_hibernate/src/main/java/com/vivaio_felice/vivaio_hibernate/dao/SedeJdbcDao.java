package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.Sede;

@Repository
public class SedeJdbcDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Sede> sediEccetto(Integer idSede) {

		return jdbcTemplate.query("select * from vivaio_felice.sede where id != ?", new Object[] { idSede },
				(rs, nowNum) -> new Sede(rs.getInt("id"), rs.getString("regione"), rs.getString("citta")));
	}

}
