package maxSumController.discrete.bb;

import java.util.Arrays;

public class BBBoundsCache {
	protected double[] bounds;
	private boolean initialised;

	// private PartialJointVariableState[] cachedState;

	// private StackTraceElement[][] cachedStackTrace;

	public void initialise(int maxIndexCode) {
		bounds = new double[maxIndexCode];
		Arrays.fill(bounds, Double.NaN);
		initialised = true;
		// cachedState = new PartialJointVariableState[maxIndexCode];
		// cachedStackTrace = new StackTraceElement[maxIndexCode][];
	}

	public boolean contains(PartialJointVariableState state) {
		return !Double.isNaN(bounds[state.getIndexCode()]);
	}

	public double get(PartialJointVariableState state) {
		return bounds[state.getIndexCode()];
	}

	// public void checkValue(PartialJointVariableState state, double bound) {
	// if (contains(state) && get(state) != bound) {
	// System.out.println("BEGIN EXCEPTION BLOCK");
	// System.out.println("offered state " + state);
	// System.out.println("cached state "
	// + cachedState[state.getIndexCode()]);
	// System.out.println(state.getIndexCode());
	// System.out.println("cached value " + get(state));
	// System.out.println("offered value " + bound);
	//
	// System.out.println("previous stacktrace ");
	// for (StackTraceElement el : cachedStackTrace[state.getIndexCode()]) {
	// System.out.println(el);
	// }
	//
	// System.out.println("END EXCEPTION BLOCK");
	//
	// throw new IllegalArgumentException();
	// }
	// }

	public void put(PartialJointVariableState state, double bound) {
		// cachedStackTrace[state.getIndexCode()] = new Throwable()
		// .getStackTrace();

		bounds[state.getIndexCode()] = bound;
		// cachedState[state.getIndexCode()] = state;
	}

	public boolean isInitialised() {
		return initialised;
	}

	// public void debug(PartialJointVariableState state) {
	// System.out.println("BEGIN BOUNDS CACHE DEBUG");
	// System.out.println("offered state " + state);
	// System.out.println("cached state " + cachedState[state.getIndexCode()]);
	// System.out.println(state.getIndexCode());
	// System.out.println("cached value " + get(state));
	//
	// System.out.println("previous stacktrace ");
	// for (StackTraceElement el : cachedStackTrace[state.getIndexCode()]) {
	// System.out.println(el);
	// }
	// System.out.println("END BOUNDS CACHE DEBUG");
	// }
}
