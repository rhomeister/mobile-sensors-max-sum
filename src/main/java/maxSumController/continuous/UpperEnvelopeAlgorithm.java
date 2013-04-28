package maxSumController.continuous;

import java.util.List;

public class UpperEnvelopeAlgorithm {

	private UpperEnvelopeAlgorithm() {

	}

	public static PieceWiseLinearFunction calculateUpperEnvelope(
			List<LineSegment> lineSegments) {

		int size = lineSegments.size();

		if (size >= 2) {
			PieceWiseLinearFunction upperEnvelope1 = calculateUpperEnvelope(lineSegments
					.subList(0, (size / 2)));
			PieceWiseLinearFunction upperEnvelope2 = calculateUpperEnvelope(lineSegments
					.subList((size / 2), size));

			return upperEnvelope1.max(upperEnvelope2);
		} else {
			return new PieceWiseLinearFunctionImpl(lineSegments.get(0));
		}
	}
}
