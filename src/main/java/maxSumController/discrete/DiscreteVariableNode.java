package maxSumController.discrete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.Function;
import maxSumController.FactorGraphNode;
import maxSumController.VariableNode;
import maxSumController.discrete.prune.FunctionToVariablePruneMessageContents;
import maxSumController.discrete.prune.PruneMessage;
import maxSumController.discrete.prune.VariableToFunctionPruneMessageContents;

import org.apache.commons.lang.mutable.MutableDouble;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscreteVariableNode<S extends DiscreteVariableState> extends
		VariableNode<DiscreteInternalVariable<S>, S> {

	private static final double PRUNE_THRESHOLD_MULTIPLIER = 1.0001;

	private static Log log = LogFactory.getLog(DiscreteVariableNode.class);

	private Map<FactorGraphNode, FunctionToVariablePruneMessageContents> receivedPruneMessages = new HashMap<FactorGraphNode, FunctionToVariablePruneMessageContents>();
	private Map<FactorGraphNode, FunctionToVariablePruneMessageContents> previouslyReceivedPruneMessages = new HashMap<FactorGraphNode, FunctionToVariablePruneMessageContents>();
	private boolean converged;

	public DiscreteVariableNode(DiscreteInternalVariable<S> variable) {
		super(variable);
	}

	@Override
	public Collection<PruneMessage> handlePruneMessage(
			Collection<PruneMessage> messagesForNode) {
		// TODO print statement
		// System.err.println(variable + " received messages ");
		// for (PruneMessage pruneMessage : messagesForNode) {
		// System.err.println(pruneMessage);
		// }
		// System.err.println();

		// if (variable.getDependencies().isEmpty() || converged) {
		// converged = true;
		// System.out.println(variable + " is converged. Not sending messages");
		// return new ArrayList<PruneMessage>();
		// }

		for (PruneMessage message : messagesForNode) {
			FunctionToVariablePruneMessageContents contents = (FunctionToVariablePruneMessageContents) message
					.getContents();

			receivedPruneMessages.put(message.getSender(), contents);
		}

		Collection<PruneMessage> result = new ArrayList<PruneMessage>();

		// if a message has been received from all variables, send messages
		if (receivedPruneMessages.size() == variable.getFunctionDependencies()
				.size()) {

			if (!receivedPruneMessages.keySet().containsAll(
					variable.getFunctionDependencies())) {
				System.out.println(receivedPruneMessages.keySet());
				System.out.println(variable.getFunctionDependencies());

				throw new IllegalArgumentException();
			}

			// compute new domain
			variable.setDomain(new DiscreteVariableDomainImpl<S>(
					computeNewDomain(receivedPruneMessages.values())));

			for (Function function : variable.getFunctionDependencies()) {
				result.add(new PruneMessage(variable, function,
						new VariableToFunctionPruneMessageContents(variable
								.getDomain().getStates())));
			}

			// clear the received messages so that the algorithm is only
			// activated again once new messages have been received from all
			// functions
			//checkConvergence();

			previouslyReceivedPruneMessages.putAll(receivedPruneMessages);
			receivedPruneMessages.clear();
		}

		// if(converged)
		// result.clear();

		// System.err.println(variable + " sending messages ");
		// for (PruneMessage pruneMessage : result) {
		// System.err.println(pruneMessage);
		// }
		// System.err.println();

		return result;
	}

	private void checkConvergence() {
		// converged if values received from functions this time are equal to
		// last time
		if (previouslyReceivedPruneMessages.isEmpty())
			return;

		for (FactorGraphNode sender : receivedPruneMessages.keySet()) {
			FunctionToVariablePruneMessageContents contents = receivedPruneMessages
					.get(sender);
			FunctionToVariablePruneMessageContents previousContents = previouslyReceivedPruneMessages
					.get(sender);

			for (S state : variable.getDomain().getStates()) {
				// bounds can only be refined
				// if (contents.getLowerBound(state) < previousContents
				// .getLowerBound(state)) {
				// log.error("Error in agent "
				// + variable.getOwningAgentIdentifier()
				// + ". Message received from " + sender);
				// log.error("Lower bound can only be increased. "
				// + "Previous lower bound: "
				// + previousContents.getLowerBound(state)
				// + ". Current: " + contents.getLowerBound(state));
				// throw new IllegalArgumentException();
				// }
				// Validate
				// .isTrue(
				// contents.getUpperBound(state) <= previousContents
				// .getUpperBound(state),
				// "Upper bound can only be decreased. Previous upper bound: "
				// + previousContents.getUpperBound(state)
				// + ". Current: "
				// + contents.getUpperBound(state));

				if (!boundsEqual(state, contents, previousContents)) {
					converged = false;
					return;
				}
			}
		}

		converged = true;
		log.debug(variable + " has converged");
	}

	public boolean isPruningConverged() {
		return converged;
	}

	private boolean boundsEqual(S state,
			FunctionToVariablePruneMessageContents contents,
			FunctionToVariablePruneMessageContents previousContents) {
		return contents.getLowerBound(state) == previousContents
				.getLowerBound(state)
				&& contents.getUpperBound(state) == previousContents
						.getUpperBound(state);
	}

	/**
	 * Removes all states for which upper-bound < max(lower-bound)
	 * 
	 * @param values
	 * @return
	 */
	protected Set<S> computeNewDomain(
			Collection<FunctionToVariablePruneMessageContents> values) {

		Map<S, MutableDouble> lowerBounds = new HashMap<S, MutableDouble>();
		Map<S, MutableDouble> upperBounds = new HashMap<S, MutableDouble>();

		for (S state : variable.getDomain().getStates()) {
			lowerBounds.put(state, new MutableDouble());
			upperBounds.put(state, new MutableDouble());
			for (FunctionToVariablePruneMessageContents content : values) {
				lowerBounds.get(state).add(content.getLowerBound(state));
				upperBounds.get(state).add(content.getUpperBound(state));
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Bounds for " + variable);

			for (S state : lowerBounds.keySet()) {
				log.debug(state + " " + lowerBounds.get(state) + " "
						+ upperBounds.get(state));
			}
		}

		boolean changed = true;
		while (lowerBounds.size() > 1 && changed) {
			changed = false;
			double maxLowerBound = ((MutableDouble) Collections.max(lowerBounds.values()))
					.doubleValue();

			Set<S> scheduledForRemoval = new HashSet<S>();

			for (S state : upperBounds.keySet()) {
				if (upperBounds.get(state).doubleValue() <= maxLowerBound * PRUNE_THRESHOLD_MULTIPLIER) {
					if (maxLowerBound != lowerBounds.get(state).doubleValue())
						scheduledForRemoval.add(state);
				}
			}

			for (S s : scheduledForRemoval) {
				changed = true;
				// make sure that at least one state is not pruned
				if (lowerBounds.size() == 1)
					break;

				log.info("Pruning state " + s + " from " + variable);

				lowerBounds.remove(s);
				upperBounds.remove(s);
			}
		}

		return lowerBounds.keySet();
	}
}
