package com.codependent.microservices.parallelization.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoRestController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping("/echo/{value}")
	public String echo(@PathVariable String value){
		logger.info("Received [{}]", value);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("Returning [{}]", value);
		return value;
	}
	
}
