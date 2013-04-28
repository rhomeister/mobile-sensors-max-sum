package maxSumController.multiball.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.DiscreteVariableDomainImpl;
import maxSumController.discrete.VariableJointState;
import maxSumController.multiball.MultiballInternalFunction;
import maxSumController.multiball.MultiballMaxSumController;
import maxSumController.multiball.MultiballVariable;
import maxSumController.multiball.MultiballVariableState;
import maxSumController.multiball.math.PolynomialInterpolation;
import maxSumController.multiball.math.SplineInterpolation;
import maxSumController.multiball.nonlinearoptimiser.GradientMethod;
import maxSumController.multiball.nonlinearoptimiser.InterpolationNonLinearOptimiser;
import maxSumController.multiball.nonlinearoptimiser.NewtonMethod;
import maxSumController.multiball.nonlinearoptimiser.NonLinearOptimiser;

public class Example {
	public static void main(String[] args) {
		exampleRun(false, 0.01, null, "Vanilla");
		exampleRun(true, 0.01, null, "Newton");
		exampleRun(false, 0.0001, new InterpolationNonLinearOptimiser(
				new PolynomialInterpolation()), "Polynomial Interpolation");
		exampleRun(false, 0.0001, new InterpolationNonLinearOptimiser(
				new SplineInterpolation()), "Spline Interpolation");

	}

	public static void exampleRun(boolean newtonMethod, double stepSize,
			InterpolationNonLinearOptimiser INLO, String title) {
		Comparable agentIdentifier = "agentA"; // this is not important

		DiscreteVariableDomain<MultiballVariableState> domain1 = new DiscreteVariableDomainImpl<MultiballVariableState>();

		MultiballVariable va1 = new MultiballVariable("va1", domain1);

		DiscreteVariableDomain<MultiballVariableState> domain2 = new DiscreteVariableDomainImpl<MultiballVariableState>();

		if (INLO != null) {
			double stateval;
			for (stateval = 0; stateval < 8; stateval += 0.4) {
				domain1.add(new MultiballVariableState(stateval));
				domain2.add(new MultiballVariableState(stateval));
			}

		} else {
			domain1.add(new MultiballVariableState(1.0));
			domain1.add(new MultiballVariableState(2.0));
			domain1.add(new MultiballVariableState(3.0));
			domain2.add(new MultiballVariableState(1.0));
			domain2.add(new MultiballVariableState(3.0));
			domain2.add(new MultiballVariableState(5.0));
		}

		MultiballVariable va2 = new MultiballVariable("va2", domain2);

		// create functions with internal maxima close to (1.2, 2.6) and (4, 2)
		ExampleMultiballInternalFunction fa1 = new ExampleMultiballInternalFunction(
				"fa1", 1.2, 2.6, 2.3, 4.0);
		fa1.SetVars(va1, va2);

		fa1.addVariableDependency(va1);
		fa1.addVariableDependency(va2);

		ExampleMultiballInternalFunction fa2 = new ExampleMultiballInternalFunction(
				"fa2", 4.0, 2.0, 2.0, 4.0);
		fa2.SetVars(va1, va2);

		fa2.addVariableDependency(va1);
		fa2.addVariableDependency(va2);

		NonLinearOptimiser optimiser = null;

		if (newtonMethod) {
			optimiser = new NewtonMethod(stepSize);
		} else {
			optimiser = new GradientMethod(stepSize);
		}

		MultiballMaxSumController maxSumController = null;

		if (INLO != null) {
			INLO.setOptimiser(optimiser);
			maxSumController = new MultiballMaxSumController(agentIdentifier,
					INLO);
		} else {
			maxSumController = new MultiballMaxSumController(agentIdentifier,
					optimiser);
		}

		maxSumController.addInternalVariable(va1);
		maxSumController.addInternalVariable(va2);
		maxSumController.addInternalFunction(fa1);
		maxSumController.addInternalFunction(fa2);

		List<Double> values = new ArrayList<Double>();

		for (int i = 0; i < 1000; i++) {
			// the name's a bit unintuitive, but this runs a single iteration of
			// the algorithm
			maxSumController.calculateNewOutgoingMessages();

			values.add(getCurrentStateValue(maxSumController));
			/*
			 * Set<MultiballVariableState> stateSet1 = domain1.getStates();
			 * Set<MultiballVariableState> stateSet2 = domain2.getStates();
			 * 
			 * String sx = "Points x: "; String sy = "Points y: ";
			 * 
			 * 
			 * for(MultiballVariableState domState : stateSet1) { sx = sx +
			 * domState.getValue() + " "; } for(MultiballVariableState domState
			 * : stateSet2) { sy = sy + domState.getValue() + " "; }
			 * System.out.println(sx); System.out.println(sy);
			 */
		}

		XYChartExample.createChart(values, title);

		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentState = maxSumController
				.computeCurrentState();

		MultiballVariableState state1 = (MultiballVariableState) currentState
				.get(va1);
		MultiballVariableState state2 = (MultiballVariableState) currentState
				.get(va2);

		System.out.println("Computed value of va1: " + state1.getValue());
		System.out.println("Computed value of va2: " + state2.getValue());

		System.out.println("Global utility "
				+ getCurrentStateValue(maxSumController));

	}

	private static double getCurrentStateValue(
			MultiballMaxSumController maxSumController) {
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentState = maxSumController
				.computeCurrentState();

		double sum = 0;

		for (MultiballInternalFunction function : maxSumController
				.getInternalFunctions()) {
			sum += function.evaluate(new VariableJointState(currentState));

		}

		return sum;

	}
}
