package maxSumController.visualization;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import maxSumController.discrete.DiscreteVariable;

public class VariableDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VariableDialog(DiscreteVariable<?> variable) {
		setTitle("Variable " + variable.getName());

		String[] columnNames = { "Property", "Value" };

		String[][] data = {
				{ "Name", variable.getName() },
				{ "Domain", variable.getDomain().toString() },
				{ "Function Dependencies",
						variable.getDependencies().toString() } };

		JTable table = new JTable(data, columnNames);
		table.setEnabled(false);

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);

		getContentPane().add(scrollPane);
		pack();
	}

}
