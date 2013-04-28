package maxSumController.multiball.example;

import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.multiball.MultiballVariableState;
import maxSumController.multiball.TwiceDifferentiableInternalFunction;

public class ExampleMultiballInternalFunction extends
		TwiceDifferentiableInternalFunction {

	private double centreX;
	private double centreY;
	private double maxValue;
	private double bumpSize;
	private boolean initialised;

	private DiscreteInternalVariable<MultiballVariableState> varX;
	private DiscreteInternalVariable<MultiballVariableState> varY;

	private double F(double x) {
		return (1 - (x * x)) * ((1 - x) * (1 - x));
	}

	private double Fprime(double x) {
		return -2 * ((x * (1 - x) * (1 - x)) + ((1 - x) * (1 - (x * x))));
	}

	public ExampleMultiballInternalFunction(String name, double centreX,
			double centreY, double maxValue, double bumpSize) {
		super(name);
		this.centreX = centreX;
		this.centreY = centreY;
		this.maxValue = maxValue;
		this.bumpSize = bumpSize;
		this.initialised = false;
	}

	public void SetVars(DiscreteInternalVariable<MultiballVariableState> varX,
			DiscreteInternalVariable<MultiballVariableState> varY) {
		this.varX = varX;
		this.varY = varY;
		this.initialised = true;
	}

	@Override
	public double evaluateDerivative(VariableJointState state,
			DiscreteInternalVariable<MultiballVariableState> variableToDeriveBy) {
		if (!initialised)
			return 0;

		return derivative(state.get(varX).getValue(), state.get(varY)
				.getValue(), (variableToDeriveBy == varX));
	}

	private double derivative(double Xpos, double Ypos, boolean derivebyX) {

		double factorX = (Xpos - centreX);
		int flipX = 1;
		int flipY = 1;

		if (factorX < 0) {
			factorX = -factorX;
			flipX = -1;
		}
		if (factorX > bumpSize)
			factorX = bumpSize;

		double factorY = (Ypos - centreY);

		if (factorY < 0) {
			factorY = -factorY;
			flipY = -1;
		}
		if (factorY > bumpSize)
			factorY = bumpSize;

		double deriv;

		if (derivebyX) {
			deriv = flipX * maxValue * Fprime(factorX / bumpSize)
					* F(factorY / bumpSize) / bumpSize;
		} else {
			deriv = flipY * maxValue * F(factorX / bumpSize)
					* Fprime(factorY / bumpSize) / bumpSize;
		}

		return deriv;
	}

	@Override
	public double evaluate(VariableJointState state) {
		if (!initialised)
			return 0;

		MultiballVariableState varState = state.get(varX);
		double factorX = (varState.getValue() - centreX);

		if (factorX < 0)
			factorX = -factorX;
		if (factorX > bumpSize)
			factorX = bumpSize;

		varState = state.get(varY);

		double factorY = (varState.getValue() - centreY);

		if (factorY < 0)
			factorY = -factorY;
		if (factorY > bumpSize)
			factorY = bumpSize;

		return maxValue * F(factorX / bumpSize) * F(factorY / bumpSize);
	}

	@Override
	public boolean isContinuous(DiscreteInternalVariable<?> variableToTest) {
		return true;
	}

	@Override
	public double evaluateSecondDerivative(VariableJointState state,
			DiscreteInternalVariable<MultiballVariableState> variableToDeriveBy) {
		if (!initialised)
			return 0;

		double xpos = state.get(varX).getValue();
		double ypos = state.get(varY).getValue();
		double delta = 0.0000001;

		if (variableToDeriveBy == varX) {
			return ((derivative(xpos + delta, ypos, true) - (derivative(xpos,
					ypos, true))) / delta);
		} else {
			return ((derivative(xpos, ypos + delta, true) - (derivative(xpos,
					ypos, true))) / delta);
		}
	}

}
