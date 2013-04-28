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
import maxSumController.discrete.VariableJointState;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TwoDimensionalLinearMarginalMaximisation implements
		MarginalMaximisation, MarginalMaximisationFactory {

	private ContinuousInternalFunction function;

	private Set<ContinuousVariable> variables;

	private static Log log = LogFactory
			.getLog(TwoDimensionalLinearMarginalMaximisation.class);

	public MarginalValues<?> calculateMarginalMaxFunction(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages,
			Variable<?, ?> variable) {

		Validate.isTrue(function.getDependencies().size() <= 2,
				"Currently only functions with less than or "
						+ "equal to two variables are supported");
		Validate.isTrue(function.getDependencies().size() > 0,
				"Function has no variables");

		// create a N dimensional function based on messages
		// add it to the function
		// project function onto the variable-utility plane

		if (function.getDependencies().size() == 1) {
			List<LineSegment> segments = function.getFunction().project(
					function.getVariableList().indexOf(variable));

			if (segments.isEmpty()) {
				if (function.getFunction().getPartitioning().getSimplices()
						.isEmpty()) {
					log
							.error("Function has empty partitioning and was not correctly defined");
					throw new IllegalArgumentException(
							"Function has empty partitioning and was not correctly defined");
				} else {
					throw new IllegalArgumentException("Unexpected error");
				}
			}

			PieceWiseLinearFunction upperEnvelope = UpperEnvelopeAlgorithm
					.calculateUpperEnvelope(segments);

			return new ContinuousMarginalValues(upperEnvelope);
		}

		List<PieceWiseLinearFunction> marginalFunctions = new ArrayList<PieceWiseLinearFunction>();

		for (ContinuousVariable otherVariable : function.getVariableList()) {
			if (!otherVariable.equals(variable)) {
				MarginalValues<?> marginalValues = sortedMessages
						.get(otherVariable);

				PieceWiseLinearFunction univariateFunction;
				if (marginalValues != null) {
					univariateFunction = ((ContinuousMarginalValues) marginalValues)
							.getFunction();
				} else {
					// no function from this variable exists, create a zero
					// function
					double lowerBound = otherVariable.getDomain()
							.getLowerBound();
					double upperBound = otherVariable.getDomain()
							.getUpperBound();

					univariateFunction = new PieceWiseLinearFunctionImpl(
							new LineSegment(lowerBound, 0.0, upperBound, 0.0));
				}

				MultiVariatePieceWiseLinearFunction sum = function
						.getFunction().addUnivariateFunction(
								univariateFunction,
								function.getVariableList().indexOf(
										otherVariable));

				List<LineSegment> segments = sum.project(function
						.getVariableList().indexOf(variable));

				PieceWiseLinearFunction upperEnvelope = UpperEnvelopeAlgorithm
						.calculateUpperEnvelope(segments);

				return new ContinuousMarginalValues(upperEnvelope);
			}
		}

		return null;
	}

	public void setFunction(InternalFunction function) {
		this.function = (ContinuousInternalFunction) function;
	}

	public void setVariables(Set<? extends Variable<?, ?>> variables) {
		this.variables = (Set<ContinuousVariable>) variables;
	}

	public MarginalMaximisation create() {
		return new TwoDimensionalLinearMarginalMaximisation();
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
