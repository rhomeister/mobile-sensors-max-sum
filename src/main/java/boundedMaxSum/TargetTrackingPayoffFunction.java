package boundedMaxSum;

import java.util.HashSet;
import java.util.Set;

import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

import org.apache.commons.lang.NotImplementedException;

public class TargetTrackingPayoffFunction extends LinkBoundedInternalFunction {

	boolean debug = false;

	protected TrackingObject target;
	protected Set<DiscreteInternalVariable<TrackingObject>> variables;

	private double basicP = 0.1;

	public TargetTrackingPayoffFunction(String name, TrackingObject target,
			Set<DiscreteInternalVariable<TrackingObject>> variables) {
		super(name);
		this.target = target;
		this.variables = variables;
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(String deletedVariable) {
		// same function without variable to delete
		Set<DiscreteInternalVariable<TrackingObject>> newVariables = new HashSet<DiscreteInternalVariable<TrackingObject>>();
		for (DiscreteInternalVariable<TrackingObject> discreteInternalVariable : variables) {
			if (!discreteInternalVariable.getName().equals(deletedVariable)) {
				newVariables.add(discreteInternalVariable);
			}
		}
		return (new TargetTrackingPayoffFunction(getName() + "d"
				+ deletedVariable, target, newVariables));
	}

	@Override
	public double getWeight(String deletedVariableName) {
		// maybe is when they do not look ?
		for (DiscreteInternalVariable<TrackingObject> discreteInternalVariable : variables) {
			if (discreteInternalVariable.getName().equals(deletedVariableName)) {
				TrackingVariable tracVar = (TrackingVariable) discreteInternalVariable;
				double p = tracVar.getSensor().getIdentificationProbability(
						target);
				if (debug) {
					System.out.println("p = " + p);
					System.out.println("res = "
							+ (Math.log(1 - (1 - basicP) * (1 - p)) - Math
									.log(1 - (1 - basicP))));
				}

				return (Math.log(1 - (1 - basicP) * (1 - p)) - Math
						.log(1 - (1 - basicP)));
			}
		}
		if (debug) {
			System.out.println("Trying to delete unexisting variable");
		}
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double getMaximumBound() {
		// All variables look at this sensor
		double jointp = 1 - basicP;
		for (DiscreteVariable<?> discreteVariable : variables) {
			TrackingVariable tracVar = (TrackingVariable) discreteVariable;
			// System.out.println("dist var "+" sensors"+
			// tracVar.getSensor().getId()+
			// " "+tracVar.getSensor().getDistance(target));
			double prob = tracVar.getSensor().getIdentificationProbability(
					target);
			jointp *= (1 - prob);
			// System.out.println("jointP" + jointp);
		}
		return Math.log(1 - jointp);
	}

	@Override
	public double getMinimumBound() {
		// no variable looks at this sensor
		if (debug) {
			System.out.println("Get Minimum bound called");
		}
		return Math.log(1 - (1 - basicP));
	}

	@Override
	public double evaluate(VariableJointState state) {
		double jointp = 1 - basicP;
		for (DiscreteVariable<?> discreteVariable : variables) {
			TrackingVariable tracVar = (TrackingVariable) discreteVariable;
			if (state.get(tracVar).equals(target)) {
				// System.out.println("dist var "+" sensors"+
				// tracVar.getSensor().getId()+
				// " "+tracVar.getSensor().getDistance(target));
				double prob = tracVar.getSensor().getIdentificationProbability(
						target);
				jointp *= (1 - prob);
				// System.out.println("jointP" + jointp);
			}
		}
		return Math.log(1 - jointp);
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(
			Set<DiscreteInternalVariable> rejectedVariables) {
		String deletedVariables = "";
		for (DiscreteInternalVariable discreteInternalVariable : rejectedVariables) {
			deletedVariables = deletedVariables
					+ discreteInternalVariable.getName();
		}
		Set<DiscreteInternalVariable<TrackingObject>> newVariables = new HashSet<DiscreteInternalVariable<TrackingObject>>();
		newVariables.addAll(variables);
		newVariables.removeAll(rejectedVariables);
		return (new TargetTrackingPayoffFunction(getName() + "d"
				+ deletedVariables, target, newVariables));
	}

	@Override
	public double getBound(Set<DiscreteInternalVariable> rejectedVariables) {
		double jointP = 1 - basicP;
		for (DiscreteInternalVariable<TrackingObject> discreteInternalVariable : rejectedVariables) {
			TrackingVariable tracVar = (TrackingVariable) discreteInternalVariable;
			double p = tracVar.getSensor().getIdentificationProbability(target);
			if (debug) {
				System.out.println("get bound p = " + p);
			}
			jointP *= (1 - p);
		}
		if (debug) {
			System.out.println("Max = " + Math.log(1 - jointP));
			System.out.println("Min = " + Math.log(1 - (1 - basicP)));
		}
		return (Math.log(1 - jointP) - Math.log(1 - (1 - basicP)));
	}

	@Override
	public BoundedInternalFunction clone() {
		throw new NotImplementedException("need to implement this clone method");
	}

}
