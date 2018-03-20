package com.bellis.ioutils;

import java.io.IOException;

public class CopyTask implements Runnable {
	
	String sourceFile;
	String destFile;
	public CopyTask(String sourceFile, String destFile) {
		this.sourceFile = sourceFile;
		this.destFile = destFile;
	}
	
	@Override
	public void run() {
		
		try {
			
			long startTime = System.nanoTime();
			IOUtils.copyFile(sourceFile, destFile);
			long stopTime = System.nanoTime();
		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
