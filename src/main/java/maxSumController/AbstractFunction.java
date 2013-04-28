package maxSumController;

public abstract class AbstractFunction implements Function {

	private Comparable owningAgentIdentifier;

	private String name;

	public AbstractFunction(String name) {
		setName(name);
	}

	@Override
	public String getName() {
		return name;
	}

	public final boolean equals(Object other) {

		if (other instanceof Function) {
			Function function = (Function) other;
			return function.getName().equals(getName())
					&& function.getOwningAgentIdentifier().equals(
							getOwningAgentIdentifier());
		}

		return false;
	}
	
	@Override
	public final int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Function " + name;
	}

	public Comparable getOwningAgentIdentifier() {
		return owningAgentIdentifier;
	}

	public void setOwningAgentIdentifier(Comparable owningAgentIdentifier) {
		this.owningAgentIdentifier = owningAgentIdentifier;
	}

	public int compareTo(FactorGraphNode o) {
		return getName().compareTo(o.getName());
	}

	public void setName(String name) {
		this.name = name;
	}
}
