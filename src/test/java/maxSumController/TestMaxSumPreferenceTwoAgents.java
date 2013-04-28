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

public class TestMaxSumPreferenceTwoAgents extends TestCase {

	/**
	 * The Max-sum controller for agent A
	 */
	private DiscreteMaxSumController maxSumControllerA;

	/**
	 * The Max-sum controller for agent B
	 */
	private DiscreteMaxSumController maxSumControllerB;

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
		DiscreteInternalFunction fa = new ConflictFunction("fa");

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
		DiscreteInternalFunction fb = new ConflictFunction("fb");

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
			
		//	System.out.println(messagesToB);

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

		System.out.println(currentStateA);
		System.out.println(currentStateB);

	}

}
