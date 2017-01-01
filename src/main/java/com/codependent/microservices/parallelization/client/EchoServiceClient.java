package com.codependent.microservices.parallelization.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

import rx.Observable;
import rx.schedulers.Schedulers;

@Component
public class EchoServiceClient {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private RestTemplate restTemplate = new RestTemplate();
	
	public String getEcho(String value){
		return doGetEcho(value);
	}
	
	@HystrixCommand
	public String getSyncHystrixEcho(String value){
		return doGetEcho(value);
	}
	
	@HystrixCommand
	public Future<String> getAsyncHystrixEcho(String value){
		return new AsyncResult<String>() {
			@Override
			public String invoke() {
				return doGetEcho(value);
			}
		};
	}
	
	@HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER)
	public Observable<String> getSyncObservableHystrixEcho(String value){
		return Observable.fromCallable(() -> {
			return doGetEcho(value);
		});
	}

	@HystrixCommand(observableExecutionMode = ObservableExecutionMode.LAZY)
	public Observable<String> getSyncLazyObservableHystrixEcho(String value){
		return Observable.fromCallable(() -> {
			return doGetEcho(value);
		});
	}
	
	@HystrixCommand
	public Observable<String> getAsyncObservableHystrixEcho(String value){
		return Observable.fromCallable(() -> {
			return doGetEcho(value);
		}).subscribeOn(Schedulers.io());
	}
	
	private String doGetEcho(String value){
		String response = null;
		try {
			logger.info("Client requesting echo for [{}]", value);
			response = restTemplate.getForObject(new URI("http://localhost:8080/echo/"+value), String.class);
		} catch (RestClientException | URISyntaxException e) {
			e.printStackTrace();
		}
		logger.info("Client got [{}]", response);
		return response;
	}
}
