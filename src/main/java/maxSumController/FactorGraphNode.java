package maxSumController;

import java.util.Set;

public interface FactorGraphNode extends Comparable<FactorGraphNode> {
	public abstract String getName();

	public abstract Set<? extends FactorGraphNode> getDependencies();
	
	public Comparable getOwningAgentIdentifier();
}
