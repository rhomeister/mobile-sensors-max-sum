package maxSumController.continuous;

import maxSumController.MarginalValues;
import maxSumController.discrete.AbstractInternalVariable;

public class ContinuousInternalVariable
		extends
		AbstractInternalVariable<ContinuousVariableDomain, ContinuousVariableState>
		implements ContinuousVariable {

	private static final double SCALER = 4e-3;// 1e-5;

	private MarginalValues<ContinuousVariableState> preference;

	public ContinuousInternalVariable(String name,
			ContinuousVariableDomain domain) {
		super(name, domain);

		PieceWiseLinearFunction function = new PieceWiseLinearFunctionImpl();

		double preferStartAndEnd = SCALER * Math.random();
		double preferMiddle = SCALER * Math.random();
		double domainRange = domain.getUpperBound() - domain.getLowerBound();
		double midPoint = 0.1 * domainRange + 0.8 * domainRange * Math.random();

		function.addSegment(new LineSegment(domain.getLowerBound(),
				preferStartAndEnd, midPoint, preferMiddle));
		function.addSegment(new LineSegment(midPoint, preferMiddle, domain
				.getUpperBound(), preferStartAndEnd));

		// function
		// .addSegment(new LineSegment(domain.getLowerBound(), SCALER
		// * Math.random(), domain.getUpperBound(), SCALER
		// * Math.random()));

		preference = new ContinuousMarginalValues(function);
	}

	public MarginalValues<ContinuousVariableState> getPreference(
			MarginalValues<ContinuousVariableState> marginalValues) {
		ContinuousMarginalValues values = (ContinuousMarginalValues) marginalValues;
		PieceWiseLinearFunction function = new PieceWiseLinearFunctionImpl();

		double current = SCALER * Math.random();

		for (LineSegment segment : values.getFunction().getLineSegments()) {
			double next = SCALER * Math.random();

			function.addSegment(new LineSegment(segment.getX1(), current,
					segment.getX2(), next));
			current = next;
		}

		ContinuousMarginalValues result = new ContinuousMarginalValues(function);

		return preference;

		// return result;

	}

	public void setPreference(
			MarginalValues<ContinuousVariableState> newPreference) {
		preference = newPreference;
	}
}
