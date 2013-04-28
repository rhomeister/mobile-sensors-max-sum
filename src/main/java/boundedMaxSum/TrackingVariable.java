package boundedMaxSum;

import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariableDomain;

public class TrackingVariable extends DiscreteInternalVariable<TrackingObject> {

	protected TrackingSensor sensor;
	
	public TrackingVariable(String name, DiscreteVariableDomain domain, TrackingSensor sensor) {
		super(name, domain);
		this.sensor = sensor;
	}

	public TrackingSensor getSensor() {
		// TODO Auto-generated method stub
		return sensor;
	}
	
	public void setSensor(TrackingSensor sensor) {
		this.sensor = sensor;
	}

}
