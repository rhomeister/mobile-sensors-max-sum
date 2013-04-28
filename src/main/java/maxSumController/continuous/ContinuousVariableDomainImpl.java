package maxSumController.continuous;

import maxSumController.MarginalValues;

import org.apache.commons.lang.Validate;

public class ContinuousVariableDomainImpl implements ContinuousVariableDomain {

	private double upperBound;

	private double lowerBound;

	public ContinuousVariableDomainImpl(double lowerBound, double upperBound) {
		Validate.isTrue(upperBound > lowerBound);
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public MarginalValues<ContinuousVariableState> createZeroMarginalFunction() {
		return new ContinuousMarginalValues(new PieceWiseLinearFunctionImpl(
				new LineSegment(lowerBound, 0, upperBound, 0)));
	}

}
