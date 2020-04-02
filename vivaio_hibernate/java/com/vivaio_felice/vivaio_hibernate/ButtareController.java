package com.vivaio_felice.vivaio_hibernate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/buttare")
public class ButtareController {

	@RequestMapping("/")
	public String index() {
		return "primapagina";
	}

}
