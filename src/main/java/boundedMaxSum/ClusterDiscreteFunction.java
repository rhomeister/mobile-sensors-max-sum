package boundedMaxSum;

import java.util.Set;

import maxSumController.Variable;
import maxSumController.discrete.VariableJointState;

public interface ClusterDiscreteFunction {

	public void addClusterVariableDependency(
			ClusteredVariable<? extends VariableJointState> cv);

	public void setOriginalDependencies(Set<Variable<?, ?>> variableDependencies);

	public double evaluateRestricted(VariableJointState jointState);

}
