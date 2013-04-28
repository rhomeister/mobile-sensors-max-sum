package maxSumController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import maxSumController.communication.ValuePropagationMessage;
import maxSumController.continuous.linear.Interval;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomainImpl;
import maxSumController.discrete.bb.BBDiscreteInternalFunction;
import maxSumController.discrete.prune.FunctionToVariablePruneMessageContents;
import maxSumController.discrete.prune.PruneMessage;
import maxSumController.discrete.prune.PruneMessageContents;
import maxSumController.discrete.prune.VariableToFunctionPruneMessageContents;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author sandrof
 * 
 *         Represents the computational unit associated with a function
 * 
 */

public class FunctionNode extends InternalNodeRepresentation {

	// the function that this function node represents
	protected InternalFunction function;

	private int hashcode;

	protected MarginalMaximisation maximiser;

	private boolean firstCall = true;

	private Map<FactorGraphNode, VariableToFunctionPruneMessageContents> receivedPruneMessages = new HashMap<FactorGraphNode, VariableToFunctionPruneMessageContents>();

	private static Log log = LogFactory.getLog(FunctionNode.class);

	// private Map<Node, VariableToFunctionPruneMessageContents>
	// receivedPruneMessages;

	public FunctionNode(InternalFunction function,
			MarginalMaximisation maximiser) {
		this.function = function;
		this.maximiser = maximiser;
		hashcode = function.getName().hashCode();
	}

	public Set<FunctionToVariableMessage> updateOutgoingMessages(
			Collection<VariableToFunctionMessage> incomingMessages) {
		Map<Variable<?, ?>, MarginalValues<?>> sortedMessages = sortMessages(incomingMessages);

		Set<FunctionToVariableMessage> res = new HashSet<FunctionToVariableMessage>();
		Set<Variable<?, ?>> variables = function.getVariableDependencies();

		maximiser.setFunction(function);
		maximiser.setVariables(variables);

		for (Variable<?, ?> variable : variables) {
			MarginalValues<?> values = maximiser.calculateMarginalMaxFunction(
					sortedMessages, variable);

			if (values == null) {
				log.error("Maximiser " + maximiser.getClass()
						+ " returned null values for function " + function
						+ " and variable " + variable);
				log
						.error("Function contains variable as dependency? "
								+ function.getVariableDependencies().contains(
										variable));

				log.error("Function's variable dependencies "
						+ function.getVariableDependencies());

				throw new IllegalArgumentException();
			}

			res.add(new FunctionToVariableMessage(function, variable, values));
		}

		return res;
	}

