package com.bellis.async;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bellis.ioutils.MyAddTask;
import com.bellis.ioutils.MySubtractTask;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
/**
 * Servlet implementation class AlgorithmAsync
 */
@WebServlet(description = "For testing various algorithms with multiple threads",
		urlPatterns = { "/AlgorithmAsync" },
		asyncSupported = true)


public class AlgorithmAsync extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//PrintWriter out = response.getWriter();
		
		try {
			executor(request, response);
			//out.println("\nFrom doMath method. 10 + 20 = " + doMath());
			//out.println("\nFrom anotherThread method. 10 - 20 = " + anotherThread());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Trace(dispatcher = true)
	protected void executor(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException, ExecutionException {
		final Token token = NewRelic.getAgent().getTransaction().getToken();

		NewRelic.setTransactionName(null, "/Banana");
		Thread.sleep(1000);
		PrintWriter out = response.getWriter();
		out.println("\nFrom executor method");
		out.println("\nFrom doMath method. 10 + 20 = " + doMath());
		out.println("\nFrom anotherThread method. 10 - 20 = " + anotherThread(token));
		token.expire();
		
		
	}
	protected int doMath() throws InterruptedException, ExecutionException {
		int a = 10;
		int b = 20;
		ExecutorService executeService = Executors.newFixedThreadPool(5);	
		
		//Question - Theoretically, how would you instrument the call that happens from the Future
		//That's inside my MyAddTask
		Future<Integer>  future = executeService.submit(new MyAddTask(a,b));
		
		while ( ! future.isDone()) {
			;
		}
		
		
		int result = future.get();
		Thread.sleep(300);

		return result;
		
		
	}
	@Trace(async = true)
	protected int anotherThread(Token token) throws InterruptedException, ExecutionException {
		token.link();

		//Segment segment_0 = NewRelic.getAgent().getTransaction().startSegment("anotherThreadSeg");
		int a = 10;
		int b = 20;
		ExecutorService executeService = Executors.newFixedThreadPool(5);	
		Future<Integer>  future = executeService.submit(new MySubtractTask(a,b));
		
		while ( ! future.isDone()) {
			;
		}
		
		int result = future.get();
		Thread.sleep(400);
		//segment_0.end();
		return result;
	}
	
	
}
