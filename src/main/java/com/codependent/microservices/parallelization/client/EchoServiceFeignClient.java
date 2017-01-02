package com.codependent.microservices.parallelization.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.netflix.hystrix.HystrixCommand;

import rx.Single;

@FeignClient(value="feignEchoService", url="http://localhost:8080")
@Component
public interface EchoServiceFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = "/echo/{value}")
    String getSyncEcho(@PathVariable("value") String value);
	
	@RequestMapping(method = RequestMethod.GET, value = "/echo/{value}")
	HystrixCommand<String> getAsyncHystrixEcho(@PathVariable("value") String value);

	@RequestMapping(method = RequestMethod.GET, value = "/echo/{value}")
    Single<String> getAsyncHystrixReactiveEcho(@PathVariable("value") String value);
}
