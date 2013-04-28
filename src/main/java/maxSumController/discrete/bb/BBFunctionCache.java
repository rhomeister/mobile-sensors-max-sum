package maxSumController.discrete.bb;

public class BBFunctionCache {

	private BBBoundsCache lowerBounds = new BBBoundsCache();

	private BBBoundsCache upperBounds = new BBBoundsCache();

	private boolean initialized;

	private BBDiscreteInternalFunction function;

	private boolean cachingEnabled = true;

	private static int misses;

	private static int calls;

	public BBFunctionCache(BBDiscreteInternalFunction function) {
		this.function = function;
	}

	public void initialise() {
		if (!initialized) {

			int maxIndexCode = PartialJointVariableState
					.getMaxIndexCode(function.getVariableExpansionOrder());

			lowerBounds.initialise(maxIndexCode);
			upperBounds.initialise(maxIndexCode);

			if (!cachingEnabled)
				System.out.println("WARNING: Caching disabled");

			initialized = true;
		}
	}

	public boolean containsLowerBound(PartialJointVariableState state) {
		initialise();

		if (!cachingEnabled) {
			return false;
		}

		return lowerBounds.contains(state);
	}

	public boolean containsUpperBound(PartialJointVariableState state) {
		initialise();

		if (!cachingEnabled) {
			return false;
		}

		return upperBounds.contains(state);
	}

	public double getLowerBound(PartialJointVariableState state) {
		calls++;
		return lowerBounds.get(state);
	}

	public double getUpperBound(PartialJointVariableState state) {
		calls++;
		return upperBounds.get(state);
	}

	public void putLowerBound(PartialJointVariableState state, double bound) {
		// if (!cachingEnabled) {
		// lowerBounds.checkValue(state, bound);
		// }

		lowerBounds.put(state, bound);
		misses++;
	}

	public void putUpperBound(PartialJointVariableState state, double bound) {
		// if (!cachingEnabled) {
		// upperBounds.checkValue(state, bound);
		// }

		upperBounds.put(state, bound);
		misses++;
	}

	public void setCachingEnabled(boolean b) {
		this.cachingEnabled = b;
	}

	public void debug(PartialJointVariableState state) {
		// lowerBounds.debug(state);
		// upperBounds.debug(state);
		//
	}

	public static int getCalls() {
		return calls;
	}

	public static int getMisses() {
		return misses;
	}

	public static void resetStatistics() {
		calls = 0;
		misses = 0;
	}

}
