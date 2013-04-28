package maxSumController.discrete.bb;

import java.io.IOException;

import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.io.GlobalConflictsMetric;
import maxSumController.io.GraphColorReader;

public class RandomGraphColoringProblem {

	public static void main(String[] args) throws IOException {

		GraphColorReader graphColorReader = new GraphColorReader(
				"GCDataGraph-randomColorable.dat", false);

		DiscreteMaxSumController controller = graphColorReader.getController();
		controller.startPruningAlgorithm();

		for (int i = 0; i < 3; i++) {
			controller.calculateNewOutgoingMessages();
			controller.setMaxSumEnabled(false);
			System.out.println("iteration " + i);
			System.out.println(controller.computeCurrentState());
			// System.out.println(globalUtility(controller));
			System.out
					.println(new GlobalConflictsMetric().evaluate(controller));
		}

//		for (VariableNode<?, ?> variable : controller.getVariableNodes()) {
//			System.out.println(variable + " "
//					+ variable.getVariable().getDomain());
//		}
//
//		for (FunctionNode node : controller.getFunctionNodes()) {
//			Set<? extends Node> dependencies = node.getFunction()
//					.getDependencies();
//
//			for (Node node2 : dependencies) {
//				DiscreteInternalVariable<?> variable = (DiscreteInternalVariable<?>) node2;
//
//				System.out.println(variable + " " + variable.getDomain());
//			}
//
//		}

	}
}
