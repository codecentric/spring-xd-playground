package de.codecentric.xd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import de.codecentric.xd.model.DistributionModel;
import de.codecentric.xd.url.UrlCreator;

public class LogFileCreator {
	
	private String host;
	private String application;
	private UrlCreator urlCreator;
	private DistributionModel durationDistributionModel;
	private DistributionModel timeBetweenLogEntriesDistributionModel;
	private String logFileName;
	
	private boolean stop = false;
	
	public LogFileCreator(String host, String application,
			UrlCreator urlCreator, DistributionModel durationDistributionModel,
			DistributionModel timeBetweenLogEntriesDistributionModel,
			String logFileName) {
		super();
		this.host = host;
		this.application = application;
		this.urlCreator = urlCreator;
		this.durationDistributionModel = durationDistributionModel;
		this.timeBetweenLogEntriesDistributionModel = timeBetweenLogEntriesDistributionModel;
		this.logFileName = logFileName;
	}

	private void writeLogEntry(){
		LogEntry logEntry = new LogEntry(
				new Date(),
				host,
				application,
				urlCreator.createUrl(),
				"GET", 
				200, 
				durationDistributionModel.getValue(System.currentTimeMillis()),
                0);
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(logFileName, true)));
			out.println(logEntry.toString());
			out.close();
		} catch (IOException e) {
			throw new RuntimeException("Writing to log file failed.",e);
		} finally {
			if (out != null){
				out.close();
			}
		}
	}
	
	public void writeLogFile(){
		while (!stop){
			writeLogEntry();
			try {
				Thread.sleep(timeBetweenLogEntriesDistributionModel.getValue(System.currentTimeMillis()));
			} catch (InterruptedException e) {
				stop = true;
			}
		}
	}
}
