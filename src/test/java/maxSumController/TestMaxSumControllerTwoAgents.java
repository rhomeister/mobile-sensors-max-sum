package maxSumController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.communication.Message;
import maxSumController.discrete.DiscreteExternalVariable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ConflictFunction;

import org.apache.commons.collections15.CollectionUtils;

/**
 * 
 * @author sandrof
 * 
 * This example shows how to create two agents for a 3 colour graph colouring
 * domain with Each agent has a variable and a function.
 * 
 */

public class TestMaxSumControllerTwoAgents extends TestCase {

	/**
	 * The Max-sum controller for agent A
	 */
	private DiscreteMaxSumController<DiscreteInternalFunction> maxSumControllerA;

	/**
	 * The Max-sum controller for agent B
	 */
	private DiscreteMaxSumController<DiscreteInternalFunction> maxSumControllerB;

	private String agentAID = "agentA";

	private String agentBID = "agentB";

	public void setUp() throws Exception {
		setUpAgentA();
		setUpAgentB();
	}

	/**
	 * set up for agent A
	 */
	private void setUpAgentA() {

		/**
		 * The internal variable for the agent A; variable that the agent A can
		 * control
		 */
		DiscreteInternalVariable<Color> va = new DiscreteInternalVariable<Color>(
				"va", new ThreeColorsDomain());

		/**
		 * The external variable for the agent A; variable that the agent A can
		 * not control (represents the state of the other agent)
		 */
		DiscreteExternalVariable<Color> vb = new DiscreteExternalVariable<Color>(
				"vb", new ThreeColorsDomain(), agentBID);

		/**
		 * The function that represents the utility for agent A
		 */
		DiscreteInternalFunction fa = new PreferenceInternalFunction(
				new ConflictFunction("fa"));

		/**
		 * The external function that represents the utility of the other agent
		 */
		ExternalFunction fb = new ExternalFunction("fb", agentBID);

		/**
		 * Add variable dependencies for the internal function
		 */
		fa.addVariableDependency(va);
		fa.addVariableDependency(vb);

		/**
		 * Add variable dependency for the external function
		 */
		va.addFunctionDependency(fb);

		/**
		 * Creation of the max-sum control for agent A
		 */
		maxSumControllerA = new DiscreteMaxSumController(agentAID);

		/**
		 * Adding the internal variable to the max-sum controller
		 */
		maxSumControllerA.addInternalVariable(va);

		/**
		 * Adding the internal function to the max-sum controller
		 */
		maxSumControllerA.addInternalFunction(fa);
	}

	/**
	 * set up for agent B
	 */
	private void setUpAgentB() {

		/**
		 * The internal variable for the agent B; variable that the agent B can
		 * control
		 */
		DiscreteInternalVariable<Color> vb = new DiscreteInternalVariable<Color>(
				"vb", new ThreeColorsDomain());

		/**
		 * The external variable for the agent B; variable that the agent B can
		 * not control (represents the state of the other agent)
		 */
		DiscreteExternalVariable<Color> va = new DiscreteExternalVariable<Color>(
				"va", new ThreeColorsDomain(), agentAID);

		/**
		 * The function that represents the utility for agent A
		 */
		DiscreteInternalFunction fb = new PreferenceInternalFunction(
				new ConflictFunction("fb"));

		/**
		 * The external function that represents the utility of the other agent
		 */
		ExternalFunction fa = new ExternalFunction("fa", agentAID);

		/**
		 * Add variable dependencies for the internal function
		 */
		fb.addVariableDependency(va);
		fb.addVariableDependency(vb);

		/**
		 * Add variable dependency for the external function
		 */
		vb.addFunctionDependency(fa);

		/**
		 * Creation of the max-sum control for agent B
		 */
		maxSumControllerB = new DiscreteMaxSumController(agentBID);

		/**
		 * Adding the internal variable to the max-sum controller
		 */
		maxSumControllerB.addInternalVariable(vb);

		/**
		 * Adding the internal function to the max-sum controller
		 */
		maxSumControllerB.addInternalFunction(fb);
	}

	/**
	 * Executing the max-sum for the two agents
	 * 
	 * @throws Exception
	 */
	public void testScenarioTwoAgents() throws Exception {

		/**
		 * Set the stopping criterion for the max-sum
		 */
		maxSumControllerA
				.setStoppingCriterion(new FixedIterationStoppingCriterion(100));
		int i = 0;

		/**
		 * In this case the execution is sequential thus the agent will execute
		 * exactly the same iterations. We choose agent A to check the stopping
		 * criterion,
		 * 
		 */
		while (!maxSumControllerA.stoppingCriterionIsMet()) {
			i++;

			/**
			 * obtaining messages for agent B
			 */
			Collection<Message> messagesToB = maxSumControllerA
					.calculateNewOutgoingMessages();

			// check whether the messages are actually for B
			for (Message message : messagesToB) {
				assertEquals(maxSumControllerB.getAgentIdentifier(), message
						.getReceivingAgentIdentifier());
			}

			/**
			 * obtaining messages for agent A
			 */
			Collection<Message> messagesToA = maxSumControllerB
					.calculateNewOutgoingMessages();

			// check whether the messages are actually for A
			for (Message message : messagesToA) {
				assertEquals(maxSumControllerA.getAgentIdentifier(), message
						.getReceivingAgentIdentifier());
			}

			/**
			 * handle the incoming messages for A
			 */
			maxSumControllerA.handleIncomingMessages(messagesToA);

			/**
			 * handle the incoming messages for B
			 */
			maxSumControllerB.handleIncomingMessages(messagesToB);
		}

		/**
		 * Compute state for agent A and agent B
		 */
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentStateA = maxSumControllerA
				.computeCurrentState();
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentStateB = maxSumControllerB
				.computeCurrentState();

		Map<DiscreteVariable<?>, DiscreteVariableState> state = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		state.putAll(maxSumControllerA.computeCurrentState());
		state.putAll(maxSumControllerB.computeCurrentState());

		// check whether the graph is colored
		for (DiscreteVariableState vs : (new ThreeColorsDomain()).getStates()) {
			assertTrue(1 >= CollectionUtils.cardinality(vs, state.values()));
		}
	}

