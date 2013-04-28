package maxSumController.continuous;

import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

public class TestUpperEnvelopeAlgorithm extends TestCase {

	public void testSingleLineSegment() throws Exception {
		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(Collections
						.singletonList(new LineSegment(0, 0, 1, 1)));

		assertEquals(0.0, function.evaluate(0.0));
		assertEquals(1.0, function.evaluate(1.0));
	}

	public void testTwoNonOverlappingLineSegments() throws Exception {
		ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
		segments.add(new LineSegment(0, 0, 1, 1));
		segments.add(new LineSegment(2, 0, 3, 1));

		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		assertEquals(0.0, function.evaluate(0.0));
		assertEquals(0.5, function.evaluate(0.5));
		assertEquals(1.0, function.evaluate(1.0));
		assertEquals(Double.NaN, function.evaluate(1.5));
		assertEquals(0.0, function.evaluate(2.0));
		assertEquals(1.0, function.evaluate(3.0));
	}

	public void testOverlappingNonIntersectingLineSegments() throws Exception {
		ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
		segments.add(new LineSegment(0, 0, 2, 2));
		segments.add(new LineSegment(1, 2, 3, 2));

		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		assertEquals(0.0, function.evaluate(0.0));
		assertEquals(2.0, function.evaluate(1.0));
		assertEquals(2.0, function.evaluate(2.0));
		assertEquals(2.0, function.evaluate(3.0));
	}

	public void testOverlappingIntersectingLineSegments() throws Exception {
		ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
		segments.add(new LineSegment(0, 0, 4, 4));
		segments.add(new LineSegment(1, 0, 3, 4));

		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		assertEquals(0.0, function.evaluate(0.0));
		assertEquals(1.0, function.evaluate(1.0));
		assertEquals(2.0, function.evaluate(2.0));
		assertEquals(4.0, function.evaluate(3.0 - 0.000001), 0.0001);
		assertEquals(3.0, function.evaluate(3.0));
		assertEquals(4.0, function.evaluate(4.0));
	}

	public void testOverlappingIntersectingLineSegments1() throws Exception {
		ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
		segments.add(new LineSegment(0, 0, 1, 1));
		segments.add(new LineSegment(0, 1, 1, 0));

		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		assertEquals(1.0, function.evaluate(0.0));
		assertEquals(0.5, function.evaluate(0.5));
		assertEquals(1.0, function.evaluate(1.0));
	}

	public void testParallelLineSegments1() throws Exception {
		ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
		segments.add(new LineSegment(0, 0, 1, 1));
		segments.add(new LineSegment(0, 0, 1, 1));

		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		assertEquals(0.0, function.evaluate(0.0));
		assertEquals(0.5, function.evaluate(0.5));
		assertEquals(1.0, function.evaluate(1.0));
	}

	public void testParallelLineSegments2() throws Exception {
		ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
		segments.add(new LineSegment(1, 1, 2, 2));
		segments.add(new LineSegment(0, 0, 3, 3));

		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		assertEquals(0.0, function.evaluate(0.0));
		assertEquals(1.0, function.evaluate(1.0));
		assertEquals(2.0, function.evaluate(2.0));
		assertEquals(3.0, function.evaluate(3.0));
	}

	public void testParallelLineSegments3() throws Exception {
		ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
		segments.add(new LineSegment(0, 0, 2, 2));
		segments.add(new LineSegment(1, 1, 3, 3));

		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		assertEquals(0.0, function.evaluate(0.0));
		assertEquals(1.0, function.evaluate(1.0));
		assertEquals(2.0, function.evaluate(2.0));
		assertEquals(3.0, function.evaluate(3.0));
	}

	public void testMultipleLineSegments() throws Exception {
		ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
		segments.add(new LineSegment(0, 0, 4, 4));
		segments.add(new LineSegment(0, 4, 4, 0));
		segments.add(new LineSegment(1, 3, 3, 3));
		segments.add(new LineSegment(1, 1, 3, 1));

		PieceWiseLinearFunction function = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(segments);

		assertEquals(4.0, function.evaluate(0.0));
		assertEquals(3.0, function.evaluate(1.0));
		assertEquals(3.0, function.evaluate(2.0));
		assertEquals(3.0, function.evaluate(3.0));
		assertEquals(4.0, function.evaluate(4.0));
	}
}
