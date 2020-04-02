package com.vivaio_felice.vivaio_hibernate;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.DipendenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.DipendenteJdbcDao;

@Controller

public class DipendenteController {

	@Autowired
	private DipendenteDao dipendenteDao;
	@Autowired
	private DipendenteJdbcDao dipendenteJdbcRepository;

	@RequestMapping("/")
	public String index() {
		return "login";
	}

	@RequestMapping(value = "/logged", method = RequestMethod.POST)
	public String logged(@RequestParam("user") String user_name, @RequestParam("password") String password,
			Model model, HttpSession session) {
		List<Dipendente> dipList = dipendenteJdbcRepository.login(user_name, password);
		System.out.println(dipList.get(0).toString());

		if (dipList.size() == 0)
			return "redirect:/";
		else {
			session.setAttribute("loggedUser", dipList.get(0));
			return "primapagina";
		}
	}

	@RequestMapping("/primapagina")
	public String primaPagina() {
		return "primapagina";
	}

}
