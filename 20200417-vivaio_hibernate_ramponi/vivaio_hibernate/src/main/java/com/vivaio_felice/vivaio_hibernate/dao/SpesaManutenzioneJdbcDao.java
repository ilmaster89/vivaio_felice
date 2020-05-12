package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.SpesaManutenzione;

@Repository
public class SpesaManutenzioneJdbcDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	AutoDao autoDao;

	public List<SpesaManutenzione> manutenzioniSede(Integer idAuto) {

		return jdbcTemplate.query("select * from spesa_manutenzione where auto_id = ? and spesa is null",
				new Object[] { idAuto },
				(rs, rowNum) -> new SpesaManutenzione(rs.getInt("id"), autoDao.findById(rs.getInt("auto_id")).get(),
						rs.getDouble("spesa"), rs.getDate("data_spesa"), rs.getString("descrizione"),
						rs.getString("dettaglio")));

	}

}
