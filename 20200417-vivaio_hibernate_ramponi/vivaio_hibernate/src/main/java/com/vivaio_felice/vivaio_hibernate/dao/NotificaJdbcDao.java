package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.Notifica;

@Repository
public class NotificaJdbcDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	DipendenteDao dipendenteDao;

	public List<Notifica> notificheDip(Integer idDip) {

		return jdbcTemplate.query("select * from notifiche where dipendente_id = ? and conferma = 0",
				new Object[] { idDip },
				(rs, rowNum) -> new Notifica(rs.getInt("id"), dipendenteDao.findById(rs.getInt("dipendente_id")).get(),
						rs.getString("descrizione"), rs.getInt("conferma")));

	}

}
