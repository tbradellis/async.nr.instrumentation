package com.bellis.ioutils;
import java.util.concurrent.Callable;

public class MySubtractTask implements Callable<Integer> {

	
	int a;
	int b;
	//@Trace(async = true)
	public MySubtractTask(int a, int b) {
	 	this.a = a;
	 	this.b = b;
	 }
	public Integer call() {
	 	int result = MyMath.subtract(a, b);
	 	return result;
	 }
}

