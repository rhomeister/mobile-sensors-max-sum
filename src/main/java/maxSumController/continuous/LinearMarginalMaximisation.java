package maxSumController.continuous;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisation;
import maxSumController.MarginalMaximisationFactory;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.VariableState;
import maxSumController.continuous.linear.ContinuousInternalFunction;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunction;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunctionUtilities;
import maxSumController.discrete.VariableJointState;

import org.apache.commons.lang.NotImplementedException;

public class LinearMarginalMaximisation implements MarginalMaximisation,
		MarginalMaximisationFactory {

	private ContinuousInternalFunction function;

	private Set<ContinuousVariable> variables;

	public MarginalValues<?> calculateMarginalMaxFunction(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages,
			Variable<?, ?> variable) {

		// create a N dimensional function based on messages
		// add it to the function
		// project function onto the variable-utility plane

		List<PieceWiseLinearFunction> marginalFunctions = new ArrayList<PieceWiseLinearFunction>();

		for (ContinuousVariable otherVariable : function.getVariableList()) {
			if (otherVariable.equals(variable)) {
				ContinuousVariable continuousVariable = (ContinuousVariable) variable;
				double lowerBound = continuousVariable.getDomain()
						.getLowerBound();
				double upperBound = continuousVariable.getDomain()
						.getUpperBound();

				marginalFunctions.add(new PieceWiseLinearFunctionImpl(
						new LineSegment(lowerBound, 0.0, upperBound, 0.0)));
			} else {
				MarginalValues<?> marginalValues = sortedMessages
						.get(otherVariable);

				if (marginalValues != null) {
					marginalFunctions
							.add(((ContinuousMarginalValues) marginalValues)
									.getFunction());
				} else {
					double lowerBound = otherVariable.getDomain()
							.getLowerBound();
					double upperBound = otherVariable.getDomain()
							.getUpperBound();

					marginalFunctions.add(new PieceWiseLinearFunctionImpl(
							new LineSegment(lowerBound, 0.0, upperBound, 0.0)));
				}
			}
		}

		MultiVariatePieceWiseLinearFunction gridFunction = MultiVariatePieceWiseLinearFunctionUtilities
				.createMultiDimensionalGridFunction(marginalFunctions);

		MultiVariatePieceWiseLinearFunction sum = function.getFunction().add(
				gridFunction);

		List<LineSegment> segments = sum.project(function.getVariableList()
				.indexOf(variable));

		PieceWiseLinearFunction upperEnvelope = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		return new ContinuousMarginalValues(upperEnvelope);
	}

	public void setFunction(InternalFunction function) {
		this.function = (ContinuousInternalFunction) function;
	}

	public void setVariables(Set<? extends Variable<?, ?>> variables) {
		this.variables = (Set<ContinuousVariable>) variables;
	}

	public MarginalMaximisation create() {
		return new LinearMarginalMaximisation();
	}

	@Override
	public VariableState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState, Variable<?, ?> targetVariable) {
		throw new NotImplementedException();
	}

	@Override
	public VariableJointState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState) {
		throw new NotImplementedException();
	}
}
