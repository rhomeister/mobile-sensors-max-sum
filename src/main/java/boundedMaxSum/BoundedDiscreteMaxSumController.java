package boundedMaxSum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisationFactory;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteMaximiser;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import boundedMaxSum.treeformation.ghs.GHSController;
import boundedMaxSum.treeformation.jung.JUNGTreeFormationAlgorithm;

/**
 * Represents a controller that operates on a spanning tree of the original
 * factor graph. Offers methods specific to bounded optimisation and maintains a
 * copy of the original factor graph to extract evaluation measures.
 * 
 * @author sandrof
 * 
 */

public class BoundedDiscreteMaxSumController extends
DiscreteMaxSumController<BoundedInternalFunction> {

	protected Set<DiscreteInternalVariable<?>> initVariables = new HashSet<DiscreteInternalVariable<?>>();

	protected Set<BoundedInternalFunction> initFunctions = new HashSet<BoundedInternalFunction>();

	private double bound;

	private Set<BoundedInternalFunction> updatedFunctions;

	private Map<DiscreteVariable<?>, DiscreteVariableState> optimalState = null;

	private boolean useGHSTreeAlgorithm = true;

	private boolean initialized;

	protected int storage = 0;

	public void setUseGHSTreeAlgorithm(boolean useGHSTreeAlgorithm) {
		this.useGHSTreeAlgorithm = useGHSTreeAlgorithm;
	}

	public BoundedDiscreteMaxSumController(Comparable<?> agentIdentifier) {
		super(agentIdentifier);
		// TODO Auto-generated constructor stub
	}

	public BoundedDiscreteMaxSumController(Comparable<?> agentIdentifier,
			MarginalMaximisationFactory maximiserFactory) {
		super(agentIdentifier, maximiserFactory);
	}

	/**
	 * compute the upper bound by accumulating the maximum value of all
	 * functions
	 * 
	 */
	public double computeSimpleUpperBound() {
		double upperBound = 0.;
		for (DiscreteInternalFunction function : initFunctions) {
			LinkBoundedInternalFunction linkFunction = (LinkBoundedInternalFunction) function;
			upperBound += linkFunction.getMaximumBound();
		}
		return upperBound;
	}

	/**
	 * compute the lower bound by accumulating the minimum of all functions
	 */
	public double computeSimpleLowerBound() {
		double lowerBound = 0.;
		for (DiscreteInternalFunction function : initFunctions) {
			LinkBoundedInternalFunction linkFunction = (LinkBoundedInternalFunction) function;
			lowerBound += linkFunction.getMinimumBound();
		}
		return lowerBound;
	}

	/**
	 * synchronises the initial factor graph copying the relationships
	 * established among variables and functions
	 */
	protected void synchroniseInitFactorGraph() {

		for (DiscreteInternalVariable<?> v : getInternalVariables()) {
			initVariables.add(v.clone());
		}

		for (BoundedInternalFunction function : getInternalFunctions()) {
			initFunctions.add(function.clone());
		}

		for (BoundedInternalFunction initf : initFunctions) {
			BoundedInternalFunction intf = getInternalFunction(initf.getName());
			// Set<DiscreteInternalVariable<?>> updateVariables = new
			// HashSet<DiscreteInternalVariable<?>>();
			for (Variable<?, ?> intv : intf.getVariableDependencies()) {
				DiscreteInternalVariable<?> initv = getInitInternalVariable(intv
						.getName());
				initf.addVariableDependency(initv);
				// updateVariables.add(initv);
			}
			// initf.updateVariableDependency(updateVariables);
		}

	}

	private DiscreteInternalVariable<?> getInitInternalVariable(String name) {
		for (DiscreteInternalVariable<?> v : initVariables) {
			if (v.getName().equals(name)) {
				return v;
			}
		}
		throw new IllegalArgumentException("Variable " + name
				+ " does not exist");
	}

	/**
	 * Initialises the max sum controller and runs the tree formation algorithm.
	 * In order to do this, it first copies the original factor graph using
	 * synchroniseInitFactorGraph Then, it creates a GHSTreeFormationController
	 * and executes this to to create a tree with maximum weight.
	 * 
	 * @throws Exception
	 *             iff an error occurs during the treeformation process
	 */
	public void initialize() throws Exception {
		if (!initialized) {

			synchroniseInitFactorGraph();
			TreeFormationAlgorithm treecontroller;

			if (useGHSTreeAlgorithm)
				treecontroller = new GHSController(this);
			else
				treecontroller = new JUNGTreeFormationAlgorithm(this);

			treecontroller.execute();
			if (useGHSTreeAlgorithm) {
				storage = ((GHSController) treecontroller).getStorageUsed();
			}
			updateFactorGraph(treecontroller);
			initialized = true;
		}
	}

	/**
	 * updates the internal factor graph according to the tree formed by the
	 * treeController. Functions that have eliminated dependencies are
	 * substituted with new functions that do not depend on the eliminated
	 * variables. The original factor graph is preserved.
	 * 
	 * @param treeController
	 */
	protected void updateFactorGraph(TreeFormationAlgorithm treeController) {
		bound = 0.;
		updatedFunctions = new HashSet<BoundedInternalFunction>();
		Set<BoundedInternalFunction> addedFunctions = new HashSet<BoundedInternalFunction>();
		Set<BoundedInternalFunction> removedFunctions = new HashSet<BoundedInternalFunction>();
		for (DiscreteInternalFunction function : functions) {
			LinkBoundedInternalFunction linkFunction = (LinkBoundedInternalFunction) function;

			if (treeController.hasRejectBranch(function)) {
				// Validate.isTrue(node.getRejectedVariables().size() <= 1);
				for (DiscreteInternalVariable<?> rejectedVariable : treeController
						.getRejectedVariables(function)) {

					// System.out.println("function " + linkFunction.getName() +
					// " weights ");
					// System.out.println(var.getName()+ " w : "+
					// linkFunction.getWeight(var.getName()));
					// Validate
					// .isTrue(node.edgeMap.get(varNode).getWeight() == (-1 *
					// linkFunction
					// .getWeight(var.getName())));
					bound += treeController.getWeight(function,
							rejectedVariable);
					// bound += -1*node.edgeMap.get(varNode).getWeight();
					rejectedVariable.removeFunctionDependency(linkFunction
							.getName());
				}
				// bound += linkFunction.getBound(node.getRejectedVariables());

				LinkBoundedInternalFunction newFunction = linkFunction
				.getNewFunction(treeController
						.getRejectedVariables(function));
				addedFunctions.add(newFunction);
				updatedFunctions.add(linkFunction);
				removedFunctions.add(linkFunction);
				// BoundedInternalFunction functionToRemove =
				// getInternalFunction(linkFunction
				// .getName());
				// functions.remove(functionToRemove);

				for (DiscreteInternalVariable otherVar : treeController
						.getKeptVariables(function)) {
					DiscreteInternalVariable<?> internalVar = getInternalVariable(otherVar
							.getName());
					internalVar
					.removeFunctionDependency(linkFunction.getName());
					internalVar.addFunctionDependency(newFunction);
				}
			}
		}

		// removing functions
		for (BoundedInternalFunction function : removedFunctions) {
			functions.remove(function);
		}

		// adding the new functions
		for (BoundedInternalFunction function : addedFunctions) {
			addInternalFunction(function);
		}
	}

	/**
	 * Returns the maximum possible difference between the solution that will be
	 * computed on the spanning tree to the optimal solution on the original
	 * factor graph.
	 * 
	 * It is obtained summing all the weights of the dependencies which were
	 * eliminated to form the spanning tree.
	 * 
	 * @return the bound on solution quality
	 */
	public double getBound() {
		return bound;
	}

	/**
	 * Returns the set of the original function that were updated (i.e., a
	 * dependency was eleiminated ) to form the spanning tree
	 * 
	 * @return the set of updated functions
	 */
	public Set<BoundedInternalFunction> getUpdatedFunctions() {
		return updatedFunctions;
	}

	/**
	 * Returns the value induced by the solution, represented by optimalState,
	 * on the spanning tree
	 * 
	 * @param optimalState
	 *            the map representing the joint variable optimalState to
	 *            evaluate
	 * @return the value induced by the optimalState on the spanning tree
	 */
	public double getSpanningTreeValue(
			Map<? extends DiscreteVariable<?>, DiscreteVariableState> state) {
		// TODO compare with createcurrentState implementation
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> newState = new HashMap<DiscreteInternalVariable<?>, DiscreteVariableState>();
		for (Variable initVar : state.keySet()) {
			newState.put(getInternalVariable(initVar.getName()),
					(DiscreteVariableState) state.get(initVar));
		}

		double treeUtility = 0.;
		for (DiscreteInternalFunction function : getInternalFunctions()) {
			// Map<DiscreteInternalVariable<?>, DiscreteVariableState>
			// optimalState = controller.computeCurrentState();
			VariableJointState jState = new VariableJointState(newState);
			// VariableJointState localjstate =
			// ((BoundedInternalFunction)function).createcurrentJointState(jState);
			treeUtility += function.evaluateRestricted(jState);
			// treeUtility += function.evaluate(localjstate);
		}
		return treeUtility;
	}

	/**
	 * Returns the value induced by the solution, represented by optimalState,
	 * on the original factor graph
	 * 
	 * @param optimalState
	 *            the map representing the joint variable optimalState to
	 *            evaluate
	 * @return the value induced by the optimalState on the original factor
	 *         graph
	 */
	public double getOriginalFactorGraphValue(
			Map<? extends DiscreteVariable<?>, DiscreteVariableState> state) {

		double utility = 0.;
		for (DiscreteInternalFunction function : initFunctions) {
			// Map<DiscreteInternalVariable<?>, DiscreteVariableState>
			// optimalState = controller.computeCurrentState();
			VariableJointState jState = new VariableJointState(state);
			// VariableJointState localjstate =
			// ((BoundedInternalFunction)function).createcurrentJointState(jState);
			utility += function.evaluateRestricted(jState);
			// treeUtility += function.evaluate(localjstate);
		}
		return utility;
	}

	/**
	 * Returns the value of the optimal solution (computed with a brute force
	 * approach)
	 * 
	 * @return the value of the optimal solution
	 */
	public double getOptimalValue() {
		// if (optimalState == null) {
		DiscreteMaximiser maximiser = new DiscreteMaximiser(initVariables,
				initFunctions);
		optimalState = maximiser.getOptimalState();
		// }

		return getOriginalFactorGraphValue(optimalState);
	}

	/**
	 * Returns the optimal state (computed with a brute force approach)
	 * 
	 * @return the optimal state
	 */
	public Map<DiscreteVariable<?>, DiscreteVariableState> getOptimalState() {
		if (optimalState == null) {
			DiscreteMaximiser maximiser = new DiscreteMaximiser(initVariables,
					initFunctions);
			optimalState = maximiser.getOptimalState();
		}

		return optimalState;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<Internal Factor Graph> \n");

		for (DiscreteInternalVariable<?> variable : variables) {
			buffer.append(variable.getName() + " "
					+ variable.getFunctionDependencies() + "\n");
		}

		for (InternalFunction function : functions) {
			buffer.append(function.getName() + " "
					+ function.getVariableDependencies() + "\n");
		}
		buffer.append("</Internal Factor Graph> \n");

		buffer.append("<Original Factor Graph> \n");

		for (DiscreteInternalVariable<?> variable : initVariables) {
			buffer.append(variable.getName() + " "
					+ variable.getFunctionDependencies() + "\n");
		}

		for (InternalFunction function : initFunctions) {
			buffer.append(function.getName() + " "
					+ function.getVariableDependencies() + "\n");
		}
		buffer.append("</Original Factor Graph> \n");

		return buffer.toString();
	}

	/**
	 * Computes the approximation ratio for the provided state. The
	 * approximation ratio is every rho such that: V* <= rho V' where V* is the
	 * optimal unknown solution and V' is the approximated solution. In our
	 * algorithm rho = 1 + (Vm + W - V')/V' where W is the bound and Vm is the
	 * value of the state on the spanning tree.
	 * 
	 * @param state
	 *            the state for which we want to compute the approximation ratio
	 * @return the approximation ratio
	 */
	public double getApproxRatio(
			Map<DiscreteInternalVariable<?>, DiscreteVariableState> state) {

		double Vm = getSpanningTreeValue(state);
		double Vprime = getOriginalFactorGraphValue(state);
		double W = getBound();
		return 1 + ((Vm + W - Vprime) / Vprime);
	}

	public DiscreteInternalVariable<?> getInitVariable(String name) {
		for (DiscreteInternalVariable<?> v : initVariables) {
			if (v.getName().equals(name)) {
				return v;
			}
		}
		throw new IllegalArgumentException("Internal Variable " + name
				+ " does not exist");
	}

	public BoundedInternalFunction getInitFunction(String name) {
		for (BoundedInternalFunction f : initFunctions) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		throw new IllegalArgumentException("Internal Function " + name
				+ " does not exist");
	}

	public Set<BoundedInternalFunction> getInitFunctions() {
		return initFunctions;
	}

	public Set<DiscreteInternalVariable<?>> getInitVariables() {
		return initVariables;
	}

	public int getStorageUsed() {
		return storage;
	}
}
