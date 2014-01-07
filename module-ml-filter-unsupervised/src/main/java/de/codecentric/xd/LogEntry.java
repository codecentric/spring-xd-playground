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
