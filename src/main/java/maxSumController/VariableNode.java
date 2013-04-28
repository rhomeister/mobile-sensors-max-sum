package maxSumController;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import maxSumController.communication.ValuePropagationMessage;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.multiball.MultiballVariableState;

import org.apache.commons.lang.Validate;

/**
 * 
 * @author sandrof
 * 
 *         Represents the computational unit associated with a variable
 * 
 */

public abstract class VariableNode<V extends InternalVariable<?, S>, S extends VariableState>
extends InternalNodeRepresentation {

	// the Variable that this function node represents
	protected V variable;

	// the variable for which this computational unit is responsible
	private int hashCode;

	private S currentState;

	// current state calculated with value propagation
	private S currentValuePropagationState;

	protected MarginalValues<S> marginalFunction;

	public VariableNode(V variable) {
		Validate.notNull(variable.getDomain());
		this.variable = variable;
		this.hashCode = variable.hashCode();
		currentState = null;
	}

	public V getVariable() {
		return variable;
	}

	@SuppressWarnings("unchecked")
	public boolean updateCurrentState(
			Collection<FunctionToVariableMessage> incomingMessages) {

		// System.out.println("Update State: " +variable.getName() + " " +
		// incomingMessages);

		Map<Function, FunctionToVariableMessage> functionToMessageMap = createFunctionToMessageMap(incomingMessages);

		VariableState oldState = currentState;

		// Make new marginal values from the recieved messages
		marginalFunction = (MarginalValues<S>) variable.getDomain()
		.createZeroMarginalFunction();

		// marginalValues = new HashMap<VariableState, Double>();
		for (Function function : variable.getFunctionDependencies()) {
			FunctionToVariableMessage receivedMessage = functionToMessageMap
			.get(function);

			if (receivedMessage != null) {
				marginalFunction = marginalFunction
				.add((MarginalValues<S>) receivedMessage
						.getMarginalFunction());
			}
		}

		// System.out.println("BEFORE "+marginalFunction);
		marginalFunction = marginalFunction.add(variable
				.getPreference(marginalFunction));
		// System.out.println("AFTER "+marginalFunction);

		// Calculate the maximum

		currentState = marginalFunction.argMax();

		/*		if(currentState instanceof MultiballVariableState){
			DiscreteMarginalValues<MultiballVariableState> mf = 
				(DiscreteMarginalValues<MultiballVariableState>) marginalFunction;
			System.out.println(variable + " ucs: " + mf);
		}*/

		/*
		 * if (logging){ log.println("####################################");
		 * log.println("Variable "+ sensorIndex + " state is now
		 * "+currentState+" (was "+ oldState+")");
		 * log.println("####################################"); }
		 */

		if (currentState != null) {

			if (currentState.equals(oldState)) {// TODO check this equality
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private Map<Function, FunctionToVariableMessage> createFunctionToMessageMap(
			Collection<FunctionToVariableMessage> incomingMessages) {
		Map<Function, FunctionToVariableMessage> functionToMessageMap = new HashMap<Function, FunctionToVariableMessage>();

		for (FunctionToVariableMessage message : incomingMessages) {
			functionToMessageMap.put(message.getSender(), message);
		}
		return functionToMessageMap;
	}

	@SuppressWarnings("unchecked")
	protected Collection<VariableToFunctionMessage> updateOutgoingMessages(
			Collection<FunctionToVariableMessage> incomingMessages) {
		Map<Function, FunctionToVariableMessage> functionToMessageMap = createFunctionToMessageMap(incomingMessages);

		Set<VariableToFunctionMessage> res = new HashSet<VariableToFunctionMessage>();

		for (Function fn : variable.getFunctionDependencies()) {
			// Make empty message

			MarginalValues<S> result = ((VariableDomain<S>) variable
					.getDomain()).createZeroMarginalFunction();

			// Iterate through all connected function nodes (excluding the
			// recipiant of the message)

			for (Function anotherFn : variable.getFunctionDependencies()) {

				if (!fn.equals(anotherFn)) {
					FunctionToVariableMessage receivedMessage = functionToMessageMap
					.get(anotherFn);

					if (receivedMessage != null) {
						result = result.add((MarginalValues<S>) receivedMessage
								.getMarginalFunction());
					} else {
						MarginalValues<S> zero = ((VariableDomain<S>) variable
								.getDomain()).createZeroMarginalFunction();
						result = result.add(zero);
					}
				}
			}

			result = result.add(variable.getPreference(result));

			VariableToFunctionMessage message = new VariableToFunctionMessage(
					variable, fn, result);
			message.normalise();

			res.add(message);
		}

		return res;

	}

	public String getVarName() {
		return variable.getName();
	}

	@Override
	public boolean equals(Object other) {
		return other.hashCode() == hashCode();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public S getCurrentState() {
		return currentState;
	}

	public void setCurrentState(S state) {
		this.currentState = state;
	}

	public void setCurrentValuePropagationState(S state) {
		this.currentValuePropagationState = state;
	}

	public MarginalValues<S> getMarginalFunction() {
		return marginalFunction;
	}

	@Override
	public FactorGraphNode getRepresentedNode() {
		return variable;
	}

	public Collection<ValuePropagationMessage> startValuePropagation(
			Collection<FunctionToVariableMessage> incomingMessages) {
		Collection<ValuePropagationMessage> result = new Vector<ValuePropagationMessage>();

		if (isRoot()) {
			updateCurrentState(incomingMessages);

			currentValuePropagationState = currentState;

			for (FactorGraphNode neighbour : getActiveNeighbours()) {
				result.add(new ValuePropagationMessage(getRepresentedNode(),
						neighbour, currentValuePropagationState));
			}
		}

		return result;
	}

	@Override
	public Collection<ValuePropagationMessage> handleValuePropagationMessage(
			ValuePropagationMessage message) {
		Collection<ValuePropagationMessage> result = new Vector<ValuePropagationMessage>();

		currentValuePropagationState = (S) message.getContents();

		// forward message to all active neighbours
		for (FactorGraphNode neighbour : getActiveNeighbours()) {
			result.add(new ValuePropagationMessage(getRepresentedNode(),
					neighbour, message.getContents()));
		}

		return result;
	}

	public S getCurrentValuePropagationState() {
		return currentValuePropagationState;
	}
}
