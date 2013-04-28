package boundedMaxSum;

import maxSumController.DiscreteVariableState;

public class TrackingObject implements Comparable<TrackingObject>, DiscreteVariableState {

	protected int id;
	protected double x;
	protected double y;
	
	
	public TrackingObject(int id) {
		this.id = id;
	}
	
	public TrackingObject(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TrackingObject) {
			TrackingObject trobj = (TrackingObject) obj;
			return (this.id == trobj.getId()); 
		}
		return false;
	}
	
	@Override
	public int compareTo(TrackingObject o) {
		return (this.id - o.getId());
	}

	@Override
	public String toString() {
		String res = "";
		res = res + id + ": " + x + "," + y;
		return res;
	}
	
	double getDistance(TrackingObject o){
		return Math.sqrt((x-o.getX())*(x-o.getX()) + (y-o.getY())*(y-o.getY()));	
	}
	
}