	protected Map<Variable<?, ?>, MarginalValues<?>> sortMessages(
			Collection<VariableToFunctionMessage> incomingMessages) {
		Map<Variable<?, ?>, MarginalValues<?>> variableToMessageMap = new HashMap<Variable<?, ?>, MarginalValues<?>>();

		for (VariableToFunctionMessage message : incomingMessages) {
			Variable<?, ?> sender = message.getSender();

			// discard messages that are unexpected (i.e. do not come from a
			// current variable dependency
			if (function.getVariableDependencies().contains(sender)) {
				variableToMessageMap.put(sender, message.getMarginalFunction());
			}
		}

		return variableToMessageMap;
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	public Function getFunction() {
		return function;
	}

	@Override
	public FactorGraphNode getRepresentedNode() {
		return getFunction();
	}

	@Override
	public Collection<ValuePropagationMessage> handleValuePropagationMessage(
			ValuePropagationMessage message) {

		Collection<ValuePropagationMessage> result = new Vector<ValuePropagationMessage>();

		VariableState variableState = message.getContents();

		for (FactorGraphNode neighbour : getActiveNeighbours()) {
			VariableState bestState = maximiser
					.getBestState((Variable) getParent(), variableState,
							(Variable) neighbour);

			result.add(new ValuePropagationMessage(getRepresentedNode(),
					neighbour, bestState));
		}

		return result;
	}

	@Override
	public Collection<PruneMessage> handlePruneMessage(
			Collection<PruneMessage> messagesForNode) {
		if (!(function instanceof BBDiscreteInternalFunction)) {
			throw new IllegalArgumentException(
					"Function should be a BBDiscreteInternalFunction"
							+ " in order to run distributed pruning. Instead, got "
							+ function.getClass());
		}

		// System.err.println(function + " received messages");
		// for (PruneMessage pruneMessage : messagesForNode) {
		// System.err.println(pruneMessage);
		// }
		// System.err.println();

		BBDiscreteInternalFunction bbFunction = (BBDiscreteInternalFunction) function;

		updateVariableDomains(messagesForNode);

		// if (firstCall) {
		// firstCall = false;
		// if (log.isDebugEnabled())
		// bbFunction.debug();
		// }

		Collection<PruneMessage> result = new ArrayList<PruneMessage>();

		for (PruneMessage message : messagesForNode) {
			VariableToFunctionPruneMessageContents contents = (VariableToFunctionPruneMessageContents) message
					.getContents();

			receivedPruneMessages.put(message.getSender(), contents);
		}

		// if a message has been received from all variables, send messages
		if (receivedPruneMessages.size() == function.getVariableDependencies()
				.size()) {
			if (!receivedPruneMessages.keySet().containsAll(
					function.getVariableDependencies())) {
				System.out.println(receivedPruneMessages.keySet());
				System.out.println(function.getVariableDependencies());
				throw new IllegalArgumentException();
			}

			receivedPruneMessages.clear();

			result = createPruneMessages();
		}

		// TODO print statement
		// System.err.println(function + " sending messages ");
		// for (PruneMessage pruneMessage : result) {
		// System.err.println(pruneMessage);
		// }
		// System.err.println();

		return result;
	}

	private Collection<PruneMessage> createPruneMessages() {
		BBDiscreteInternalFunction bbFunction = (BBDiscreteInternalFunction) function;
		Collection<PruneMessage> result = new ArrayList<PruneMessage>();

		for (Variable<?, ?> variable : function.getVariableDependencies()) {
			Map<DiscreteVariableState, Interval> bounds = new HashMap<DiscreteVariableState, Interval>();

			DiscreteVariable<?> discreteVariable = (DiscreteVariable<?>) variable;

			for (DiscreteVariableState state : discreteVariable.getDomain()
					.getStates()) {
				double lowerBound = bbFunction.getLowerBound(discreteVariable,
						state);
				double upperBound = bbFunction.getUpperBound(discreteVariable,
						state);

				bounds.put(state, new Interval(lowerBound, upperBound));
			}

			PruneMessageContents contents = new FunctionToVariablePruneMessageContents(
					bounds);

			result.add(new PruneMessage(function, variable, contents));
		}

		return result;
	}

	private void updateVariableDomains(Collection<PruneMessage> messagesForNode) {
		for (PruneMessage message : messagesForNode) {

			VariableToFunctionPruneMessageContents contents = (VariableToFunctionPruneMessageContents) message
					.getContents();
			// receivedPruneMessages.put(message.getSender(), contents);

			// update the domains of the variables
			// look up the local representation of the variable
			DiscreteVariable<?> discreteVariable = (DiscreteVariable<?>) function
					.getVariableDependency((Variable<?, ?>) message.getSender());

			Validate.isTrue(!contents.getStates().isEmpty());

			if (!discreteVariable.getDomain().getStates().containsAll(
					contents.getStates())) {
				System.out.println(message);
				System.out.println(discreteVariable);

				log
						.warn("Older message received: new variabledomain should be subset "
								+ "of old variable domain. Old domain: "
								+ discreteVariable.getDomain()
								+ ". New domain: " + contents.getStates());

				if (!contents.getStates().containsAll(
						discreteVariable.getDomain().getStates())) {
					throw new IllegalArgumentException();
				}
			} else {
				if (discreteVariable.getDomain().size() > contents.getStates()
						.size()) {
					log
							.info("Narrowing the domain of " + discreteVariable
									+ " from " + discreteVariable.getDomain()
									+ " to " + contents.getStates()
									+ " in function " + function);
					discreteVariable.setDomain(new DiscreteVariableDomainImpl(
							contents.getStates()));
				}
			}
		}
	}

	public Collection<PruneMessage> startPruningAlgorithm() {
		return createPruneMessages();
	}

}
