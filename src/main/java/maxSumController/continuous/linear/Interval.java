package maxSumController.continuous.linear;

public class Interval {
	private double upperBound;

	private double lowerBound;

	public Interval(double lowerBound, double upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public Interval() {
		lowerBound = 0.0;
		upperBound = 0.0;
	}

	public double getUpperbound() {
		return upperBound;
	}

	public double getLowerbound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
	
	@Override
	public String toString() {
		return "[" + lowerBound + ", " + upperBound + "]";
	}
}
