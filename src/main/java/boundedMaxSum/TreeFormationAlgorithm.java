package boundedMaxSum;

import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.discrete.DiscreteInternalVariable;

public interface TreeFormationAlgorithm {
	public boolean hasRejectBranch(DiscreteInternalFunction function);

	public Set<DiscreteInternalVariable> getRejectedVariables(
			DiscreteInternalFunction function);

	public double getWeight(DiscreteInternalFunction function,
			DiscreteInternalVariable var);

	public Set<DiscreteInternalVariable> getKeptVariables(
			DiscreteInternalFunction function);

	public void execute() throws Exception;
}
