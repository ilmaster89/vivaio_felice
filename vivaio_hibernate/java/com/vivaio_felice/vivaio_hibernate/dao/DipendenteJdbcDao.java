package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.Dipendente;
import com.vivaio_felice.vivaio_hibernate.Livello;

@Repository
public class DipendenteJdbcDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Dipendente> login(String user_name, String password) {

		return jdbcTemplate.query(
				"select * from dipendenti join livelli on dipendenti.id_livello = livelli.id where user_name = ? and password = ?",
				new Object[] { user_name, password },
				(rs, rowNum) -> new Dipendente(rs.getInt("dipendenti.id"),
						new Livello(rs.getInt("livelli.id"), rs.getString("mansione")), rs.getString("nome"),
						rs.getString("cognome"), rs.getString("user_name"), rs.getString("password")));

	}

}
