package de.codecentric.xd.model;

import java.util.Random;

public class DistributionModel {
	
	private ManipulationModel manipulationModel;
	private int meanValue;
	private double variance;
	private Random random = new Random();
	
	public DistributionModel(ManipulationModel manipulationModel,
			int meanValue, double variance) {
		super();
		this.manipulationModel = manipulationModel;
		this.meanValue = meanValue;
		this.variance = variance;
	}

	public int getValue(long currentTimeMillis){
		double duration = meanValue + random.nextGaussian() * variance;
		duration = manipulationModel.getFactor(currentTimeMillis)*duration;
		return duration <= 0? 0:(int)duration;
	}

}
