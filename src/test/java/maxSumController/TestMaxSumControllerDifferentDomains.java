package maxSumController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;
import maxSumController.io.ConflictFunction;

import org.apache.commons.collections15.CollectionUtils;
import org.junit.Before;

public class TestMaxSumControllerDifferentDomains extends TestCase {

	private DiscreteMaxSumController maxSumController;

	@Before
	public void setUp() throws Exception {
		Comparable agentIdentifier = "agentA";

		DiscreteInternalVariable<Color> va1 = new DiscreteInternalVariable<Color>(
				"va1", new ThreeColorsDomain());

		ColorDomain twoColorsDomain = new ColorDomain();
		twoColorsDomain.add(Color.BLUE);
		twoColorsDomain.add(Color.YELLOW);
		DiscreteInternalVariable<Color> va2 = new DiscreteInternalVariable<Color>(
				"va2", twoColorsDomain);

		DiscreteInternalFunction fa1 = new PreferenceInternalFunction(
				new ConflictFunction("fa1"));
		DiscreteInternalFunction fa2 = new PreferenceInternalFunction(
				new ConflictFunction("fa2"));

		fa1.addVariableDependency(va1);
		fa1.addVariableDependency(va2);

		fa2.addVariableDependency(va1);
		fa2.addVariableDependency(va2);

		maxSumController = new DiscreteMaxSumController(agentIdentifier);

		maxSumController.addInternalVariable(va1);
		maxSumController.addInternalVariable(va2);
		maxSumController.addInternalFunction(fa1);
		maxSumController.addInternalFunction(fa2);
	}

	public void testScenarioSameAgent() throws Exception {
		maxSumController
				.setStoppingCriterion(new FixedIterationStoppingCriterion(100));
		int i = 0;
		while (!maxSumController.stoppingCriterionIsMet()) {
			i++;
			maxSumController.calculateNewOutgoingMessages();
		}

		Map<DiscreteInternalVariable<?>, DiscreteVariableState> state = maxSumController
				.computeCurrentState();
		List<Color> colors = Arrays.asList(Color.BLUE, Color.YELLOW,
				Color.GREEN, Color.RED);

		for (DiscreteVariableState vs : colors) {
			assertTrue(1 >= CollectionUtils.cardinality(vs, state.values()));
		}

	}

}
