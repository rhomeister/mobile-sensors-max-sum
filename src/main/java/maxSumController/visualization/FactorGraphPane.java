package maxSumController.visualization;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import maxSumController.DiscreteInternalFunction;
import maxSumController.FactorGraphNode;
import maxSumController.Function;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteVariable;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;

public class FactorGraphPane extends JPanel {

	private int sizeShape = 40;

	private GraphZoomScrollPane scrollPane;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3114598809707408203L;

	public FactorGraphPane(JungFactorGraphAdapter graph) {
		super(new BorderLayout());

		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<FactorGraphNode, FactorGraphEdge> layout = new FRLayout2<FactorGraphNode, FactorGraphEdge>(
				graph);

		final VisualizationViewer<FactorGraphNode, FactorGraphEdge> vv = new VisualizationViewer<FactorGraphNode, FactorGraphEdge>(
				layout);

		DefaultModalGraphMouse<FactorGraphNode, FactorGraphEdge> gm = new DefaultModalGraphMouse<FactorGraphNode, FactorGraphEdge>();
		gm.setMode(ModalGraphMouse.Mode.PICKING);

		vv.addGraphMouseListener(new GraphMouseListener<FactorGraphNode>() {

			@Override
			public void graphReleased(FactorGraphNode v, MouseEvent me) {
			}

			@Override
			public void graphPressed(FactorGraphNode v, MouseEvent me) {
			}

			@Override
			public void graphClicked(FactorGraphNode v, MouseEvent me) {
				createDialog(v);
			}
		});

		vv.setGraphMouse(gm);

		vv.getRenderContext().setVertexShapeTransformer(
				new FactorGraphShapeTransformer());

		vv.getRenderContext().setVertexLabelTransformer(
				new FactorGraphVertexLabeler());

		scrollPane = new GraphZoomScrollPane(vv);

		add(scrollPane, BorderLayout.CENTER);

		add(createControlBar(vv, gm), BorderLayout.SOUTH);

	}

	private Component createControlBar(
			final VisualizationViewer<FactorGraphNode, FactorGraphEdge> vv,
			DefaultModalGraphMouse<FactorGraphNode, FactorGraphEdge> gm) {
		final ScalingControl scaler = new CrossoverScalingControl();
		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});

		JPanel controls = new JPanel();
		controls.add(plus);
		controls.add(minus);
		JComboBox modeBox = gm.getModeComboBox();
		controls.add(modeBox);

		return controls;
	}

	private class FactorGraphVertexLabeler implements
			Transformer<FactorGraphNode, String> {

		@Override
		public String transform(FactorGraphNode input) {
			return input.getName();
		}
	}

	private void createDialog(FactorGraphNode v) {
		if (v instanceof DiscreteVariable<?>) {
			DiscreteVariable<?> variable = (DiscreteVariable<?>) v;
			VariableDialog variableDialog = new VariableDialog(variable);
			variableDialog.setVisible(true);
		} 
		if( v instanceof DiscreteInternalFunction) {
			DiscreteInternalFunction function = (DiscreteInternalFunction) v;
			FunctionDialog functionDialog = new FunctionDialog(function);
			functionDialog.setVisible(true);
		}

	}

	private class FactorGraphShapeTransformer implements
			Transformer<FactorGraphNode, Shape> {

		@Override
		public Shape transform(FactorGraphNode input) {
			if (input instanceof Function) {
				return new Rectangle(-sizeShape / 2, -sizeShape / 2, sizeShape,
						sizeShape);
			} else if (input instanceof Variable<?, ?>) {
				return new Ellipse2D.Float(-sizeShape / 2, -sizeShape / 2,
						sizeShape, sizeShape);
			}
			throw new IllegalArgumentException("Unexpected class "
					+ input.getClass());
		}
	}
}
