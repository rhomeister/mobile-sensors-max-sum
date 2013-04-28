package boundedMaxSum;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.discrete.bb.BBDiscreteInternalFunction;
import maxSumController.discrete.bb.PartialJointVariableState;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

public class FixedInternalFunction extends DiscreteInternalFunction implements
		BBDiscreteInternalFunction {

	private FixedPayoffMatrix payoffs;

	public FixedInternalFunction(List<DiscreteInternalVariable<?>> variables,
			FixedPayoffMatrix matrix) {
		super("");

		for (DiscreteInternalVariable<?> variable : variables) {
			addVariableDependency(variable);
		}

		this.payoffs = matrix;
	}

	@Override
	public double evaluate(VariableJointState state) {
		return payoffs.get(state);
	}

	@Override
	public double getLowerBound(PartialJointVariableState state) {
		Validate.isTrue(state.isFullyDetermined());
		return evaluate(state.getJointState());
	}

	@Override
	public double getLowerBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		throw new NotImplementedException();
	}

	@Override
	public double getUpperBound(PartialJointVariableState state) {
		Validate.isTrue(state.isFullyDetermined());

		return evaluate(state.getJointState());
	}

	@Override
	public double getUpperBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		throw new NotImplementedException();
	}

	@Override
	public List<DiscreteVariable> getVariableExpansionOrder() {
		ArrayList<DiscreteVariable> variableList = new ArrayList<DiscreteVariable>();

		Set<Variable<?, ?>> variableDependencies = getVariableDependencies();

		for (Variable<?, ?> variable : variableDependencies) {
			variableList.add((DiscreteVariable) variable);
		}

		return variableList;
	}

	@Override
	public void debug(PartialJointVariableState state) {
		// TODO Auto-generated method stub

	}

}
