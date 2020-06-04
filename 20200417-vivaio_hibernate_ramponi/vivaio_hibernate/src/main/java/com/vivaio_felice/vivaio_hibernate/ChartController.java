package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;
import com.vivaio_felice.vivaio_hibernate.dao.SpesaManutenzioneDao;

@Controller
public class ChartController {

	@Autowired
	PrenotazioneDao prenoDao;
	@Autowired
	AutoDao autoDao;
	@Autowired
	SpesaManutenzioneDao spesaDao;

	@RequestMapping("/dashKm")
	public String dash(Model model, HttpSession session) {

		Integer idSede = (Integer) session.getAttribute("sede");
		model.addAttribute("autoInSede", autoDao.autoInSede(idSede, LocalDate.now()));
		return "dashKm";
	}

	@RequestMapping(value = "/graficoKm", method = RequestMethod.POST)
	public String graficoKm(Model model, HttpSession session, @RequestParam("auto") Integer idAuto,
			@RequestParam("data1") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data1,
			@RequestParam("data2") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data2) {

		if (data1 == null || data2 == null) {
			Integer idSede = (Integer) session.getAttribute("sede");
			model.addAttribute("autoInSede", autoDao.autoInSede(idSede, LocalDate.now()));
			return "dashKm";
		}

		LocalDateTime ldt1 = LocalDateTime.of(data1, LocalTime.MIDNIGHT);
		LocalDateTime ldt2 = LocalDateTime.of(data2, LocalTime.MIDNIGHT);

		List<Integer> kmAuto = prenoDao.kmPerGrafico(idAuto, ldt1, ldt2);
		List<java.util.Date> dateAuto = prenoDao.dataPerGrafico(idAuto, ldt1, ldt2);

		Map<Object, Object> reportAuto = new LinkedHashMap<Object, Object>();
		Integer kmPrec = prenoDao.kmPrecedenti(idAuto, ldt1);
		Integer sommaKm = 0;
		if (kmPrec == null)
			kmPrec = 0;
		if (!kmAuto.isEmpty()) {
			reportAuto.put((dateAuto.get(0)), kmAuto.get(0) - kmPrec);
			sommaKm += (kmAuto.get(0) - kmPrec);

			for (int i = 1; i < dateAuto.size(); i++) {
				if (kmAuto.get(i) == null)
					reportAuto.put((dateAuto.get(i)), 0);
				if (kmAuto.get(i) != null) {
					reportAuto.put((dateAuto.get(i)), (kmAuto.get(i) - kmAuto.get(i - 1)));
					sommaKm += (kmAuto.get(i) - kmAuto.get(i - 1));
				}
			}
		}

		model.addAttribute("reportAuto", reportAuto);
		model.addAttribute("sommaKm", sommaKm);

		return "graficoKm";

	}

	@RequestMapping("/dashManu")

	public String dashboardManu(HttpSession session, Model model) {
		Integer idSede = (Integer) session.getAttribute("sede");
		model.addAttribute("autoInSede", autoDao.autoInSede(idSede, LocalDate.now()));
		return "dashManu";

	}

	@RequestMapping(value = "/graficoManu", method = RequestMethod.POST)
	public String graficoManutenzione(Model model, HttpSession session, @RequestParam("auto") Integer idAuto,
			@RequestParam("data1") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data1,
			@RequestParam("data2") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data2) {

		List<Integer> spese = spesaDao.speseAuto(idAuto, data1, data2);
		List<Date> date = spesaDao.dateManutenzione(idAuto, data1, data2);
		Map<Object, Object> reportSpese = new LinkedHashMap<Object, Object>();
		Integer sommaSpese = 0;

		if (!spese.isEmpty()) {

			for (int i = 0; i < spese.size(); i++) {

				if (spese.get(i) == null)
					reportSpese.put(date.get(i), 0);
				if (spese.get(i) != null)
					reportSpese.put(date.get(i), spese.get(i));
				sommaSpese = sommaSpese + spese.get(i);

			}

		}

		model.addAttribute("reportSpese", reportSpese);
		model.addAttribute("sommaSpese", sommaSpese);

		return "graficoManu";

	}

}
