package com.bellis.ioutils;

import java.util.concurrent.Callable;

public class MyAddTask implements Callable<Integer> {
	
	int a;
	int b;
	
	public MyAddTask(int a, int b) {
	 	this.a = a;
	 	this.b = b;
	 }
	 public Integer call() {
	 	int result = MyMath.add(a, b);
	 	return result;
	 }
}


