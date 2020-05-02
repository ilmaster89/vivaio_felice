package com.vivaio_felice.vivaio_hibernate.dao;

import java.time.LocalDate;
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

	public List<Auto> autoInSede(Integer idSede) {

		return jdbcTemplate.query(
				"select auto.*, patenti.id as id_pat, patenti.tipologia as tip_pat, carburanti.id as id_car, carburanti.tipologia as tip_car from parcheggio join auto on parcheggio.auto_id = auto.id join sede on parcheggio.sede_id  = sede.id join carburanti on carburanti.id = auto.carburante_id join patenti on patenti.id = auto.patente_id  where parcheggio.sede_id = ? and parcheggio.data_parch = ?",
				new Object[] { idSede, LocalDate.now() },
				(rs, rowNum) -> new Auto(rs.getInt("id"), new Patente(rs.getInt("id_pat"), rs.getString("tip_pat")),
						new Carburante(rs.getInt("id_car"), rs.getString("tip_car")), rs.getString("marca"),
						rs.getString("modello"), rs.getString("targa"), rs.getDouble("kw"), rs.getDouble("tara"),
						rs.getDate("data_assicurazione")));

	}
}
