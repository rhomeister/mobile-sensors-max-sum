package senseSleep;

import maxSumController.continuous.ContinuousExternalVariable;
import maxSumController.continuous.ContinuousVariableDomain;

public class SensorStartExternalVariable extends ContinuousExternalVariable implements SensorStartVariable {

	private double dutyCycle;

	public SensorStartExternalVariable(String name,
			ContinuousVariableDomain domain, Comparable owningAgentIdentifier,
			double dutyCycle) {
		super(name, domain, owningAgentIdentifier);
		this.dutyCycle = dutyCycle;
	}

	public double getDutyCycle() {
		return dutyCycle;
	}

}
