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
 * Servlet implementation class TokenAsync
 */
@WebServlet(description = "track async work with with NR Tokens API", urlPatterns = { "/TokenAsync" })
public class TokenAsync extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Trace(dispatcher = true)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		NewRelic.setTransactionName(null, "/TokenTest");
		final Token token = NewRelic.getAgent().getTransaction().getToken();
		
		try {
			executor(request, response, token);
			Thread.sleep(1000);

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		token.expire();

		
	}
	
	@Trace(async = true)
	protected void executor(HttpServletRequest request, HttpServletResponse response, Token token) throws IOException, InterruptedException, ExecutionException {
		token.link();
		Thread.sleep(1000);
		PrintWriter out = response.getWriter();
		out.println("\nFrom executor method");
		out.println("\nFrom doMath method. 100 + 200 = " + doMath(token));
		out.println("\nFrom anotherThread method. 300 - 200 = " + anotherThread(token));
		
	}
	
	@Trace(async = true)
	protected int doMath(Token token) throws InterruptedException, ExecutionException {
		token.link();

		int a = 100;
		int b = 200;
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

		int a = 300;
		int b = 200;
		ExecutorService executeService = Executors.newFixedThreadPool(5);	
		Future<Integer>  future = executeService.submit(new MySubtractTask(a,b));
		
		while ( ! future.isDone()) {
			;
		}
		
		int result = future.get();
		Thread.sleep(400);
		return result;
	}
	
	
}

