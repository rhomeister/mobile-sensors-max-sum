package maxSumController.visualization;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

public class FunctionDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FunctionDialog(DiscreteInternalFunction function) {
		setLayout(new BorderLayout());

		setTitle("Function " + function.getName());

		String[] columnNames = { "Property", "Value" };

		String[][] data = {
				{ "Name", function.getName() },
				{ "Variable Dependencies",
						function.getDependencies().toString() } };

		JTable table = new JTable(data, columnNames);
		table.setEnabled(false);

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);

		getContentPane().add(scrollPane, BorderLayout.NORTH);

		if (function.getDependencies().size() <= 2) {
			getContentPane().add(createFunctionTable(function),
					BorderLayout.SOUTH);
		}

		pack();
	}

	private Component createFunctionTable(DiscreteInternalFunction function) {
		if (function.getVariableDependencies().size() == 2) {
			Iterator<Variable<?, ?>> iterator = function
					.getVariableDependencies().iterator();
			DiscreteVariable<?> variable1 = (DiscreteVariable<?>) iterator
					.next();
			DiscreteVariable<?> variable2 = (DiscreteVariable<?>) iterator
					.next();

			List<DiscreteVariableState> states1 = new ArrayList(variable1
					.getDomain().getStates());
			List<DiscreteVariableState> states2 = new ArrayList(variable2
					.getDomain().getStates());

			String[][] data = new String[states1.size() + 1][states2.size() + 1];
			String[] columnNames = new String[states2.size() + 1];

			data[0][0] = variable1.getName() + "    \\    "
					+ variable2.getName();
			columnNames[0] = "";

			for (int i = 0; i < states2.size(); i++) {
				data[0][i + 1] = states2.get(i).toString();
				columnNames[i + 1] = "";
			}

			int row = 1;

			for (DiscreteVariableState state1 : states1) {
				data[row][0] = state1.toString();

				int col = 1;

				for (DiscreteVariableState state2 : states2) {

					Map<DiscreteVariable<?>, DiscreteVariableState> values = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
					values.put(variable1, state1);
					values.put(variable2, state2);

					data[row][col] = ""
							+ function.evaluate(new VariableJointState(values));
					col++;
				}
				row++;
			}

			return new JTable(data, columnNames);
		}

		return null;
	}
}
