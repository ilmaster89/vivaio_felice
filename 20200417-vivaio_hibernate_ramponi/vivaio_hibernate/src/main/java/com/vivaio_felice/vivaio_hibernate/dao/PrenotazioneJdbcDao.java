package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.Prenotazione;

@Repository
public class PrenotazioneJdbcDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Prenotazione> precedente(Integer idAuto) {

		return jdbcTemplate
				.query("select * from prenotazioni where auto_id = ? and km is not null" + " order by km desc limit 1",
						new Object[] { idAuto },
						(rs, rowNum) -> new Prenotazione(rs.getInt("id"), rs.getInt("dipendente_id"),
								rs.getInt("auto_id"), rs.getInt("causale_id"), rs.getDate("data_inizio"),
								rs.getDate("data_fine"), rs.getInt("km")));

	}

}
