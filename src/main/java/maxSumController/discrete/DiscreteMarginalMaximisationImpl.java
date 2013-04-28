package maxSumController.discrete;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisationFactory;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.VariableState;
import maxSumController.discrete.bb.AbstractDiscreteMarginalMaximisation;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

public class DiscreteMarginalMaximisationImpl extends
		AbstractDiscreteMarginalMaximisation implements
		MarginalMaximisationFactory {

	private DiscreteInternalFunction function;

	private Set<DiscreteVariable<?>> variables;

	// private boolean changed;

	public MarginalValues<?> calculateMarginalMaxFunction(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages,
			Variable<?, ?> variable) {
		Map<DiscreteVariableState, Double> values = new HashMap<DiscreteVariableState, Double>();

		JointStateIterator stateMap = new JointStateIterator(variables);

		Validate.isTrue(variables.contains(variable));

		// Loop through all the joint variable states
		while (stateMap.hasNext()) {
			VariableJointState jointState = stateMap.next();

			double utility = function.evaluate(jointState);

			for (DiscreteVariable<?> anotherVariable : variables) {
				if (!variable.equals(anotherVariable)) {
					DiscreteMarginalValues<?> functionFromVariable = (DiscreteMarginalValues<?>) sortedMessages
							.get(anotherVariable);

					if (functionFromVariable != null) {
						DiscreteVariableState state = jointState
								.get(anotherVariable);
						utility += functionFromVariable.getValue(state);
					}
				}
			}

			DiscreteVariableState variableState = jointState
					.get((DiscreteVariable<?>) variable);
			Double previousValue = values.get(variableState);
			values.put(variableState, Math
					.max((previousValue == null) ? -Double.MAX_VALUE
							: previousValue, utility));
		}

		return new DiscreteMarginalValues<DiscreteVariableState>(values);
	}

	public void setFunction(InternalFunction function) {
		// if (this.function == null || !this.function.equals(function))
		// changed = true;

		this.function = (DiscreteInternalFunction) function;
	}

	public void setVariables(Set<? extends Variable<?, ?>> variables) {
		// if (this.variables == null || !this.variables.equals(variables))
		// changed = true;

		this.variables = (Set<DiscreteVariable<?>>) variables;
	}

	public DiscreteMarginalMaximisationImpl create() {
		return new DiscreteMarginalMaximisationImpl();
	}

	@Override
	public VariableState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState, Variable<?, ?> targetVariable) {
		throw new NotImplementedException();
	}

	@Override
	protected DiscreteInternalFunction getFunction() {
		return function;
	}
}
