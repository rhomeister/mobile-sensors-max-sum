package maxSumController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import maxSumController.communication.Inbox;
import maxSumController.communication.PostOffice;

import org.apache.commons.lang.Validate;

/**
 * 
 * @author sandrof
 * 
 *         Recomputes outgoing messages whenever a new message is received,
 *         using old messages for the others.
 * 
 */

public class SingleMessageIterationPolicy implements IterationPolicy {

	private Set<? extends VariableNode<?, ?>> variables;

	private PostOffice postOffice;

	private Set<FunctionNode> functions;

	private int iterations;

	@Override
	public void initialise(Set<? extends VariableNode<?, ?>> variables,
			PostOffice postOffice, Set<FunctionNode> functions) {
		Validate.notNull(postOffice);
		this.variables = variables;
		this.postOffice = postOffice;
		this.functions = functions;
	}

	@Override
	public void enqueueNewOutgoingMessages() {
		/*
		 * for (VariableNode<?, ?> variableNode : variables) { Inbox inbox =
		 * postOffice.getInbox(variableNode);
		 * Collection<FunctionToVariableMessage> messagesForVariable = inbox
		 * .getMessages(FunctionToVariableMessage.class);
		 * Collection<VariableToFunctionMessage> updateOutgoingMessages =
		 * variableNode .updateOutgoingMessages(messagesForVariable);
		 * postOffice.process(updateOutgoingMessages);
		 * //System.out.println("V2F: " + updateOutgoingMessages); }
		 * 
		 * for (FunctionNode functionNode : functions) { Inbox inbox =
		 * postOffice.getInbox(functionNode);
		 * Collection<VariableToFunctionMessage> messagesForFunction = inbox
		 * .getMessages(VariableToFunctionMessage.class);
		 * Collection<FunctionToVariableMessage> updateOutgoingMessages =
		 * functionNode .updateOutgoingMessages(messagesForFunction);
		 * postOffice.process(updateOutgoingMessages);
		 * //System.out.println("F2V: " + updateOutgoingMessages); }
		 */

		ArrayList<VariableNode> varlist = new ArrayList(variables);
		ArrayList<FunctionNode> funlist = new ArrayList(functions);
		Collections.shuffle(varlist);
		Collections.shuffle(funlist);

		for (int i = 0; i < Math.max(varlist.size(), funlist.size()); i++) {

			if (i < varlist.size()) {
				VariableNode variableNode = varlist.get(i);

				Inbox inbox = postOffice.getInbox(variableNode);
				Collection<FunctionToVariableMessage> messagesForVariable = inbox
						.getMessages(FunctionToVariableMessage.class);
				Collection<VariableToFunctionMessage> updateOutgoingMessages = variableNode
						.updateOutgoingMessages(messagesForVariable);
				postOffice.process(updateOutgoingMessages);
				// System.out.println("V2F: " + updateOutgoingMessages);
			}

			if (i < funlist.size()) {
				FunctionNode functionNode = funlist.get(i);

				Inbox inbox = postOffice.getInbox(functionNode);
				Collection<VariableToFunctionMessage> messagesForFunction = inbox
						.getMessages(VariableToFunctionMessage.class);
				Collection<FunctionToVariableMessage> updateOutgoingMessages = functionNode
						.updateOutgoingMessages(messagesForFunction);

				for (FunctionToVariableMessage functionToVariableMessage : updateOutgoingMessages) {
					Validate.notNull(functionToVariableMessage.getContents());
				}

				postOffice.process(updateOutgoingMessages);
				// System.out.println("F2V: " + updateOutgoingMessages);
			}

		}

		iterations++;

	}

	public int getIterationCount() {
		return iterations;
	}

}
