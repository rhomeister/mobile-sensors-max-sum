package maxSumController.discrete;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.MarginalValues;

import org.apache.commons.lang.Validate;

public class DiscreteMarginalValues<T extends DiscreteVariableState> implements
		MarginalValues<T> {
	protected Map<T, Double> values;

	public DiscreteMarginalValues(Map<T, Double> values) {
		super();
		this.values = values;

		validateNoNaN();

	}

	private void validateNoNaN() {
		for (Double value : values.values()) {
			Validate.isTrue(!value.isNaN());
			Validate.isTrue(!value.isInfinite());
		}
	}

	public DiscreteMarginalValues() {
		values = new HashMap<T, Double>();
	}

	@Override
	public String toString() {
		DecimalFormat dc = new DecimalFormat("0.00000");

		String returnString = "";

		if (values.keySet().isEmpty())
			returnString += "[empty]";

		for (DiscreteVariableState vs : values.keySet()) {
			returnString += "[" + vs + ": " + dc.format(values.get(vs)) + "]";
		}

		return returnString;
	}

	public Map<T, Double> getValues() {
		return values;
	}

	public Double getValue(DiscreteVariableState state) {
		return values.get(state);
	}

	public void normalise() {
		double sumOfValues = getSumOfValues();

		for (T vs : values.keySet()) {
			values.put(vs, values.get(vs) - sumOfValues
					/ (double) values.size());
		}

		validateNoNaN();
	}

	public double getSumOfValues() {
		double sum = 0.0;

		for (DiscreteVariableState vs : values.keySet()) {
			sum += values.get(vs);
		}

		return sum;
	}

	public DiscreteMarginalValues<T> add(MarginalValues<T> marginalValues) {
		DiscreteMarginalValues<T> otherFunction = (DiscreteMarginalValues<T>) marginalValues;

		Map<T, Double> resultValues = new HashMap<T, Double>();
		Map<T, Double> otherValues = otherFunction.getValues();

		Set<T> keyUnion = new HashSet<T>(values.keySet());
		keyUnion.addAll(otherValues.keySet());

		for (T vs : keyUnion) {
			Double myValue = values.get(vs);
			myValue = (myValue == null) ? 0 : myValue;
			Double otherValue = otherValues.get(vs);
			otherValue = (otherValue == null) ? 0 : otherValue;

			if (vs == null) {
				System.out.println();
			}
			
			resultValues.put(vs, myValue + otherValue);
		}

		return new DiscreteMarginalValues<T>(resultValues);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof DiscreteMarginalValues) {
			DiscreteMarginalValues<?> otherFunction = (DiscreteMarginalValues<?>) other;
			return otherFunction.getValues().equals(values);
		}

		return false;
	}

	public T argMax() {
		double maximumValue = Double.NEGATIVE_INFINITY;

		T result = null;
		for (T vs : values.keySet()) {
			// System.out.println("Marginalvalue["+i+"] = "+ marginalValues[i]);
			if (values.get(vs) >= maximumValue) {
				maximumValue = values.get(vs);
				result = vs;
			}
		}

		if (result == null) {
			System.out.println(values);
		}

		Validate.isTrue(values.keySet().isEmpty() || result != null);

		return result;
	}

	@Override
	public int getSize() {
		return getValues().size();
	}

	public double min() {
		return Collections.min(values.values());
	}

	public double max() {
		return Collections.max(values.values());
	}

	public void put(DiscreteVariableState state, double d) {
		values.put((T) state, d);

		validateNoNaN();
	}
}
