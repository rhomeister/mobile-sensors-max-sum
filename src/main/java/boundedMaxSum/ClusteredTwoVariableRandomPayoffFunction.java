package boundedMaxSum;

import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;

public class ClusteredTwoVariableRandomPayoffFunction extends
		ClusterDiscreteFunctionImpl implements LinkBoundedFunctionInterface {

	public ClusteredTwoVariableRandomPayoffFunction(String name,
			DiscreteInternalFunction function) {
		super(name);
		tvrpf = (TwoVariablesRandomPayoffFunction) function;
	}

	TwoVariablesRandomPayoffFunction tvrpf;

	@Override
	public double getBound(Set<DiscreteInternalVariable> rejectedVariables) {
		return tvrpf.getBound(rejectedVariables);
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(String deletedVariableName) {
		return tvrpf.getNewFunction(deletedVariableName);
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(
			Set<DiscreteInternalVariable> rejectedVariables) {
		return tvrpf.getNewFunction(rejectedVariables);
	}

	@Override
	public double getWeight(String deletedVariableName) {
		return tvrpf.getWeight(deletedVariableName);
	}

	@Override
	public double getMaximumBound() {
		return tvrpf.getMaximumBound();
	}

	@Override
	public double getMinimumBound() {
		return tvrpf.getMinimumBound();
	}

	@Override
	public BoundedInternalFunction clone() {
		return tvrpf.clone();
	}

	@Override
	public double evaluate(VariableJointState state) {
		// TODO Auto-generated method stub
		return tvrpf.evaluate(state);
	}

}
