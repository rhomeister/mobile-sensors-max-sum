package maxSumController.discrete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.DiscreteVariableState;
import maxSumController.continuous.linear.Interval;
import maxSumController.discrete.prune.FunctionToVariablePruneMessageContents;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;

public class TestDiscreteVariableNode extends TestCase {

	private DiscreteVariableNode<Color> node;

	@Override
	protected void setUp() throws Exception {
		DiscreteVariableDomainImpl<Color> domain = new ThreeColorsDomain();
		DiscreteInternalVariable<Color> variable = new DiscreteInternalVariable<Color>(
				"dummy", domain);
		node = new DiscreteVariableNode<Color>(variable);
	}

	public void testComputeNewDomainTightBounds() throws Exception {

		Collection<FunctionToVariablePruneMessageContents> values = new ArrayList<FunctionToVariablePruneMessageContents>();

		Map<DiscreteVariableState, Interval> bounds = new HashMap<DiscreteVariableState, Interval>();
		bounds.put(Color.BLUE, new Interval(0.0, 0.0));
		bounds.put(Color.RED, new Interval(0.0, 0.0));
		bounds.put(Color.GREEN, new Interval(0.0, 0.0));

		values.add(new FunctionToVariablePruneMessageContents(bounds));

		bounds = new HashMap<DiscreteVariableState, Interval>();
		bounds.put(Color.BLUE, new Interval(1.0, 1.0));
		bounds.put(Color.RED, new Interval(1.0, 1.0));
		bounds.put(Color.GREEN, new Interval(1.0, 1.0));
		values.add(new FunctionToVariablePruneMessageContents(bounds));

		assertEquals(1, node.computeNewDomain(values).size());
	}

	public void testComputeNewDomainOneStateDominated() throws Exception {

		Collection<FunctionToVariablePruneMessageContents> values = new ArrayList<FunctionToVariablePruneMessageContents>();

		Map<DiscreteVariableState, Interval> bounds = new HashMap<DiscreteVariableState, Interval>();
		bounds.put(Color.BLUE, new Interval(0.0, 1.0));
		bounds.put(Color.RED, new Interval(3.0, 4.0));
		bounds.put(Color.GREEN, new Interval(0.0, 5.0));

		values.add(new FunctionToVariablePruneMessageContents(bounds));

		bounds = new HashMap<DiscreteVariableState, Interval>();
		bounds.put(Color.BLUE, new Interval(0.0, 4.0));
		bounds.put(Color.RED, new Interval(4.0, 6.0));
		bounds.put(Color.GREEN, new Interval(5.0, 9.0));
		values.add(new FunctionToVariablePruneMessageContents(bounds));

		Set<Color> newDomain = node.computeNewDomain(values);
		assertEquals(2, newDomain.size());
		assertFalse(newDomain.contains(Color.BLUE));
		assertTrue(newDomain.contains(Color.GREEN));
		assertTrue(newDomain.contains(Color.RED));
	}

	public void testComputeNewDomainNoStatesDominated() throws Exception {

		Collection<FunctionToVariablePruneMessageContents> values = new ArrayList<FunctionToVariablePruneMessageContents>();

		Map<DiscreteVariableState, Interval> bounds = new HashMap<DiscreteVariableState, Interval>();
		bounds.put(Color.BLUE, new Interval(0.0, 1.0));
		bounds.put(Color.RED, new Interval(0.0, 4.0));
		bounds.put(Color.GREEN, new Interval(0.0, 5.0));

		values.add(new FunctionToVariablePruneMessageContents(bounds));

		bounds = new HashMap<DiscreteVariableState, Interval>();
		bounds.put(Color.BLUE, new Interval(0.0, 4.0));
		bounds.put(Color.RED, new Interval(-1.0, 6.0));
		bounds.put(Color.GREEN, new Interval(2.0, 9.0));
		values.add(new FunctionToVariablePruneMessageContents(bounds));

		Set<Color> newDomain = node.computeNewDomain(values);
		assertEquals(3, newDomain.size());
		assertTrue(newDomain.contains(Color.BLUE));
		assertTrue(newDomain.contains(Color.GREEN));
		assertTrue(newDomain.contains(Color.RED));
	}

}
