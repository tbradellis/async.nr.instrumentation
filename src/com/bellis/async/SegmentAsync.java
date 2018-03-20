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
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Trace;

/**
 * Servlet implementation class SegmentAsync
 */
@WebServlet(description = "track async work with with NR Segments API", urlPatterns = { "/SegmentAsync" })
public class SegmentAsync extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Trace(dispatcher = true)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		NewRelic.setTransactionName(null, "/SegmentsTest");
		
		try {
			executor(request, response);
			Thread.sleep(1000);

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Trace(async = true)
	protected void executor(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException, ExecutionException {
		Segment segment_0 = NewRelic.getAgent().getTransaction().startSegment("executorThreadSeg");

		Thread.sleep(1000);
		PrintWriter out = response.getWriter();
		out.println("\nFrom executor method");
		out.println("\nFrom doMath method. 30 + 20 = " + doMath());
		out.println("\nFrom anotherThread method. 40 - 20 = " + anotherThread());		
		segment_0.end();

	}
	@Trace(async = true)
	protected int doMath() throws InterruptedException, ExecutionException {
		Segment segment_2 = NewRelic.getAgent().getTransaction().startSegment("doMathThreadSeg");

		int a = 30;
		int b = 20;
		ExecutorService executeService = Executors.newFixedThreadPool(5);	
		Future<Integer>  future = executeService.submit(new MyAddTask(a,b));
		
		while ( ! future.isDone()) {
			;
		}
	
		int result = future.get();
		Thread.sleep(300);
		segment_2.end();
		return result;
		
		
	}
	@Trace(async = true)
	protected int anotherThread() throws InterruptedException, ExecutionException {
		Segment segment_1 = NewRelic.getAgent().getTransaction().startSegment("anotherThreadSeg");
		int a = 40;
		int b = 20;
		ExecutorService executeService = Executors.newFixedThreadPool(5);	
		Future<Integer>  future = executeService.submit(new MySubtractTask(a,b));
		
		while ( ! future.isDone()) {
			;
		}
		
		int result = future.get();
		Thread.sleep(400);
		segment_1.end();
		return result;
	}
	
	
}
