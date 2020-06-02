package com.vivaio_felice.vivaio_hibernate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;

@Controller
public class ChartController {

	@Autowired
	PrenotazioneDao prenoDao;
	@Autowired
	AutoDao autoDao;

	@RequestMapping("/dash")
	public String dash(Model model, HttpSession session) {

		Integer idSede = (Integer) session.getAttribute("sede");
		model.addAttribute("autoInSede", autoDao.autoInSede(idSede, LocalDate.now()));
		return "dashboard";
	}

	@RequestMapping("/provaGrafico")
	public String provaGrafico(Model model, HttpSession session, @RequestParam("auto") Integer idAuto) {

		List<Integer> kmAuto = prenoDao.kmPerGrafico(idAuto, LocalDateTime.now());
		List<Date> dateAuto = prenoDao.dataPerGrafico(idAuto, LocalDateTime.now());

		Map<Object, Object> reportAuto = new LinkedHashMap<Object, Object>();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		reportAuto.put((dateAuto.get(0)), 0);
		for (int i = 1; i < dateAuto.size(); i++) {
			reportAuto.put((dateAuto.get(i)), (kmAuto.get(i) - kmAuto.get(i - 1)));

		}

		System.out.println(reportAuto);
		model.addAttribute("reportAuto", reportAuto);

		return "grafico";

	}

}
