package boundedMaxSum;

import java.util.HashSet;
import java.util.Set;

import maxSumController.Variable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;

public class ClusteredVariable<S extends VariableJointState> extends
		DiscreteInternalVariable<S> {

	Set<DiscreteInternalVariable<?>> variables = new HashSet<DiscreteInternalVariable<?>>();

	public ClusteredVariable(String name, JointDiscreteVariableDomain<S> domain) {
		super(name, domain);
		// TODO Auto-generated constructor stub
	}

	public ClusteredVariable(String name,
			Set<DiscreteInternalVariable<?>> variables) {
		super(name, null);
		this.variables = variables;
	}

	public void addVariable(DiscreteInternalVariable<?> v) {
		variables.add(v);
	}

	public Set<? extends Variable> getVariables() {
		return variables;
	}

}
