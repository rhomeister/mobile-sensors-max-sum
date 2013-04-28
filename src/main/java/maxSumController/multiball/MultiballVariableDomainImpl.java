package maxSumController.multiball;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableDomainImpl;

public class MultiballVariableDomainImpl<T extends MultiballVariableState> extends
		DiscreteVariableDomainImpl<T> {
	
	public static double lambda = 1;

	public MultiballVariableDomainImpl(){
	}
	public MultiballVariableDomainImpl(Set<T> states) {
		super(states);
	}

	
	@Override
	public DiscreteMarginalValues<T> createPreference() {
		
		Map<T, Double> pref = new HashMap<T, Double>();
		for (T vs : getStates()) {
			pref.put(vs, SCALER*Math.cos(vs.getValue()/lambda));
		}
		DiscreteMarginalValues<T> zero = new DiscreteMarginalValues<T>(pref);
		return zero;
	}
}
