package senseSleep;

import maxSumController.continuous.ContinuousInternalVariable;
import maxSumController.continuous.ContinuousVariableDomainImpl;

public class SensorStartInternalVariable extends ContinuousInternalVariable
		implements SensorStartVariable {

	private double dutyCycle;

	public SensorStartInternalVariable(String name, double dutyCycle,
			double scheduleIntervalLength) {
		super(name, new ContinuousVariableDomainImpl(0, scheduleIntervalLength));
		this.dutyCycle = dutyCycle;
	}

	public double getDutyCycle() {
		return dutyCycle;
	}

}
