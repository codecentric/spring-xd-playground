package de.codecentric.xd.url;

import java.util.Random;

public class SimpleUrlCreator implements UrlCreator {
	
	private String baseUrl;
	private int maxRandomValue;
	private int minRandomValue;
	private Random random = new Random();

	public SimpleUrlCreator(String baseUrl, int maxRandomValue,
			int minRandomValue) {
		super();
		this.baseUrl = baseUrl;
		this.maxRandomValue = maxRandomValue;
		this.minRandomValue = minRandomValue;
	}

	public String createUrl() {
		int valueRange = maxRandomValue - minRandomValue;
		int randomValue = random.nextInt(valueRange)+minRandomValue;
		return baseUrl+randomValue;
	}

}
