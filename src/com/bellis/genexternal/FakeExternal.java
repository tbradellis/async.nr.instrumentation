package com.bellis.genexternal;

import java.net.URI;
import java.net.URISyntaxException;

import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public class FakeExternal {
	
	String a = "ExternalService imitator";
	
	
	
	public FakeExternal() {
		
	}
	
	@Trace(dispatcher = true)
	public String imitator() throws InterruptedException {
		String library = "imitator()";
		URI uri = null;
		try {
			uri = new URI("http://google.com:80");
		}catch (URISyntaxException e) {
	          
			e.printStackTrace();
	    } 
		String procedure = "POST";
		
		
		ExternalParameters params = GenericParameters
				.library(library)
				.uri(uri)
				.procedure(procedure)
				.build();
		
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		
		
		
		Thread.sleep(700);
		return a;
	}

}
