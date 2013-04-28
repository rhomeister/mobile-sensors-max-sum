package maxSumController.continuous.linear;

import org.apache.commons.lang.Validate;

public class NDimensionalLine implements NDimensionalObject {

	private NDimensionalPoint start;

	private NDimensionalPoint end;

	public NDimensionalLine(NDimensionalPoint start, NDimensionalPoint end) {
		Validate.isTrue(start.getDimensionCount() == end.getDimensionCount());

		this.start = start;
		this.end = end;
	}

	public NDimensionalPoint getEnd() {
		return end;
	}

	public NDimensionalPoint getStart() {
		return start;
	}

	public int getDimensionCount() {
		return start.getDimensionCount();
	}

	@Override
	public String toString() {
		return start + " -> " + end;
	}

	public NCube getBoundingBox() {
		NCube boundingBox = new NCube(start.getDimensionCount());

		for (int i = 0; i < getDimensionCount(); i++) {
			double lowerBound = Math.min(start.getCoordinates()[i], end
					.getCoordinates()[i]);
			double upperBound = Math.max(start.getCoordinates()[i], end
					.getCoordinates()[i]);

			boundingBox.setBoundaries(i, lowerBound, upperBound);
		}

		return boundingBox;
	}
}
