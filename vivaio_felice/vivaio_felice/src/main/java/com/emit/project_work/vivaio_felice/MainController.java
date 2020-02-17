package com.emit.project_work.vivaio_felice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	@RequestMapping("/")
	public String index() {
		return "home";
	}

	@RequestMapping("/sec")
	public String seconda() {
		return "SecondaPagina";
	}

}
