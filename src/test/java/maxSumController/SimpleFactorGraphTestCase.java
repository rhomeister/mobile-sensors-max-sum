package maxSumController;

import java.util.Collection;

import junit.framework.TestCase;
import maxSumController.communication.Message;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ConflictFunction;

import org.junit.Before;

public abstract class SimpleFactorGraphTestCase extends TestCase {

	protected DiscreteMaxSumController<DiscreteInternalFunction> maxSumController;

	protected DiscreteInternalVariable<Color> va1;

	protected DiscreteInternalVariable<Color> va2;

	protected DiscreteInternalVariable<Color> va3;

	protected DiscreteInternalFunction fa1;

	protected DiscreteInternalFunction fa2;

	protected DiscreteInternalFunction fa3;

	@Before
	public void setUp() throws Exception {
		Comparable agentIdentifier = "agentA";

		va1 = new DiscreteInternalVariable<Color>("va1",
				new ThreeColorsDomain());
		va2 = new DiscreteInternalVariable<Color>("va2",
				new ThreeColorsDomain());
		va3 = new DiscreteInternalVariable<Color>("va3",
				new ThreeColorsDomain());

		fa1 = new PreferenceInternalFunctionSingleVariable(
				new ConflictFunction("fa1"), va1);
		fa2 = new PreferenceInternalFunctionSingleVariable(
				new ConflictFunction("fa2"), va2);

		fa3 = new PreferenceInternalFunctionSingleVariable(
				new ConflictFunction("fa3"), va2);

		fa1.addVariableDependency(va1);
		fa1.addVariableDependency(va2);

		fa2.addVariableDependency(va1);
		fa2.addVariableDependency(va2);
		fa2.addVariableDependency(va3);

		fa3.addVariableDependency(va2);
		fa3.addVariableDependency(va3);

		maxSumController = new DiscreteMaxSumController(agentIdentifier);

		maxSumController.addInternalVariable(va1);
		maxSumController.addInternalVariable(va2);
		maxSumController.addInternalVariable(va3);
		maxSumController.addInternalFunction(fa1);
		maxSumController.addInternalFunction(fa2);
		maxSumController.addInternalFunction(fa3);
	}
	
	protected <T extends Message> Collection<T> getMessagesForNode(FactorGraphNode node,
			Class<T> type) {
		return maxSumController.postOffice.getInbox(node).getMessages(type);
	}

}
