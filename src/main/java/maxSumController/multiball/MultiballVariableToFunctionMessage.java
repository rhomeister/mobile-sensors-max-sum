package maxSumController.multiball;

import maxSumController.Function;
import maxSumController.VariableToFunctionMessage;
import maxSumController.MarginalValues;
import maxSumController.Variable;

public class MultiballVariableToFunctionMessage 
	extends VariableToFunctionMessage {

	private DerivativeFunction derivativeFunction;

	private DerivativeFunction secondDerivativeFunction;
	
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param function
	 *            is q_{i -> j}(z)
	 * @param derivativeFunction
	 *            is g_{i -> j}(z)
	 */
	
	public MultiballVariableToFunctionMessage(Variable<?, ?> sender,
			Function receiver, MarginalValues<?> function,
			DerivativeFunction derivativeFunction,
			DerivativeFunction secondDerivativeFunction) {
		super(sender, receiver, function);
		this.derivativeFunction = derivativeFunction;
		this.secondDerivativeFunction = secondDerivativeFunction;
	}

	public DerivativeFunction getDerivativeFunction() {
		return derivativeFunction;
	}	

	public void setDerivativeFunction(DerivativeFunction derivativeFunction) {
		this.derivativeFunction = derivativeFunction;
	}
	
	public DerivativeFunction getSecondDerivativeFunction() {
		return secondDerivativeFunction;
	}	

	public void setSecondDerivativeFunction(DerivativeFunction secondDerivativeFunction) {
		this.secondDerivativeFunction = secondDerivativeFunction;
	}

}
