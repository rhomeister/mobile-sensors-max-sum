package maxSumController.multiball;

import java.util.Set;

import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.DiscreteVariableDomainImpl;
import maxSumController.discrete.VariableJointState;

public class MultiMaxInternalFunction extends MultiballInternalFunction {

	private DiscreteInternalVariable<MultiMaxContinuousFunction> indexingVariable;
	private Set<DiscreteInternalVariable<?>> continuousVariables;
	
	public MultiMaxInternalFunction(String name, 
			Set<MultiMaxContinuousFunction> functionSet,
			Set<DiscreteInternalVariable<?>> continuousVariables) {
		super(name);
		this.continuousVariables = continuousVariables;
		String indexingVariableName = name + "_indexingVariable";
		DiscreteVariableDomain<MultiMaxContinuousFunction> indexDomain = new DiscreteVariableDomainImpl<MultiMaxContinuousFunction>();

		for(MultiMaxContinuousFunction fn: functionSet)
		{
			indexDomain.add(fn);
		}
		
		this.indexingVariable = new DiscreteInternalVariable<MultiMaxContinuousFunction>(indexingVariableName, indexDomain);
	}
	
	public DiscreteInternalVariable<MultiMaxContinuousFunction> 
		getIndexingVariable() {
		return indexingVariable;
	}

	@Override
	public double evaluate(VariableJointState state) {
		MultiMaxContinuousFunction currentFunction = state.get(indexingVariable);
		return currentFunction.evaluate(state);
	}

	@Override
	public double evaluateDerivative(VariableJointState state,
			DiscreteInternalVariable<MultiballVariableState> variableToDeriveBy) {
		MultiMaxContinuousFunction currentFunction = state.get(indexingVariable);
		return currentFunction.evaluateDerivative(state, variableToDeriveBy);
	}

	@Override
	public boolean isContinuous(DiscreteInternalVariable<?> variableToTest) {
		return continuousVariables.contains(variableToTest);
	}
	

}
