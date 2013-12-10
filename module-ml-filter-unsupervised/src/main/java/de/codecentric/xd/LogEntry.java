package de.codecentric.xd;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class LogEntry {
	
	private Date date;
	private String host;
	private String application;
	private String url;
	private String httpMethod;
	private int httpStatusCode;
	private int duration;

	public LogEntry(String logEntry) {
		// TODO add error handling
		String[] splittedEntry = logEntry.split("#");
		try {
			date = DateFormat.getDateTimeInstance().parse(splittedEntry[0]);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Problem with parsing date.",e);
		}
		host = splittedEntry[1];
		application = splittedEntry[2];
		url = splittedEntry[3];
		httpMethod = splittedEntry[4];
		httpStatusCode = Integer.parseInt(splittedEntry[5]);
		duration = Integer.parseInt(splittedEntry[6]);
	}

	public LogEntry(Date date, String host, String application, String url, 
			String httpMethod, int httpStatusCode, int duration) {
		this.date = date;
		this.host = host;
		this.application = application;
		this.url = url;
		this.httpMethod = httpMethod;
		this.httpStatusCode = httpStatusCode;
		this.duration = duration;
	}

	public Date getDate() {
		return date;
	}

	public String getHost() {
		return host;
	}

	public String getUrl() {
		return url;
	}

	public String getApplication() {
		return application;
	}

	public int getDuration() {
		return duration;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
	public String toString(){
		String dateString = DateFormat.getDateTimeInstance().format(date);
		return dateString+"#"+host+"#"+application+"#"+url+"#"+httpMethod+"#"+httpStatusCode+"#"+duration;  
	}

}
