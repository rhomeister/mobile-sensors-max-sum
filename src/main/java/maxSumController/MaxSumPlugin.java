package maxSumController;

import java.util.HashMap;
import java.util.Map;

public class MaxSumPlugin implements AlgorithmPlugin {

	private boolean enabled = true;
	private AbstractMaxSumController<?, ?, ?> controller;
	private boolean firstCall = true;
	private Map<Variable<?, ?>, VariableDomain<?>> variableDomains = new HashMap<Variable<?, ?>, VariableDomain<?>>();

	public MaxSumPlugin(AbstractMaxSumController<?, ?, ?> controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		if (enabled) {
			if (firstCall) {
				saveVariableDomains();
				firstCall = false;
			} else {
				checkVariableDomains();
			}

			controller.getIterationPolicy().enqueueNewOutgoingMessages();
		}
	}

	private void checkVariableDomains() {
		for (Variable<?, ?> variable : controller.getAllVariables()) {
			VariableDomain<?> variableDomain = variableDomains.get(variable);

			if (!variable.getDomain().equals(variableDomain)) {
				throw new IllegalArgumentException(
						"Variable domain has changed during execution of max-sum. "
								+ "Other plugins might be operating at the same time or equals() for the domain has not been implemented. "
								+ "Old domain " + variableDomain
								+ ". New domain " + variable.getDomain());
			}
		}
	}

	private void saveVariableDomains() {
		for (Variable<?, ?> variable : controller.getAllVariables()) {
			variableDomains.put(variable, variable.getDomain());
		}
	}

	public void setEnabled(boolean b) {
		enabled = b;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getIterationCount() {
		return controller.getIterationPolicy().getIterationCount();
	}
}
