package maxSumController.continuous.linear;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.lang.Validate;

public class TwoDimensionalTriangulationGUI extends JFrame {

	private TwoDimensionalTriangulationPanel panel;

	public TwoDimensionalTriangulationGUI() {
		this("");
	}

	public TwoDimensionalTriangulationGUI(String title) {
		panel = new TwoDimensionalTriangulationPanel();
		panel.setPreferredSize(new Dimension(800, 800));

		getContentPane().add(panel);

		setTitle(title);

		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void draw(SimplexPartitioning partitioning) {
		Validate.isTrue(partitioning.getDimensionCount() == 2);
		panel.setPartitioning(partitioning);
	}

	public void draw(List<NSimplex> simplices, NCube domain) {
		panel.setSimplices(simplices, domain);
	}

	public static void main(String[] args) {
		TwoDimensionalTriangulationGUI gui = new TwoDimensionalTriangulationGUI();

		NCube domain = new NCube(new double[] { 0, 0 }, new double[] { 1, 1 });
		NDimensionalPoint a = new NDimensionalPoint(0, 0);
		NDimensionalPoint b = new NDimensionalPoint(1, 0);
		NDimensionalPoint c = new NDimensionalPoint(0, 1);
		NDimensionalPoint d = new NDimensionalPoint(1, 1);
		NDimensionalPoint e = new NDimensionalPoint(0.2, 0.8);
		NDimensionalPoint q = new NDimensionalPoint(0.5, 0.5);
		NDimensionalPoint f = new NDimensionalPoint(0.2, 0.5);
		NDimensionalPoint g = new NDimensionalPoint(0.8, 0.5);

		SimplexPartitioning partitioning1 = new SimplexPartitioning(domain);
		SimplexPartitioning partitioning2 = new SimplexPartitioning(domain);

		partitioning1.add(new NSimplex(a, b, d));
		partitioning1.add(new NSimplex(a, c, d));

		partitioning2.add(new NSimplex(a, c, f));
		partitioning2.add(new NSimplex(a, b, f));
		partitioning2.add(new NSimplex(b, d, f));
		partitioning2.add(new NSimplex(c, f, d));
		partitioning2.split(g);
		partitioning2.split(new NDimensionalLine(a, g));
		partitioning2.split(new NDimensionalLine(c, g));

		SimplexPartitioning union = partitioning2.union(partitioning1);

		gui.draw(union);
	}

	public void draw(NDimensionalPoint point) {
		panel.drawPoint(point);
	}

	public void draw(MultiVariatePieceWiseLinearFunction result) {
		draw(result.getPartitioning());
	}

	public void draw(NCube domain, NSimplex simplex) {
		panel.draw(domain, simplex);
	}

	public void draw(NDimensionalPoint point, Color color) {
		panel.drawPoint(point, color);
	}
}
