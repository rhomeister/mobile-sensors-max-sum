package maxSumController;

import java.util.Set;

import maxSumController.communication.PostOffice;

public interface IterationPolicy {

	public void enqueueNewOutgoingMessages();

	public void initialise(Set<? extends VariableNode<?, ?>> variables,
			PostOffice postOffice, Set<FunctionNode> functions);

	public int getIterationCount();

}
