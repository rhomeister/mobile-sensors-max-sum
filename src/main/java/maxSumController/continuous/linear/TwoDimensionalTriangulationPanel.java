package maxSumController.continuous.linear;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

public class TwoDimensionalTriangulationPanel extends JPanel {

	private static final int RADIUS = 5;

	private static final int TRANSLATE_X = 10;

	private static final int TRANSLATE_Y = 20;

	private List<NSimplex> simplices;

	private Map<NDimensionalPoint, Color> points = new HashMap<NDimensionalPoint, Color>();

	private NCube domain;

	public void setPartitioning(SimplexPartitioning partitioning) {
		setSimplices(partitioning.getSimplices(), partitioning.getDomain());
	}

	public void setSimplices(List<NSimplex> simplices, NCube domain) {
		this.simplices = simplices;
		this.domain = domain;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (simplices != null) {
			for (NSimplex simplex : simplices) {
				for (NDimensionalPoint point : simplex.getPoints()) {
					drawPoint(point, g, Color.black);
				}

				for (NDimensionalPoint point : points.keySet()) {
					drawPoint(point, g, points.get(point));
				}

				for (NDimensionalLine line : simplex.getEdges()) {
					NDimensionalPoint start = line.getStart();
					NDimensionalPoint end = line.getEnd();

					int x1 = convertXCoordinate(start.getCoordinates()[0]);
					int y1 = convertYCoordinate(start.getCoordinates()[1]);
					int x2 = convertXCoordinate(end.getCoordinates()[0]);
					int y2 = convertYCoordinate(end.getCoordinates()[1]);

					g.drawLine(x1, y1, x2, y2);
				}
			}
		}
	}

	private void drawPoint(NDimensionalPoint point, Graphics g, Color color) {
		int centerX = convertXCoordinate(point.getCoordinates()[0]);
		int centerY = convertYCoordinate(point.getCoordinates()[1]);

		g.setColor(color);
		g.fillOval(centerX - RADIUS, centerY - RADIUS, 2 * RADIUS, 2 * RADIUS);
		g.setColor(Color.black);
	}

	private int convertYCoordinate(double d) {
		return TRANSLATE_Y
				/ 2
				+ (int) (getHeight() - TRANSLATE_Y - d / domain.getDomainEnd(0)
						* (getHeight() - TRANSLATE_Y));
	}

	private int convertXCoordinate(double d) {
		return TRANSLATE_X
				/ 2
				+ (int) ((getHeight() - TRANSLATE_X) * d / domain
						.getDomainEnd(1));
	}

	public void drawPoint(NDimensionalPoint point) {
		drawPoint(point, Color.black);
	}

	public void draw(NCube domain, NSimplex simplex) {
		setSimplices(Arrays.asList(simplex), domain);
	}

	public void drawPoint(NDimensionalPoint point, Color color) {
		this.points.put(point, color);
		repaint();
	}
}
