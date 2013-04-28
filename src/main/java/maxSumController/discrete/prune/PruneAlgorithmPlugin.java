package maxSumController.discrete.prune;

import java.util.Collection;

import maxSumController.AlgorithmPlugin;
import maxSumController.DiscreteInternalFunction;
import maxSumController.FunctionNode;
import maxSumController.VariableNode;
import maxSumController.communication.Inbox;
import maxSumController.communication.PostOffice;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariableNode;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PruneAlgorithmPlugin implements AlgorithmPlugin {

	private Log log = LogFactory.getLog(PruneAlgorithmPlugin.class);

	private DiscreteMaxSumController<DiscreteInternalFunction> controller;
	private PostOffice postOffice;
	private boolean enabled = false;

	private boolean converged;

	private int convergedIterations;

	private int totalIterations = 10;

	private int iterations = 0;

	private boolean done;

	public PruneAlgorithmPlugin(
			DiscreteMaxSumController discreteMaxSumController) {
		controller = discreteMaxSumController;
		postOffice = controller.getPostOffice();
	}

	@Override
	public void run() {
		if (!enabled)
			return;

		// if (converged) {
		// convergedIterations++;
		//
		// if (convergedIterations > 5) {
		// if (!controller.isMaxSumEnabled()) {
		// log.info("Converged for " + convergedIterations
		// + " iterations. Enabling max-sum algorithm");
		// controller.setMaxSumEnabled(true);
		// }
		// } else {
		// // let function nodes still read and empty their mailboxes, but
		// // don't let them send anything
		// for (FunctionNode internalNode : controller.getFunctionNodes()) {
		// Inbox inbox = postOffice.getInbox(internalNode);
		//
		// Collection<PruneMessage> messagesForNode = inbox
		// .consumeMessages(PruneMessage.class);
		//
		// internalNode.handlePruneMessage(messagesForNode);
		// }
		// }
		// return;
		// }

		if (iterations < totalIterations) {
			runIteration();

			iterations++;
		}

		// converged = true;
		//
		// for (VariableNode<?, ?> variableNode : controller.getVariableNodes())
		// {
		//
		// Inbox inbox = postOffice.getInbox(variableNode);
		//
		// Collection<PruneMessage> messagesForNode = inbox
		// .consumeMessages(PruneMessage.class);
		//
		// postOffice
		// .process(variableNode.handlePruneMessage(messagesForNode));
		//
		// Validate
		// .isTrue(inbox.consumeMessages(PruneMessage.class).isEmpty());
		//
		// converged &= ((DiscreteVariableNode<?>) variableNode)
		// .isPruningConverged();
		// }

		// start the max sum algorithm
		if (iterations >= totalIterations) {
			log.info(controller.getAgentIdentifier()
					+ ": Pruning algorithm converged.");
			
			done = true;
			enabled = false;

			controller.setMaxSumEnabled(true);
		}
	}

	private void runIteration() {
		for (FunctionNode internalNode : controller.getFunctionNodes()) {
			Inbox inbox = postOffice.getInbox(internalNode);

			Collection<PruneMessage> messagesForNode = inbox
					.consumeMessages(PruneMessage.class);

			Validate
					.isTrue(inbox.consumeMessages(PruneMessage.class).isEmpty());

			postOffice
					.process(internalNode.handlePruneMessage(messagesForNode));
		}
		
		for (VariableNode<?, ?> variableNode : controller.getVariableNodes()) {

			Inbox inbox = postOffice.getInbox(variableNode);

			Collection<PruneMessage> messagesForNode = inbox
					.consumeMessages(PruneMessage.class);

			postOffice
					.process(variableNode.handlePruneMessage(messagesForNode));

			Validate
					.isTrue(inbox.consumeMessages(PruneMessage.class).isEmpty());

			converged &= ((DiscreteVariableNode<?>) variableNode)
					.isPruningConverged();
		}
		
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isDone() {
		return done;
	}
}
