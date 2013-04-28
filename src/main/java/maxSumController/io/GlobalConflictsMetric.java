package maxSumController.io;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.Function;
import maxSumController.GlobalMetric;
import maxSumController.InternalVariable;
import maxSumController.MaxSumController;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;

public class GlobalConflictsMetric implements GlobalMetric {

	@Override
	public double evaluate(MaxSumController<?, ?, ?> controller) {
		int conflicts = 0;
		Set<DiscreteInternalVariable<?>> variables = (Set<DiscreteInternalVariable<?>>) controller
				.getInternalVariables();
		Map<DiscreteVariable<?>, DiscreteVariableState> state = (Map<DiscreteVariable<?>, DiscreteVariableState>) controller
				.getCurrentState();

		for (DiscreteInternalVariable<?> variable : variables) {
			Color variableColor = (Color) state.get(variable);
			Set<DiscreteVariable<?>> neighbours = getNeighbouringVariables(variable);

			for (DiscreteVariable<?> anothervariable : neighbours) {
				if (!variable.getName().equals(anothervariable.getName())) {
					Color anotherVariableColor = (Color) state
							.get(anothervariable);
					if (variableColor.getName().equals(
							anotherVariableColor.getName())) {
						conflicts++;
					}
				}
			}

		}
		return conflicts / 2;
	}

	
	
	private Set<DiscreteVariable<?>> getNeighbouringVariables(
			InternalVariable<?, ?> variable) {
		Set<DiscreteVariable<?>> neighbours = new HashSet<DiscreteVariable<?>>();
		Set<Function> functions = variable.getFunctionDependencies();
		int vid = Integer.valueOf(variable.getName().substring(1));

		for (Function function : functions) {
			if (vid == Integer.valueOf(function.getName().substring(1))) {
				DiscreteInternalFunction ifunction = (DiscreteInternalFunction) function;
				neighbours
						.addAll((Collection<? extends DiscreteVariable<?>>) ifunction
								.getVariableDependencies());
			}
		}
		return neighbours;
	}
}
