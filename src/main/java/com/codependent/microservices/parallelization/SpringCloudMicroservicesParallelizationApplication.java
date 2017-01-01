package com.codependent.microservices.parallelization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@EnableCircuitBreaker
@SpringBootApplication
public class SpringCloudMicroservicesParallelizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudMicroservicesParallelizationApplication.class, args);
	}
}
