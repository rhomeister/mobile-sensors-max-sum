package maxSumController.discrete.bb;

import java.util.HashMap;

import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.continuous.linear.Interval;

public class EmptyMessageValueFunction extends MessageValueFunction {

	public EmptyMessageValueFunction() {
		super(new HashMap<Variable<?, ?>, MarginalValues<?>>(), null);
	}

	@Override
	public Interval getValueInterval(PartialJointVariableState state) {
		return new Interval(0.0, 0.0);
	}

}
