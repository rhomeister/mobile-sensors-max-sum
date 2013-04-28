package maxSumController;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.io.Color;

public class TestDiscreteFunction extends TestCase {

	private DiscreteMarginalValues<Color> emptyFunction;

	private DiscreteMarginalValues<Color> function1;

	private DiscreteMarginalValues<Color> function2;

	@Override
	protected void setUp() throws Exception {
		emptyFunction = new DiscreteMarginalValues<Color>();

		HashMap<Color, Double> values1 = new HashMap<Color, Double>();
		values1.put(Color.RED, 1.0);
		values1.put(Color.GREEN, 5.0);
		function1 = new DiscreteMarginalValues<Color>(values1);

		HashMap<Color, Double> values2 = new HashMap<Color, Double>();
		values2.put(Color.RED, 4.0);
		values2.put(Color.BLUE, 3.0);
		function2 = new DiscreteMarginalValues<Color>(values2);
	}

	public void testAdd() throws Exception {
		assertEquals(function1, emptyFunction.add(function1));

		DiscreteMarginalValues<Color> result = function1.add(function2);

		Map<Color, Double> resultValues = result.getValues();

		Map<DiscreteVariableState, Double> expectedValues = new HashMap<DiscreteVariableState, Double>();
		expectedValues.put(Color.RED, 5.0);
		expectedValues.put(Color.GREEN, 5.0);
		expectedValues.put(Color.BLUE, 3.0);

		assertEquals(expectedValues, resultValues);
	}

	public void testNormalise() throws Exception {
		emptyFunction.normalise();

		function1.normalise();
		Map<DiscreteVariableState, Double> expectedValues = new HashMap<DiscreteVariableState, Double>();
		expectedValues.put(Color.RED, -2.0);
		expectedValues.put(Color.GREEN, 2.0);
		assertEquals(expectedValues, function1.getValues());
		assertEquals(0.0, function1.getSumOfValues());
	}

	public void testArgMax() throws Exception {
		assertEquals(Color.GREEN, function1.argMax());
		assertEquals(Color.RED, function2.argMax());
	}
}
