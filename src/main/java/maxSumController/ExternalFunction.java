package maxSumController;

import java.util.Set;

/**
 * This is a proxy for a function that is owned by another agent
 * 
 * @author rs06r
 * 
 */
public class ExternalFunction extends AbstractFunction {

	public ExternalFunction(String name, Comparable owningAgentIdentifier) {
		super(name);
		setOwningAgentIdentifier(owningAgentIdentifier);
	}

	public Set<? extends FactorGraphNode> getDependencies() {
		throw new IllegalArgumentException(
				"The dependencies of ExternalFunctions are unknown");
	}
}