	/**
	 * Changing the structure of the problem
	 * 
	 * @throws Exception
	 */
	public void testChangingStructure() throws Exception {

		/* FIRST ITERATION */

		/**
		 * execute the first iterations after the agent set up
		 */

		Collection<Message> messagesToB = maxSumControllerA
				.calculateNewOutgoingMessages();
		Collection<Message> messagesToA = maxSumControllerB
				.calculateNewOutgoingMessages();

		maxSumControllerA.handleIncomingMessages(messagesToA);
		maxSumControllerB.handleIncomingMessages(messagesToB);

		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentStateA = maxSumControllerA
				.computeCurrentState();
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentStateB = maxSumControllerB
				.computeCurrentState();

		/* STRUCTURE CHANGE */

		/**
		 * changing the structure (removing the constraint between agents)
		 */

		/**
		 * removing dependency of agent A state from agent B utility
		 */
		DiscreteInternalVariable<?> vaToUpdate = maxSumControllerA
				.getInternalVariable("va");
		vaToUpdate.removeFunctionDependency("fb");

		/**
		 * removing dependency of agent B state from agent A utility
		 */
		DiscreteInternalVariable<?> vbToUpdate = maxSumControllerB
				.getInternalVariable("vb");
		vbToUpdate.removeFunctionDependency("fa");

		/**
		 * removing dependency of agent A utility from agent B state
		 */
		DiscreteInternalFunction faToUpdate = maxSumControllerA
				.getInternalFunction("fa");
		faToUpdate.removeVariableDependency("vb");

		/**
		 * removing dependency of agent B utility from agent A state
		 */
		DiscreteInternalFunction fbToUpdate = maxSumControllerB
				.getInternalFunction("fb");
		fbToUpdate.removeVariableDependency("va");

		/* SECOND ITERATION */

		/**
		 * Performing the second iteration with the new structure
		 */

		messagesToB = maxSumControllerA.calculateNewOutgoingMessages();
		messagesToA = maxSumControllerB.calculateNewOutgoingMessages();

		maxSumControllerA.handleIncomingMessages(messagesToA);
		maxSumControllerB.handleIncomingMessages(messagesToB);

		currentStateA = maxSumControllerA.computeCurrentState();
		currentStateB = maxSumControllerB.computeCurrentState();

		/* STRUCTURE CHANGE */

		/**
		 * changing the structure (putting back the constraint between the
		 * agents)
		 */

		vaToUpdate = maxSumControllerA.getInternalVariable("va");
		faToUpdate = maxSumControllerA.getInternalFunction("fa");

		faToUpdate.addVariableDependency(new DiscreteExternalVariable<Color>(
				"vb", new ThreeColorsDomain(), agentBID));

		vaToUpdate.addFunctionDependency(new ExternalFunction("fb", agentBID));

		vbToUpdate = maxSumControllerB.getInternalVariable("vb");
		fbToUpdate = maxSumControllerB.getInternalFunction("fb");

		fbToUpdate.addVariableDependency(new DiscreteExternalVariable<Color>(
				"va", new ThreeColorsDomain(), agentAID));

		vbToUpdate.addFunctionDependency(new ExternalFunction("fa", agentAID));

		/* THIRD ITERATION */

		/**
		 * executing the third iterations
		 */

		messagesToB = maxSumControllerA.calculateNewOutgoingMessages();
		messagesToA = maxSumControllerB.calculateNewOutgoingMessages();

		maxSumControllerA.handleIncomingMessages(messagesToA);
		maxSumControllerB.handleIncomingMessages(messagesToB);

		currentStateA = maxSumControllerA.computeCurrentState();
		currentStateB = maxSumControllerB.computeCurrentState();

		/* CHECK */

		Map<DiscreteVariable<?>, DiscreteVariableState> state = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		state.putAll(maxSumControllerA.computeCurrentState());
		state.putAll(maxSumControllerB.computeCurrentState());
		for (DiscreteVariableState vs : (new ThreeColorsDomain()).getStates()) {
			assertTrue(1 >= CollectionUtils.cardinality(vs, state.values()));
		}

	}

}
