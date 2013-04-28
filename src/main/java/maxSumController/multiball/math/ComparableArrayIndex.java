package maxSumController.multiball.math;

public class ComparableArrayIndex implements Comparable<ComparableArrayIndex> {

	public double[] array;
	public int index;
	
	public ComparableArrayIndex(double[] array, int index) {
		this.array = array;
		this.index = index;
	}
	
	@Override
	public int compareTo(ComparableArrayIndex other) {
		double val = array[index];
		double otherVal = other.array[other.index];
		if (val == otherVal) return 0;
		return ((val > otherVal) ? 1 : -1);
	}
	
}
