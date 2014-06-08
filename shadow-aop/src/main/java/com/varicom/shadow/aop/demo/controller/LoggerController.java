package com.varicom.shadow.aop.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoggerController {

	@RequestMapping(value = "/logger", method = RequestMethod.GET)
	public void testLogger() {

		System.err.println("==========/logger==========");

//		return "redirect:/abc";
	}

	@RequestMapping(value = "/abc", method = RequestMethod.GET)
	public void testLoggerr() {
		System.err.println("==========/adc==========");
	}
}