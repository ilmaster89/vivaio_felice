package com.vivaio_felice.vivaio_hibernate.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vivaio_felice.vivaio_hibernate.Livello;

@Repository
public class LivelloJdbcDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Livello> treLivelli() {

		return jdbcTemplate.query("select * from livelli where id != 4",
				(rs, rowNum) -> new Livello(rs.getInt("id"), rs.getString("mansione")));

	}

}
