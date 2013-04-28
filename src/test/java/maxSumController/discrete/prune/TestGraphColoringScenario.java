package maxSumController.discrete.prune;

import junit.framework.TestCase;
import maxSumController.DiscreteInternalFunction;
import maxSumController.VariableNode;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.DiscreteVariableDomainImpl;
import maxSumController.discrete.bb.AbstractBBDiscreteInternalFunction;
import maxSumController.discrete.bb.BBDiscreteMarginalMaximisation;
import maxSumController.io.Color;
import maxSumController.io.SingleVariableConflictFunction;

public class TestGraphColoringScenario extends TestCase {
	private DiscreteMaxSumController<DiscreteInternalFunction> controller;
	private DiscreteInternalVariable<Color> variable1;
	private DiscreteInternalVariable<Color> variable2;
	private DiscreteInternalVariable<Color> variable3;

	@Override
	protected void setUp() throws Exception {
		BBDiscreteMarginalMaximisation.validate = true;

		controller = new DiscreteMaxSumController("agent1",
				new BBDiscreteMarginalMaximisation());

		DiscreteVariableDomain<Color> domain1 = new DiscreteVariableDomainImpl<Color>();
		domain1.add(Color.BLUE);
		domain1.add(Color.RED);
		variable1 = new DiscreteInternalVariable<Color>("v1", domain1);

		DiscreteVariableDomain<Color> domain2 = new DiscreteVariableDomainImpl<Color>();
		domain2.add(Color.GREEN);
		domain2.add(Color.BLUE);
		variable2 = new DiscreteInternalVariable<Color>("v2", domain2);

		DiscreteVariableDomain<Color> domain3 = new DiscreteVariableDomainImpl<Color>();
		domain3.add(Color.GREEN);
		domain3.add(Color.BLUE);
		
		variable3 = new DiscreteInternalVariable<Color>("v3", domain2);

		controller.addInternalVariable(variable1);
		controller.addInternalVariable(variable2);
		controller.addInternalVariable(variable3);

		AbstractBBDiscreteInternalFunction function1 = new SingleVariableConflictFunction(
				"f1", variable1);
		AbstractBBDiscreteInternalFunction function2 = new SingleVariableConflictFunction(
				"f2", variable2);
		AbstractBBDiscreteInternalFunction function3 = new SingleVariableConflictFunction(
				"f3", variable3);

		function1.addVariableDependency(variable1);
		function1.addVariableDependency(variable2);

		function2.addVariableDependency(variable1);
		function2.addVariableDependency(variable2);
		
		controller.addInternalFunction(function1);
		controller.addInternalFunction(function2);

		controller.setMaxSumEnabled(false);
		controller.startPruningAlgorithm();
	}

	public void testPrune() throws Exception {
		controller.calculateNewOutgoingMessages();
		// controller.setMaxSumEnabled(false);
		controller.calculateNewOutgoingMessages();
		// controller.setMaxSumEnabled(false);
		controller.calculateNewOutgoingMessages();
		// controller.setMaxSumEnabled(false);
		controller.calculateNewOutgoingMessages();
		controller.calculateNewOutgoingMessages();
		controller.calculateNewOutgoingMessages();
		controller.calculateNewOutgoingMessages();
		controller.calculateNewOutgoingMessages();
		controller.calculateNewOutgoingMessages();

		System.out.println(controller.computeCurrentState());

		for (VariableNode<?, ?> node : controller.getVariableNodes()) {
			System.out.println(node.getVariable().getDomain());
		}

	}
}
