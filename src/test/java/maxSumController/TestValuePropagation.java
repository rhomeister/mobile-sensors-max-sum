package maxSumController;

import java.util.Collection;

import maxSumController.communication.ValuePropagationMessage;
import maxSumController.discrete.DiscreteVariableDomainImpl;

public class TestValuePropagation extends SimpleFactorGraphTestCase {

	private double oldScalerValue;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		oldScalerValue = DiscreteVariableDomainImpl.SCALER;

		DiscreteVariableDomainImpl.SCALER = 0.0;

		maxSumController.startSpanningTreeAlgorithm();

		for (int i = 0; i < 10; i++) {
			maxSumController.calculateNewOutgoingMessages();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		DiscreteVariableDomainImpl.SCALER = oldScalerValue;
	}

	public void testStartValuePropagation() throws Exception {
		maxSumController.startValuePropagation();

		FactorGraphNode root = maxSumController.getInternalNodes().iterator().next()
				.getRoot();

		InternalNodeRepresentation internalRootNode = maxSumController
				.getInternalNode(root);

		// neighbours of the root node should have received a message
		for (FactorGraphNode neighbour : internalRootNode.getActiveNeighbours()) {
			assertEquals(1, getMessagesForNode(neighbour).size());
		}

		// all the other nodes should not have received a message
		for (InternalNodeRepresentation node : maxSumController
				.getInternalNodes()) {
			if (!internalRootNode.getActiveNeighbours().contains(
					node.getRepresentedNode())) {
				assertEquals(0, getMessagesForNode(node.getRepresentedNode())
						.size());
			}
		}
	}

	public void testIterations() throws Exception {
		maxSumController.startValuePropagation();

		for (int i = 0; i < 10; i++) {
			maxSumController.calculateNewOutgoingMessages();
		}

		System.out.println(maxSumController.computeCurrentState());
		System.out.println(maxSumController.getCurrentValuePropagationState());

	}

	private Collection<ValuePropagationMessage> getMessagesForNode(FactorGraphNode receiver) {
		return getMessagesForNode(receiver, ValuePropagationMessage.class);
	}

}
