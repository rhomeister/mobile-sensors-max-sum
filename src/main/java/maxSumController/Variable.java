package maxSumController;

public interface Variable<D extends VariableDomain<S>, S extends VariableState>
		extends FactorGraphNode {

	public void setDomain(D variableDomain);

	public D getDomain();

	public Comparable getOwningAgentIdentifier();

	public void setOwningAgentIdentifier(Comparable identifier);

}