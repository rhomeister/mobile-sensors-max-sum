package maxSumController.multiball;

import maxSumController.Function;
import maxSumController.FunctionToVariableMessage;
import maxSumController.MarginalValues;
import maxSumController.Variable;

public class MultiballFunctionToVariableMessage extends
		FunctionToVariableMessage {

	private DerivativeFunction derivativeFunction;
	
	private DerivativeFunction secondDerivativeFunction;

	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param function
	 *            is r_{i -> j}(z)
	 * @param derivativeFunction
	 *            is f_{i -> j}(z)
	 */
	public MultiballFunctionToVariableMessage(Function sender,
			Variable<?, ?> receiver, MarginalValues<?> function,
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
