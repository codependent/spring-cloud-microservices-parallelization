package com.codependent.microservices.parallelization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@EnableCircuitBreaker
@SpringBootApplication
public class SpringCloudMicroservicesParallelizationApplication {

	@Bean
	public feign.Logger.Level feignLoggerLevel() {
	    return feign.Logger.Level.FULL;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringCloudMicroservicesParallelizationApplication.class, args);
	}
}
