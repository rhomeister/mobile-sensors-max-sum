package maxSumController.discrete;

import java.util.HashSet;
import java.util.Set;

import maxSumController.AbstractInternalFunction;
import maxSumController.AbstractVariable;
import maxSumController.Function;
import maxSumController.InternalVariable;
import maxSumController.FactorGraphNode;
import maxSumController.VariableDomain;
import maxSumController.VariableState;

public abstract class AbstractInternalVariable<D extends VariableDomain<S>, S extends VariableState>
		extends AbstractVariable<D, S> implements InternalVariable<D, S> {

	private Set<Function> functions = new HashSet<Function>();

	public AbstractInternalVariable(String name, D domain) {
		super(name, domain);
	}

	public void addFunctionDependency(Function function) {
		functions.add(function);

		if (function instanceof AbstractInternalFunction) {
			AbstractInternalFunction internalFunction = (AbstractInternalFunction) function;
			if (!internalFunction.getVariableDependencies().contains(this))
				internalFunction.addVariableDependency(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see maxSumController.InternalVariable#removeFunctionDependency(java.lang.String)
	 */
	public void removeFunctionDependency(String name) {
		Function toremove = null;
		for (Function f : functions) {
			if (f.getName().equals(name)) {
				toremove = f;
				break;
			}
		}
		if (toremove != null) {
			functions.remove(toremove);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see maxSumController.InternalVariable#getFunctionDependencies()
	 */
	public Set<Function> getFunctionDependencies() {
		return functions;
	}
	
	public Set<? extends FactorGraphNode> getDependencies() {
		return getFunctionDependencies();
	}

}