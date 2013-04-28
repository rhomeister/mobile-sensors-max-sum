package maxSumController.visualization;

import maxSumController.discrete.DiscreteMaxSumController;
import boundedMaxSum.BoundedDiscreteMaxSumUtils;

public class FactorGraphVisualiserExample {

	public static void main(String[] args) {

		DiscreteMaxSumController<?> controller = BoundedDiscreteMaxSumUtils
				.createBoundedController();

		FactorGraphVisualisationUtils.visualise(controller);
	}

}
