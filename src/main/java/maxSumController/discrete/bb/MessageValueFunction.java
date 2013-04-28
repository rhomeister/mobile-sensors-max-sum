package maxSumController.discrete.bb;

import java.util.HashMap;
import java.util.Map;

import maxSumController.DiscreteVariableState;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.continuous.linear.Interval;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariable;

import org.apache.commons.lang.Validate;

/**
 * Computes upper and lower bounds for the values of the messages given that
 * only a subset of the variables has been set
 * 
 * @author rs06r
 * 
 */
public class MessageValueFunction {

	private Map<DiscreteVariable<?>, DiscreteMarginalValues> messages = new HashMap<DiscreteVariable<?>, DiscreteMarginalValues>();

	private DiscreteVariable<?> targetVariable;

	public MessageValueFunction(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages,
			DiscreteVariable<?> targetVariable) {
		for (Variable<?, ?> variable : sortedMessages.keySet()) {
			DiscreteMarginalValues values = (DiscreteMarginalValues) sortedMessages
					.get(variable);

			messages.put((DiscreteVariable) variable, values);
		}
		this.targetVariable = targetVariable;
	}

	/**
	 * Gives a upper and lower bound for the messages for a partial variable
	 * state
	 * 
	 * @param state
	 * @param messages
	 * @return
	 */
	public Interval getValueInterval(PartialJointVariableState state) {
		double min = 0.0;
		double max = 0.0;

		for (DiscreteVariable variable : messages.keySet()) {
			if (variable.equals(targetVariable))
				continue;

			DiscreteVariableState variableState = state.getState(variable);

			DiscreteMarginalValues<?> values = (DiscreteMarginalValues<?>) messages
					.get(variable);

			if (variableState == null) {
				// Variable has not been set, we have to use the bounds
				min += values.min();
				max += values.max();
			} else {
				// Variable has been set, we know the value for this state
				Double value = values.getValue(variableState);

				if (value == null) {
					System.out.println(values);
					System.out.println(variableState);
					System.out.println(variable.getDomain());

					throw new IllegalArgumentException("JointState contains a "
							+ "variable state that no longer exists. "
							+ variableState + " " + variable.getDomain());
				}

				min += value;
				max += value;
			}
		}

		Validate.isTrue(!Double.isNaN(min));
		Validate.isTrue(!Double.isInfinite(min));
		Validate.isTrue(!Double.isNaN(max));
		Validate.isTrue(!Double.isInfinite(max));

		return new Interval(min, max);
	}

	@Override
	public String toString() {
		return super.toString() + " " + messages;
	}
}
