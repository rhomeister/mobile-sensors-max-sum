package maxSumController;

import maxSumController.discrete.DiscreteMaxSumController;

public class MaxSumSettings {

	private int maxSumIterations;

	private boolean useGlobalPruning;

	// private boolean useCommunicationConstraint;

	// private boolean useValuePropagation;

	// private int valuePropagationIterations;

	// private int valuePropagationIterationsTotal = 20;

	public MaxSumSettings() {
	}

	public void applySettings(DiscreteMaxSumController controller) {
		controller.setStoppingCriterion(new FixedIterationStoppingCriterion(
				maxSumIterations));

		// if (useGlobalPruning)
		// controller.startPruningAlgorithm();
	}

	// @Required
	public void setUseGlobalPruning(boolean useGlobalPruning) {
		this.useGlobalPruning = useGlobalPruning;
	}

	// @Required
	public void setMaxSumIterations(int maxSumIterations) {
		this.maxSumIterations = maxSumIterations;
	}

	public boolean isUseGlobalPruning() {
		return useGlobalPruning;
	}

}
