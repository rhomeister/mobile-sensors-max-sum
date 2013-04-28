package maxSumController;

import java.util.Map;

import maxSumController.discrete.DiscreteVariable;

public interface Maximiser<V extends Variable<?, ?>,S extends VariableState> {

	Map<V , S> getOptimalState();
	
}
