package boundedMaxSum;

import maxSumController.visualization.FactorGraphVisualisationUtils;

public class BoundedFactorGraphVisualisationExample {

	public static void main(String[] args) {

		BoundedDiscreteMaxSumController controller = BoundedDiscreteMaxSumUtils
				.createBoundedController();

		FactorGraphVisualisationUtils.visualise(controller);

		try {
			controller.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FactorGraphVisualisationUtils.visualise(controller);

	}

}
