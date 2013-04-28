package maxSumController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.communication.MaxSumMessage;
import maxSumController.discrete.DiscreteExternalVariable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMarginalValues;
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
 *         This example shows how to create two agents for a 3 colour graph
 *         colouring domain with Each agent has a variable and a function.
 * 
 */

public class TestMaxSumControllerTwoAgents2 extends TestCase {

	/**
	 * The Max-sum controller for agent A
	 */
	private DiscreteMaxSumController maxSumControllerA;

	/**
	 * The Max-sum controller for agent B
	 */
	private DiscreteMaxSumController maxSumControllerB;

	DiscreteInternalFunction fa = null;

	DiscreteExternalVariable<Color> vb = null;

	DiscreteInternalFunction fb = null;

	private String agentAID = "agentA";

	private String agentBID = "agentB";

	DiscreteExternalVariable<Color> va = null;

	private Map<DiscreteVariableState, Double> valuesMap_ = null;

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
		 * not control (represents the state of the other agent).
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
		 * control.
		 */
		DiscreteInternalVariable<Color> vb = new DiscreteInternalVariable<Color>(
				"vb", new ThreeColorsDomain());

		/**
		 * The external variable for the agent B; variable that the agent B can
		 * not control (represents the state of the other agent).
		 */
		va = new DiscreteExternalVariable<Color>("va", new ThreeColorsDomain(),
				agentAID);

		/**
		 * The function that represents the utility for agent A
		 */
		fb = new PreferenceInternalFunction(new ConflictFunction("fb"));

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
			Collection<MaxSumMessage<?, ?>> messagesToB = maxSumControllerA
					.calculateNewOutgoingMessages();

			MaxSumMessage<?, ?> messagesArray[] = new MaxSumMessage<?, ?>[10];
			messagesToB.toArray(messagesArray);

			// Define data Holder object and popualte with mock data for va to
			// fb messasge.
			MaxSumColorMessage maxSumDataMsg = new MaxSumColorMessage();
			maxSumDataMsg.agentId = agentAID;
			maxSumDataMsg.funcToVar = false;
			maxSumDataMsg.receiverId = "fb";
			maxSumDataMsg.senderId = "va";

			maxSumDataMsg.maxSumDataList = new MaxSumColorData[3];

			maxSumDataMsg.maxSumDataList[0] = new MaxSumColorData();
			maxSumDataMsg.maxSumDataList[1] = new MaxSumColorData();
			maxSumDataMsg.maxSumDataList[2] = new MaxSumColorData();

			// Populate mock data values.
			maxSumDataMsg.maxSumDataList[0].color = "BLUE";
			maxSumDataMsg.maxSumDataList[1].color = "GREEN";
			maxSumDataMsg.maxSumDataList[2].color = "RED";
			maxSumDataMsg.maxSumDataList[0].value = 0.090686;
			maxSumDataMsg.maxSumDataList[1].value = 0.02376;
			maxSumDataMsg.maxSumDataList[2].value = -0.044446;

			// Populate the values map.
			valuesMap_ = new HashMap<DiscreteVariableState, Double>();
			for (int j = 0; j < maxSumDataMsg.maxSumDataList.length; j++) {
				valuesMap_.put(
						new Color(maxSumDataMsg.maxSumDataList[j].color),
						maxSumDataMsg.maxSumDataList[j].value);
			}

			DiscreteMarginalValues<DiscreteVariableState> discreteMarginalValues = new DiscreteMarginalValues<DiscreteVariableState>(
					valuesMap_);

			// Create MaxSumMessage, that can be passed into MaxSumController.
			VariableToFunctionMessage variableToFunctionMessage = new VariableToFunctionMessage(
					va, fb, discreteMarginalValues);

			// change the second entry in the array, (va to fb message).
			int counter = 0;
			for (MaxSumMessage<?, ?> message : messagesToB) {
				// .add(functionToVariableMessage);
				if (counter == 1) {
					messagesToB.remove(message);
					// maxSumControllerB.handleIncomingMessages(messagesToB);
					messagesToB.add(variableToFunctionMessage);
					break;
				}
				counter++;
			}

			/** TODO: insert here */

			// check whether the messages are actually for B
			for (MaxSumMessage<?, ?> message : messagesToB) {
				assertEquals(maxSumControllerB.getAgentIdentifier(), message
						.getReceivingAgentIdentifier());
			}

			/**
			 * obtaining messages for agent A
			 */
			Collection<MaxSumMessage<?, ?>> messagesToA = maxSumControllerB
					.calculateNewOutgoingMessages();

			// Check whether the messages are actually for A.
			for (MaxSumMessage<?, ?> message : messagesToA) {
				assertEquals(maxSumControllerA.getAgentIdentifier(), message
						.getReceivingAgentIdentifier());
				System.out.println("Message to send" + message.toString());
			}

			/**
			 * Handle the incoming messages for A.
			 */
			maxSumControllerA.handleIncomingMessages(messagesToA);

			/**
			 * Handle the incoming messages for B.
			 */
			maxSumControllerB.handleIncomingMessages(messagesToB);

			System.out.println("");
		}

		maxSumControllerA.computeCurrentState();
		maxSumControllerB.computeCurrentState();

		/**
		 * Compute state for agent A and agent B
		 */
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentStateA = maxSumControllerA
				.getCurrentState();
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentStateB = maxSumControllerB
				.getCurrentState();

		Map<DiscreteVariable<?>, DiscreteVariableState> state = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();

		state.putAll(maxSumControllerA.getCurrentState());
		state.putAll(maxSumControllerB.getCurrentState());

		// check whether the graph is colored
		for (DiscreteVariableState vs : (new ThreeColorsDomain()).getStates()) {
			assertTrue(1 >= CollectionUtils.cardinality(vs, state.values()));
		}

		// print out states
		System.out.println(state);
	}

	public class MaxSumColorMessage {
		public boolean funcToVar;
		public String agentId;
		public String senderId;
		public String receiverId;
		public MaxSumColorData maxSumDataList[];
	}

	public class MaxSumColorData {
		String color;
		double value;
	}

}
