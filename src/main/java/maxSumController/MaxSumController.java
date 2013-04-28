package maxSumController;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import maxSumController.communication.Message;

public interface MaxSumController<V extends Variable<?, ?>, S extends VariableState, F extends InternalFunction> {

	public abstract Comparable getAgentIdentifier();

	public abstract void addInternalVariable(V v);

	public abstract Collection<Message> calculateNewOutgoingMessages();

	public abstract void addInternalFunction(F function);

	public abstract Set<V> getInternalVariables();

	public abstract Set<F> getInternalFunctions();

	public abstract void handleIncomingMessages(
			Collection<Message> incomingMessages);

	public boolean stoppingCriterionIsMet();

	public V getInternalVariable(String name);

	public F getInternalFunction(String name);

	public Map<? extends Variable<?, ?>, S> computeCurrentState();

	public Map<? extends Variable<?, ?>, S> getCurrentState();

	public int getMessageSize();

	public int getMessageCount();
	
	public Collection<F> getFunctions();
}