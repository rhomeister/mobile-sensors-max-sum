package boundedMaxSum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.VariableJointState;

/**
 * Calculate optimal solution to a problem by exhaustive search. Written for
 * random payoff graph colouring but should work for any problem represented as
 * a factor graph for max-sum
 * 
 * @author mw08v
 * 
 */
public class ArbitraryGraphColouringExhaustiveSolution {

	private boolean debug = false;

	protected VariableJointState optimalConfiguration;

	protected Map<DiscreteVariable<?>, Iterator<? extends DiscreteVariableState>> iterators;

	public ArbitraryGraphColouringExhaustiveSolution() {
		optimalConfiguration = new VariableJointState(
				new HashMap<DiscreteVariable<?>, DiscreteVariableState>());
		iterators = new HashMap<DiscreteVariable<?>, Iterator<? extends DiscreteVariableState>>();
	}

	/**
	 * Return the optimal configuration found. Result only makes sense after
	 * calculateOptimal() has been called.
	 * 
	 * @return
	 */
	public VariableJointState getOptimalConfiguration() {
		return optimalConfiguration;
	}

	/**
	 * Calculate the optimal solution by exhaustive search.
	 * 
	 * @param controller
	 *            max-sum controller with the complete factor graph prepared.
	 * @param domain
	 *            domain of the variables
	 * @return
	 */
	public double calculateOptimal(DiscreteMaxSumController<DiscreteInternalFunction> controller,
			DiscreteVariableDomain<?> domain) {
		double maxResult = Double.NEGATIVE_INFINITY;
		VariableJointState currentConfiguration = new VariableJointState(
				new HashMap<DiscreteVariable<?>, DiscreteVariableState>());

		// Initialise state iterators and set initial values.
		for (DiscreteInternalVariable<?> var : controller
				.getInternalVariables()) {
			Iterator<? extends DiscreteVariableState> it = var.getDomain()
					.iterator();
			iterators.put(var, it);
			currentConfiguration.getVariableJointStates().put(var, it.next());
			if (debug) {
				System.out.println("Configuration = " + currentConfiguration);
			}
		}

		// Iterate through all possible variable configurations
		for (int i = 0; i < Math.pow(domain.size(), controller
				.getInternalVariables().size()); i++) {

			double totalUtility = 0;
			boolean overflowed = true;

			// Find total utility for the current variable configuration
			for (DiscreteInternalFunction func : controller
					.getInternalFunctions()) {
				VariableJointState currentJointState = ((BoundedInternalFunction)func).createcurrentJointState(currentConfiguration);
				//System.out.println("evaluating conf = "+currentJointState);
				totalUtility += func.evaluate(currentJointState);
				//totalUtility += func.evaluate(currentConfiguration);
			}
			if (debug)
				System.out.println(currentConfiguration + " = " + totalUtility);
			if (totalUtility > maxResult) {
				// Best configuration so far - keep
				optimalConfiguration.getVariableJointStates().clear();
				optimalConfiguration.getVariableJointStates().putAll(
						currentConfiguration.getVariableJointStates());
				maxResult = totalUtility;
			}

			// Move to next variable configuration
			for (DiscreteInternalVariable<?> var : controller
					.getInternalVariables()) {
				Iterator<? extends DiscreteVariableState> varStateIt = iterators
						.get(var);
				if (overflowed) {
					currentConfiguration.getVariableJointStates().put(var,
							iterators.get(var).next());
				}

				if (!varStateIt.hasNext()) {
					// All states exhausted - reset
					iterators.put(var, var.getDomain().iterator());
					overflowed = true;
				} else {
					overflowed = false;
				}

			}
		}
		return maxResult;
	}

	public double calculateOptimal(DiscreteMaxSumController<DiscreteInternalFunction> controller) {
		double maxResult = Double.NEGATIVE_INFINITY;
		VariableJointState currentConfiguration = new VariableJointState(
				new HashMap<DiscreteVariable<?>, DiscreteVariableState>());

		
		int totalStateNumber = 1;

		// Initialise state iterators and set initial values.
		for (DiscreteInternalVariable<?> var : controller
				.getInternalVariables()) {
			Iterator<? extends DiscreteVariableState> it = var.getDomain()
					.iterator();
			iterators.put(var, it);
			currentConfiguration.getVariableJointStates().put(var, it.next());
			if (debug) {
				System.out.println("Configuration = " + currentConfiguration);
			}
			totalStateNumber*=var.getDomainSize();
			if (debug) {
				System.out.println(totalStateNumber);
			}
		}

		
		
		// Iterate through all possible variable configurations		
		for (int i=0;i<totalStateNumber;i++) {

			double totalUtility = 0;
			boolean overflowed = true;

			
			// Find total utility for the current variable configuration
			for (DiscreteInternalFunction func : controller
					.getInternalFunctions()) {
				VariableJointState currentJointState = ((BoundedInternalFunction)func).createcurrentJointState(currentConfiguration);
				//System.out.println("evaluating conf = "+currentJointState);
				totalUtility += func.evaluate(currentJointState);
				//totalUtility += func.evaluate(currentConfiguration);
			}
			if (debug)
				System.out.println(currentConfiguration + " = " + totalUtility);
			if (totalUtility > maxResult) {
				// Best configuration so far - keep
				optimalConfiguration.getVariableJointStates().clear();
				optimalConfiguration.getVariableJointStates().putAll(
						currentConfiguration.getVariableJointStates());
				maxResult = totalUtility;
			}

			

			// Move to next variable configuration
			for (DiscreteInternalVariable<?> var : controller
					.getInternalVariables()) {
				Iterator<? extends DiscreteVariableState> varStateIt = iterators
						.get(var);
				if (overflowed) {
					if (debug){ 
						System.out.println("advancing var "+var);
					}	
					if (iterators.get(var).hasNext()){
						currentConfiguration.getVariableJointStates().put(var,
							iterators.get(var).next());
					}
				}

				if (!varStateIt.hasNext()) {
					// All states exhausted - reset
					iterators.put(var, var.getDomain().iterator());
					overflowed = true;
				} else {
					overflowed = false;
				}
			
			}
			
			

			if (debug) {
				System.out.println(i);
			}
			
		}
		return maxResult;
	}

}
