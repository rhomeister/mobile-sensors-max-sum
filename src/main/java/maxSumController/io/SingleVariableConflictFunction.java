package maxSumController.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.discrete.bb.AbstractBBDiscreteInternalFunction;
import maxSumController.discrete.bb.PartialJointVariableState;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;

public class SingleVariableConflictFunction extends AbstractBBDiscreteInternalFunction {

	DiscreteVariable<?> myVariable;
	private List<DiscreteVariable> variableExpansionOrder;

	public SingleVariableConflictFunction(String name,
			DiscreteVariable<?> myVariable) {
		super(name);
		this.myVariable = myVariable;
	}

	public void setMyVariable(DiscreteVariable<?> myVariable) {
		this.myVariable = myVariable;
	}

	public DiscreteVariable<?> getMyVariable() {
		return myVariable;
	}

	@Override
	public double evaluate(VariableJointState states) {

		int conflicts = 0;
		DiscreteVariableState myVariableState = states.getVariableJointStates()
				.get(myVariable);

		for (DiscreteVariable<?> v : states.getVariables()) {
			DiscreteVariableState vs = states.getVariableJointStates().get(v);
			if (!myVariable.equals(v) && vs.equals(myVariableState)) {
				conflicts++;
			}
		}

		return -conflicts;

	}

	@Override
	public double getLowerBound(PartialJointVariableState state) {
		List<DiscreteVariable<Color>> undeterminedVariables = state
				.getUndeterminedVariables();

		if (state.isSet(myVariable)) {
			// get my variable. if set check conflicts. lowerbound is if all
			// other
			// variables that can be the same color are setting
			// the same value as this variable

			Color myColor = (Color) state.getState(myVariable);
			int conflicts = getCurrentConflicts(state, myColor);

			for (DiscreteVariable<Color> discreteVariable : undeterminedVariables) {
				if (discreteVariable.getDomain().getStates().contains(myColor)) {
					conflicts++;
				}
			}

			return -conflicts;

		} else {
			// if not set, worst case scenario is if all other variables take on
			// the
			// same value as the mostly used state that this variable can take

			Collection<Color> states = state.getStates();

			for (DiscreteVariable<Color> discreteVariable : undeterminedVariables) {
				if (!discreteVariable.equals(myVariable))
					states.addAll(discreteVariable.getDomain().getStates());
			}

			Map<Color, Integer> cardinalityMap = CollectionUtils
					.getCardinalityMap(states);

			int maxConflicts = 0;

			for (Color myColor : ((Set<Color>) myVariable.getDomain()
					.getStates())) {
				Integer potentialConflicts = cardinalityMap.get(myColor);

				if (potentialConflicts != null) {
					maxConflicts = Math.max(maxConflicts, potentialConflicts);
				}
			}

			return -maxConflicts;

		}
	}

	private int getCurrentConflicts(PartialJointVariableState state,
			Color myColor) {
		int conflicts = -1;

		for (Color color : (Collection<Color>) state.getStates()) {
			if (color.equals(myColor)) {
				conflicts++;
			}
		}
		return conflicts;
	}

	@Override
	public double getUpperBound(PartialJointVariableState state) {
		if (state.isSet(myVariable)) {
			return -getCurrentConflicts(state, (Color) state
					.getState(myVariable));
		} else {
			return 0;
		}
	}

	@Override
	public List<DiscreteVariable> getVariableExpansionOrder() {
		if (variableExpansionOrder == null) {
			variableExpansionOrder = new ArrayList<DiscreteVariable>(
					getDiscreteVariableDependencies());
		}

		return variableExpansionOrder;
	}

	// public static void main(String[] args) {
	//
	// Map<Variable, VariableState> variables = new HashMap<Variable,
	// VariableState>();
	//
	// ColorDomain threeColorDomain = new ColorDomain();
	// threeColorDomain.add(Colors.RED);
	// threeColorDomain.add(Colors.BLUE);
	// threeColorDomain.add(Colors.GREEN);
	//
	// Variable redVariable = new InternalVariable("red", threeColorDomain);
	// Variable blueVariable = new InternalVariable("blue", threeColorDomain);
	// variables.put(redVariable, Colors.RED);
	// variables.put(new InternalVariable("red1", threeColorDomain),
	// Colors.RED);
	// variables.put(new InternalVariable("red2", threeColorDomain),
	// Colors.RED);
	// variables.put(blueVariable, Colors.BLUE);
	// variables.put(new InternalVariable("blue1", threeColorDomain),
	// Colors.BLUE);
	//
	// System.out.println(new SingleVariableConflictFunction("", redVariable)
	// .evaluate(new VariableJointState(variables)));
	// System.out.println(new SingleVariableConflictFunction("", blueVariable)
	// .evaluate(new VariableJointState(variables)));
	// }
	
	@Override
	public double getLowerBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		throw new NotImplementedException();
	}
	
	
	@Override
	public double getUpperBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		throw new NotImplementedException();
	}

}
