package maxSumController.continuous;

import maxSumController.AbstractExternalVariable;

public class ContinuousExternalVariable
		extends
		AbstractExternalVariable<ContinuousVariableDomain, ContinuousVariableState>
		implements ContinuousVariable {

	public ContinuousExternalVariable(String name,
			ContinuousVariableDomain domain, Comparable owningAgentIdentifier) {
		super(name, domain, owningAgentIdentifier);
	}
}
