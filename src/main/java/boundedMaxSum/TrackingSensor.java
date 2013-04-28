package boundedMaxSum;

public class TrackingSensor extends TrackingObject{

	protected double visibilityRange;
	
	public TrackingSensor(int id) {
		super(id);
	}

	public double getVisibilityRange() {
		return visibilityRange;
	}
	
	public void setVisibilityRange(double visibilityRange) {
		this.visibilityRange = visibilityRange;
	}
	
	public TrackingSensor(int id, double x, double y, double visibilityRange){
		super(id,x,y);
		this.visibilityRange = visibilityRange;
	}
	
	double getIdentificationProbability(TrackingObject trob){
		double dist = getDistance(trob);
		double F = visibilityRange;
		double C = 0.001;
		double insideProb = -(1/(F-C))*dist + (F/(F-C));
		double prob = 1.0;
		if (dist > C && dist < visibilityRange){
			prob = insideProb;
		}
		else if (dist >= visibilityRange){
			prob = 0.0;
		}
		return prob;
	}
	
}
