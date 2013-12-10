package de.codecentric.xd.model;

public class SimpleManipulationModel implements ManipulationModel {
	
	private double factor;

	public SimpleManipulationModel(double factor) {
		super();
		this.factor = factor;
	}

	public double getFactor(long currentTimeMillis) {
		return factor;
	}

}
