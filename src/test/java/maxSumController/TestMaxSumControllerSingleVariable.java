package maxSumController;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;

import org.junit.Before;

public class TestMaxSumControllerSingleVariable extends TestCase {

	private DiscreteMaxSumController<DiscreteInternalFunction> maxSumController;

	private DiscreteInternalVariable<Color> va1;

	@Before
	public void setUp() throws Exception {
		Comparable agentIdentifier = "agentA";

		ThreeColorsDomain threeColorsDomain = new ThreeColorsDomain();
		va1 = new DiscreteInternalVariable<Color>("va1", threeColorsDomain);
		threeColorsDomain.add(Color.YELLOW);

		DiscreteInternalFunction fa1 = new PreferenceInternalFunction(
				new ColorPreferenceFunction("fa1"));

		fa1.addVariableDependency(va1);

		maxSumController = new DiscreteMaxSumController(agentIdentifier);

		maxSumController.addInternalVariable(va1);
		maxSumController.addInternalFunction(fa1);
	}

	public void testScenarioSameAgent() throws Exception {
		maxSumController
				.setStoppingCriterion(new FixedIterationStoppingCriterion(100));
		int i = 0;
		while (!maxSumController.stoppingCriterionIsMet()) {
			i++;
			maxSumController.calculateNewOutgoingMessages();
		}

		DiscreteVariableState variableState = maxSumController
				.computeCurrentState().get(va1);

		assertEquals(Color.RED, variableState);
	}

	private class ColorPreferenceFunction extends DiscreteInternalFunction {

		public ColorPreferenceFunction(String name) {
			super(name);
		}

		@Override
		public double evaluate(VariableJointState jointState) {
			DiscreteVariable<?> variable = jointState.getVariables().iterator()
					.next();
			Color variableState = (Color) jointState.get(variable);

			double result = 0.0;

			if (variableState.equals(Color.GREEN)) {
				result = 1.7461846511968027E-4;
			} else if (variableState.equals(Color.RED)) {
				result = 0.10540746818951297;
			} else if (variableState.equals(Color.BLUE)) {
				result = 2.640350408442682E-4;
			} else if (variableState.equals(Color.YELLOW)) {
				result = 0.004192260699952821;
			}

			return result;

		}
	}

}
