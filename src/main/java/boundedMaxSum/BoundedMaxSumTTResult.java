package boundedMaxSum;

public class BoundedMaxSumTTResult extends BoundedMaxSumResult{

	protected int numberOfSensors;
	protected int numberOfTargets;
	
	public int getNumberOfSensors() {
		return numberOfSensors;
	}
	
	public void setNumberOfSensors(int numberOfSensors) {
		this.numberOfSensors = numberOfSensors;
	}
	
	public int getNumberOfTargets() {
		return numberOfTargets;
	}
	
	public void setNumberOfTargets(int numberOfTargets) {
		this.numberOfTargets = numberOfTargets;
	}
	
	public String toCSV() {
		StringBuffer sb = new StringBuffer();
		sb.append(getNumberOfSensors());
		sb.append(',');
		sb.append(getNumberOfTargets());
		sb.append(',');
		sb.append(getAverageConnectionsPerNode());
		sb.append(',');
		sb.append(getOptimalUtility());
		sb.append(',');
		sb.append(getActualUtility());
		sb.append(',');
		sb.append(getTreeUtility());
		sb.append(',');
		sb.append(getUpperBound());
		sb.append(',');
		sb.append(getSimpleUpperBound());
		sb.append(',');
		sb.append(getEdges());
		sb.append(',');
		sb.append(getEdgesDeleted());

		return sb.toString();
	}

	
}
