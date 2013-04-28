package maxSumController.visualization;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import maxSumController.discrete.DiscreteMaxSumController;

public class FactorGraphVisualisationUtils {

	public static void visualise(DiscreteMaxSumController<?> controller) {
		JungFactorGraphAdapter graph = new JungFactorGraphAdapter(controller);

		FactorGraphPane pane = new FactorGraphPane(graph);

		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
}
