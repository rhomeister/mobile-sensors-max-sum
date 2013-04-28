package boundedMaxSum;

import java.util.Set;

import maxSumController.discrete.DiscreteVariableDomain;

public class TrackingDomainFactory {
	static DiscreteVariableDomain<TrackingObject> buildTrackingDomain(Set<TrackingObject> tobjs){
		DiscreteVariableDomain<TrackingObject> td = new TrackingDomain();
		for (TrackingObject trackingObject : tobjs) {
			td.add(trackingObject);
		}
		return td;
	}
}
