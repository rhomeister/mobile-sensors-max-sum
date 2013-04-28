package maxSumController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import maxSumController.communication.Inbox;
import maxSumController.communication.Message;
import maxSumController.communication.PostOffice;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractMaxSumController<V extends InternalVariable<?, ?>, S extends VariableState, F extends InternalFunction>
		implements MaxSumController<V, S, F> {
	private Comparable agentIdentifier;

	IterationPolicy iterationPolicy;

	protected static Log log = LogFactory
			.getLog(AbstractMaxSumController.class);

	protected PostOffice postOffice;

	protected Set<V> variables = new HashSet<V>();

	protected Set<F> functions = new HashSet<F>();

	private int simStep;

	protected Set<VariableNode<?, ?>> variableNodes = new HashSet<VariableNode<?, ?>>();

	protected Set<FunctionNode> functionNodes = new HashSet<FunctionNode>();

	protected Map<V, VariableNode<?, ?>> variableToVariableNodeMap = new HashMap<V, VariableNode<?, ?>>();

	protected Map<Function, FunctionNode> functionToFunctionNodeMap = new HashMap<Function, FunctionNode>();

	private StoppingCriterion stoppingCriterion = new FixedIterationStoppingCriterion(
			100);

	private List<AlgorithmPlugin> algorithmPlugins = new ArrayList<AlgorithmPlugin>();

	private boolean checked;

	protected MaxSumPlugin maxSumPlugin;

	public AbstractMaxSumController(Comparable agentIdentifier) {
		this.agentIdentifier = agentIdentifier;
		this.postOffice = new PostOffice(this);

		algorithmPlugins.add(new SpanningTreePlugin(this));
		maxSumPlugin = new MaxSumPlugin(this);

		algorithmPlugins.add(maxSumPlugin);
		algorithmPlugins.add(new ValuePropagationPlugin(this));
	}

	public final Comparable getAgentIdentifier() {
		return agentIdentifier;
	}

	public final void addInternalVariable(V variable) {
		Validate.notNull(variable.getDomain());
		// set owning identifier first, before adding it to the list of
		// variables. The hashcode depends on the owning identifier!
		variable.setOwningAgentIdentifier(agentIdentifier);
		VariableNode<?, ?> variableNode = createVariableNode(variable);
		variables.add(variable);
		variableNodes.add(variableNode);
		variableToVariableNodeMap.put(variable, variableNode);
	}

	protected abstract VariableNode<?, ?> createVariableNode(V variable);

	public final void addInternalFunction(F function) {
		function.setOwningAgentIdentifier(agentIdentifier);
		functions.add(function);
		FunctionNode functionNode = createFunctionNode(function);
		functionNodes.add(functionNode);
		functionToFunctionNodeMap.put(function, functionNode);
	}

	protected abstract FunctionNode createFunctionNode(InternalFunction function);

	@Override
	public Set<F> getInternalFunctions() {
		return functions;
	}

	@Override
	public Set<V> getInternalVariables() {
		return variables;
	}

	public V getInternalVariable(String name) {
		for (V v : variables) {
			if (v.getName().equals(name)) {
				return v;
			}
		}
		throw new IllegalArgumentException("Variable " + name
				+ " does not exist");
	}

	public F getInternalFunction(String name) {
		for (F f : functions) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		throw new IllegalArgumentException("Function " + name
				+ " does not exist");
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		for (V variable : variables) {
			buffer.append(variable.getName() + " "
					+ variable.getFunctionDependencies() + "\n");
		}

		for (InternalFunction function : functions) {
			buffer.append(function.getName() + " "
					+ function.getVariableDependencies() + "\n");
		}

		return buffer.toString();
	}

	public void handleIncomingMessages(Collection<Message> incomingMessages) {
		for (Message message : incomingMessages) {
			if (!getAgentIdentifier().equals(
					message.getReceivingAgentIdentifier())) {
				throw new IllegalArgumentException(
						"Received a message not addressed to this Controller. Expected "
								+ getAgentIdentifier() + ", but was "
								+ message.getReceivingAgentIdentifier()
								+ ". Message contents " + message);
			}

			Validate.isTrue(isOwner(message.getReceiver()),
					"Message is not addressed to this maxsumcontroller");
		}

		postOffice.process(incomingMessages);
	}

	public void setIterationPolicy(IterationPolicy iterationPolicy) {
		this.iterationPolicy = iterationPolicy;
		iterationPolicy.initialise(variableNodes, postOffice, functionNodes);
	}

	public Map<Function, FunctionNode> getFunctionToFunctionNodeMap() {
		return functionToFunctionNodeMap;
	}

	public Map<V, VariableNode<?, ?>> getVariableToVariableNodeMap() {
		return variableToVariableNodeMap;
	}

	public boolean stoppingCriterionIsMet() {
		return stoppingCriterion.isDone(maxSumPlugin.getIterationCount());
	}

	public void setStoppingCriterion(StoppingCriterion stoppingCriterion) {
		this.stoppingCriterion = stoppingCriterion;
	}

	public Collection<Message> calculateNewOutgoingMessages() {
		check();

		simStep++;

		for (AlgorithmPlugin plugin : algorithmPlugins) {
			plugin.run();
		}

		return postOffice.getMessagesForExternal();
	}

	private void check() {
		if (checked)
			return;

		if (functions.isEmpty()) {
			log.warn("Controller has no internal functions");
		}

		if (variables.isEmpty()) {
			log.warn("Controller has no internal variables");
		}

		for (InternalNodeRepresentation node : getInternalNodes()) {
			if (node.getRepresentedNode().getDependencies().isEmpty()) {
				log.warn(node + " has no dependencies");
			}
		}

		checked = true;

	}

	public Collection<InternalNodeRepresentation> getInternalNodes() {
		Collection<InternalNodeRepresentation> result = new HashSet<InternalNodeRepresentation>(
				variableNodes);
		result.addAll(functionNodes);
		return result;
	}

	public Set<V> calculateChangedVariable() {
		Set<V> result = new HashSet<V>();

		for (VariableNode<?, ?> variableNode : variableNodes) {
			Inbox inbox = postOffice.getInbox(variableNode);
			Collection<FunctionToVariableMessage> messagesForVariable = inbox
					.getMessages(FunctionToVariableMessage.class);

			if (variableNode.updateCurrentState(messagesForVariable))
				result.add((V) variableNode.getVariable());
		}
		return result;
	}

	public Map<V, S> computeCurrentState() {
		calculateChangedVariable();

		return getCurrentState();
	}

	public Map<V, S> getCurrentState() {
		Map<V, S> res = new HashMap<V, S>();
		for (VariableNode<?, ?> vnode : variableNodes) {
//			Validate.notNull(vnode.getCurrentState());
			res.put((V) vnode.getVariable(), (S) vnode.getCurrentState());
		}
		return res;
	}

	public void updateVariableStates() {
		for (VariableNode vnode : variableNodes) {
			vnode.setCurrentState(vnode.getCurrentValuePropagationState());
		}
	}

	/**
	 * Returns the values of the variables that were calculated by the value
	 * propagation algorithm, if available. If the algorithm has not been
	 * activated, or has not terminated, the value of (some of the) variables
	 * can be null
	 * 
	 * @return
	 */
	public Map<V, S> getCurrentValuePropagationState() {
		Map<V, S> res = new HashMap<V, S>();
		for (VariableNode<?, ?> vnode : variableNodes) {
			res.put((V) vnode.getVariable(), (S) vnode
					.getCurrentValuePropagationState());
		}
		return res;
	}

	public int getMessageCount() {
		return postOffice.getMessageCount();
	}

	public int getMessageSize() {
		return postOffice.getMessageSize();
	}

	public void startSpanningTreeAlgorithm() {
		for (VariableNode<?, ?> node : variableNodes) {
			Collection<SpanningTreeMessage> messages = node
					.startSpanningTreeAlgorithm();
			postOffice.process(messages);
		}
	}

	public void startValuePropagation() {
		for (VariableNode<?, ?> node : variableNodes) {
			Collection<FunctionToVariableMessage> lastReceivedMaxSumMessages = postOffice
					.getInbox(node)
					.getMessages(FunctionToVariableMessage.class);

			postOffice.process(node
					.startValuePropagation(lastReceivedMaxSumMessages));
		}
	}

	/**
	 * Returns true iff node is an internal variable or internal function to
	 * this controller
	 * 
	 * @param node
	 * @return
	 */
	public boolean isOwner(FactorGraphNode node) {
		return getInternalFunctions().contains(node)
				|| getInternalVariables().contains(node);
	}

	public InternalNodeRepresentation getInternalNode(FactorGraphNode node) {

		if (variableToVariableNodeMap.containsKey(node))
			return variableToVariableNodeMap.get(node);
		else if (functionToFunctionNodeMap.containsKey(node))
			return functionToFunctionNodeMap.get(node);
		else
			throw new IllegalArgumentException(
					"Node is not represented by this controller");
	}

	public PostOffice getPostOffice() {
		return postOffice;
	}

	public IterationPolicy getIterationPolicy() {
		return iterationPolicy;
	}

	public void addAlgorithmPlugin(AlgorithmPlugin plugin, int executionOrder) {
		algorithmPlugins.add(executionOrder, plugin);
	}

	public void clearAlgorithmPlugins() {
		algorithmPlugins.clear();
	}

	public void startMaxSumAlgorithm() {
		setMaxSumEnabled(true);
	}

	public void setMaxSumEnabled(boolean b) {
		maxSumPlugin.setEnabled(b);
	}

	public Set<FunctionNode> getFunctionNodes() {
		return functionNodes;
	}

	public Set<VariableNode<?, ?>> getVariableNodes() {
		return variableNodes;
	}

	public Set<Variable<?, ?>> getAllVariables() {
		Set<Variable<?, ?>> allVariables = new HashSet<Variable<?, ?>>();
		allVariables.addAll(variables);

		for (InternalFunction function : functions) {
			allVariables.addAll(function.getVariableDependencies());
		}

		return allVariables;
	}

	@Override
	public Collection<F> getFunctions() {
		return functions;
	}
}