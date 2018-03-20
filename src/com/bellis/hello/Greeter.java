package com.bellis.hello;

import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.MessageProduceParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.OutboundHeaders;

public class Greeter {
	
	static String a = "Hello World";
	String b = "Good morning!";
	String c = "Good afternoon!";
	
	public Greeter() {
				
	}
	
	public String morningGreet() {
		return b;
	}
	
	public String afternoonGreet() {
		return c;
	}
	
	
	@Trace(dispatcher=true)
	public String sleepyGreeter() throws InterruptedException {
		String library = "sleepyGreeter()";
		String destination = "MyMessageQue";
		
		ExternalParameters parameters = MessageProduceParameters
				.library(library)
				.destinationType(DestinationType.NAMED_QUEUE)
				.destinationName(destination)
				.outboundHeaders(new OutboundHeaders() {
					@Override
					public void setHeader(String arg0, String arg1) {
							// stub
					}						
					public HeaderType getHeaderType() {
						return HeaderType.MESSAGE;
					}})
				.build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(parameters);
		Thread.sleep(500);
		String h = "hello there!";
		
		return h;
		
	}
}
