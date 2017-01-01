package com.codependent.microservices.parallelization.web;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codependent.microservices.parallelization.client.EchoServiceClient;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@RestController
public class ParallelizationRestController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EchoServiceClient serviceClient;
	
	/**
	 * Two sync requests, no asynchronicity, no parallelization
	 * @return
	 */
	@RequestMapping("/noParallelization")
	public String noParallelization(){
		logger.info("noParallelization()");
		String echo1 = serviceClient.getEcho("Hello");
		String echo2 = serviceClient.getEcho("World!");
		String result = echo1 + "-" + echo2;
		logger.info("noParallelization got [{}]", result);
		logger.info("noParallelization() - exiting()");
		return result;
	}
	
	/**
	 * Just using Hystrix doesn't mean parallelization neither asynchronicity
	 * @return
	 */
	@RequestMapping("/noParallelizationHystrix")
	public String noParallelizationHystrix(){
		logger.info("noParallelizationHystrix()");
		String echo1 = serviceClient.getSyncHystrixEcho("Hello");
		String echo2 = serviceClient.getSyncHystrixEcho("World!");
		String result = echo1 + "-" + echo2;
		logger.info("noParallelization got [{}]", result);
		logger.info("noParallelization() - exiting()");
		return result;
	}
	
	/**
	 * Hystrix and Observable don't involve parallelization by themselves
	 * @return
	 */
	@RequestMapping("/noParallelizationHystrixObservable")
	public Single<String> noParallelizationHystrixObservable(){
		logger.info("noParallelizationHystrixObservable()");
		Observable<String> echo1 = serviceClient.getSyncObservableHystrixEcho("Hello");
		Observable<String> echo2 = serviceClient.getSyncObservableHystrixEcho("World!");
		Observable<String> resultObservable = Observable.zip(echo1, echo2, (String r1, String r2) -> {
			String result = r1 + "-" + r2;
			logger.info("noParallelizationHystrixObservable got [{}]", result);
			return result;
		});
		logger.info("noParallelizationHystrixObservable() - exiting()");
		return resultObservable.toSingle();
	}
	
	/**
	 * Hystrix and a lazy observable don't mean parallelization.
	 * @return
	 */
	@RequestMapping("/noParallelizationHystrixObservable2")
	public Single<String> noParallelizationHystrixObservable2(){
		logger.info("noParallelizationHystrixObservable2()");
		Observable<String> echo1 = serviceClient.getSyncLazyObservableHystrixEcho("Hello");
		Observable<String> echo2 = serviceClient.getSyncLazyObservableHystrixEcho("World!");
		Observable<String> resultObservable = Observable.zip(echo1, echo2, (String r1, String r2) -> {
			String result = r1 + "-" + r2;
			logger.info("noParallelizationHystrixObservable2 got [{}]", result);
			return result;
		});
		logger.info("noParallelizationHystrixObservable2() - exiting()");
		return resultObservable.toSingle();
	}
	
	/**
	 * Due to in the second call, the execution isn't parallelized.
	 * @return
	 */
	@RequestMapping("/noParallelizationHystrixObservable3")
	public Single<String> noParallelizationHystrixObservable3(){
		logger.info("parallelizationHystrixObservable3()");
		Observable<String> echo1 = serviceClient.getSyncLazyObservableHystrixEcho("Hello");
		Observable<String> echo2 = serviceClient.getSyncLazyObservableHystrixEcho("World!")
			.subscribeOn(Schedulers.io());
		
		Observable<String> resultObservable = Observable.zip(echo1, echo2, (String r1, String r2) -> {
			String result = r1 + "-" + r2;
			logger.info("noParallelizationHystrixObservable3 got [{}]", result);
			return result;
		});
		logger.info("noParallelizationHystrixObservable4() - exiting()");
		return resultObservable.toSingle();
	}
	
	/**
	 * Hystrix executed asynchronously with parallelization. The http thread isn't freed
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@RequestMapping("/parallelizationHystrix")
	public String parallelizationHystrix() throws InterruptedException, ExecutionException{
		logger.info("parallelizationHystrix()");
		Future<String> echo1Future = serviceClient.getAsyncHystrixEcho("Hello");
		Future<String> echo2Future = serviceClient.getAsyncHystrixEcho("World!");
		String result = echo1Future.get() + "-" + echo2Future.get();
		logger.info("noParallelization got [{}]", result);
		logger.info("noParallelization() - exiting()");
		return result;
	}
	
	/**
	 * Hystrix executed asynchronously with parallelization freeing the http thread
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@RequestMapping("/parallelizationHystrixCallable")
	public Callable<String> parallelizationHystrixCallable() throws InterruptedException, ExecutionException{
		logger.info("parallelizationHystrixCallable()");
		Future<String> echo1Future = serviceClient.getAsyncHystrixEcho("Hello");
		Future<String> echo2Future = serviceClient.getAsyncHystrixEcho("World!");
		logger.info("noParallelization() - exiting()");
		return () -> {
			String result = echo1Future.get() + "-" + echo2Future.get();
			logger.info("noParallelization got [{}]", result);
			return result;
		};
	}
	
	/**
	 * Hystrix and Observable executed asynchronously and in parallel. It requires that every Observer is async
	 * @return
	 */
	@RequestMapping("/parallelizationHystrixObservable")
	public Single<String> parallelizationHystrixObservable(){
		logger.info("parallelizationHystrixObservable()");
		Observable<String> echo1 = serviceClient.getAsyncObservableHystrixEcho("Hello");
		Observable<String> echo2 = serviceClient.getAsyncObservableHystrixEcho("World!");
		Observable<String> resultObservable = Observable.zip(echo1, echo2, (String r1, String r2) -> {
			String result = r1 + "-" + r2;
			logger.info("parallelizationHystrixObservable got [{}]", result);
			return result;
		});
		logger.info("parallelizationHystrixObservable() - exiting()");
		return resultObservable.toSingle();
	}
	
	/**
	 * Due to .subscribeOn the execution is parallelized in a bad way:
	 * 1 reactive thread for one call, the http thread for the second call
	 * @return
	 */
	@RequestMapping("/parallelizationHystrixObservable2")
	public Single<String> parallelizationHystrixObservable2(){
		logger.info("parallelizationHystrixObservable2()");
		Observable<String> echo1 = serviceClient.getSyncLazyObservableHystrixEcho("Hello")
			.subscribeOn(Schedulers.io());
		Observable<String> echo2 = serviceClient.getSyncLazyObservableHystrixEcho("World!");
		
		Observable<String> resultObservable = Observable.zip(echo1, echo2, (String r1, String r2) -> {
			String result = r1 + "-" + r2;
			logger.info("parallelizationHystrixObservable2 got [{}]", result);
			return result;
		});
		logger.info("parallelizationHystrixObservable2() - exiting()");
		return resultObservable.toSingle();
	}	
	
}
