package maxSumController.continuous;

import java.util.Collection;
import java.util.SortedSet;

public interface PieceWiseLinearFunction {

	public SortedSet<Double> getIntervalEndpoints();

	public double evaluate(double x);

	public double getFirstIntervalEndpoint();

	public LineSegment getLineSegment(double x);

	public void addSegment(LineSegment segment);

	public double getLastIntervalEndpoint();

	public LineSegment getStrictLineSegment(double x);

	public void simplify();

	public double argMax();

	public PieceWiseLinearFunction max(PieceWiseLinearFunction other);

	public PieceWiseLinearFunction add(PieceWiseLinearFunction other);

	public void normalise();

	public double getArea();

	public double getValidDomainLength();
	
	public Collection<LineSegment> getLineSegments();

}
