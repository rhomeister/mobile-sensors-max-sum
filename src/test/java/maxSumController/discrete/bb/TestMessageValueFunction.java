package maxSumController.discrete.bb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.continuous.linear.Interval;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.io.Color;

public class TestMessageValueFunction extends TestCase {

	private MessageValueFunction function;

	private List<DiscreteVariable<Color>> variables;

	private DiscreteInternalVariable<Color> a;

	private DiscreteInternalVariable<Color> b;

	private DiscreteInternalVariable<Color> c;

	private DiscreteInternalVariable<Color> d;

	private HashMap<Variable<?, ?>, MarginalValues<?>> sortedMessages;

	@Override
	protected void setUp() throws Exception {

		DiscreteVariable<?> targetVariable;

		sortedMessages = new HashMap<Variable<?, ?>, MarginalValues<?>>();

		a = new DiscreteInternalVariable<Color>("a",
				TestPartialJointVariableState.createDomain(4));
		b = new DiscreteInternalVariable<Color>("b",
				TestPartialJointVariableState.createDomain(2));
		c = new DiscreteInternalVariable<Color>("c",
				TestPartialJointVariableState.createDomain(3));
		d = new DiscreteInternalVariable<Color>("d",
				TestPartialJointVariableState.createDomain(1));

		variables = new ArrayList<DiscreteVariable<Color>>();
		variables.add(a);
		variables.add(b);
		variables.add(c);
		variables.add(d);

		DiscreteMarginalValues<Color> aValues = new DiscreteMarginalValues<Color>();
		aValues.getValues().put(new Color("0"), 4.0);
		aValues.getValues().put(new Color("1"), 6.0);
		aValues.getValues().put(new Color("2"), 2.0);
		aValues.getValues().put(new Color("3"), 1.0);

		sortedMessages.put(a, aValues);

		DiscreteMarginalValues<Color> bValues = new DiscreteMarginalValues<Color>();
		bValues.getValues().put(new Color("0"), 1.0);
		bValues.getValues().put(new Color("1"), 2.0);
		sortedMessages.put(b, bValues);

		DiscreteMarginalValues<Color> cValues = new DiscreteMarginalValues<Color>();
		cValues.getValues().put(new Color("0"), 3.0);
		cValues.getValues().put(new Color("1"), 9.0);
		cValues.getValues().put(new Color("2"), 2.0);
		sortedMessages.put(c, cValues);

		DiscreteMarginalValues<Color> dValues = new DiscreteMarginalValues<Color>();
		dValues.getValues().put(new Color("0"), 3.0);
		sortedMessages.put(d, dValues);

		function = new MessageValueFunction(sortedMessages, a);
	}

	public void testname() throws Exception {
		PartialJointVariableState<Color> state = new PartialJointVariableState<Color>(
				variables, a, new Color("0"));

		Interval valueInterval = function.getValueInterval(state);

		assertEquals(6.0, valueInterval.getLowerbound());
		assertEquals(14.0, valueInterval.getUpperbound());

		state = state.getChildren().iterator().next();

		valueInterval = function.getValueInterval(state);

		assertEquals(7.0, valueInterval.getLowerbound());
		assertEquals(14.0, valueInterval.getUpperbound());
		
		// c becomes color "2"
		state = state.getChildren().iterator().next();
		valueInterval = function.getValueInterval(state);

		assertEquals(7.0, valueInterval.getLowerbound());
		assertEquals(7.0, valueInterval.getUpperbound());
	}
}
