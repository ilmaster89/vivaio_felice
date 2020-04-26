package com.vivaio_felice.vivaio_hibernate.dao;

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
				"select auto.*, patenti.id as id_pat, patenti.tipologia as tip_pat, carburanti.id as id_car, carburanti.tipologia as tip_car from parcheggio as p1 \r\n"
						+ "left outer join parcheggio as p2 on p1.auto_id=p2.auto_id and p1.data_parch < p2.data_parch \r\n"
						+ "join auto on p1.auto_id = auto.id \r\n" + "join sede on sede.id = p1.sede_id\r\n"
						+ "join patenti on auto.patente_id = patenti.id\r\n"
						+ "join carburanti on auto.carburante_id = carburanti.id\r\n"
						+ "where p2.id is null and p1.sede_id = ?",
				new Object[] { idSede },
				(rs, rowNum) -> new Auto(rs.getInt("id"), new Patente(rs.getInt("id_pat"), rs.getString("tip_pat")),
						new Carburante(rs.getInt("id_car"), rs.getString("tip_car")), rs.getString("marca"),
						rs.getString("modello"), rs.getString("targa"), rs.getDouble("kw"), rs.getDouble("tara"),
						rs.getDate("data_assicurazione")));

	}
}
