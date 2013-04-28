package maxSumController.continuous.linear;

public enum Operation {
	ADD {
		public double evaluate(double d1, double d2) {
			return d1 + d2;
		}
	},
	SUBTRACT {
		public double evaluate(double d1, double d2) {
			return d1 - d2;
		}
	},
	MULTIPLY {
		public double evaluate(double d1, double d2) {
			return d1 * d2;
		}
	},
	MINIMUM {
		public double evaluate(double d1, double d2) {
			return Math.min(d1, d2);
		}
	},
	MAXIMUM {
		public double evaluate(double d1, double d2) {
			return Math.max(d1, d2);
		}
	};

	public abstract double evaluate(double d1, double d2);

	public double evaluate(MultiVariatePieceWiseLinearFunction function1,
			MultiVariatePieceWiseLinearFunction function2,
			NDimensionalPoint point) {
		return evaluate(function1.evaluate(point), function2.evaluate(point));
	}

	public MultiVariateFunction evaluate(
			final MultiVariatePieceWiseLinearFunction function1,
			final MultiVariatePieceWiseLinearFunction function2) {
		return new MultiVariateFunction() {

			public int getDimensionCount() {
				return function1.getDimensionCount();
			}

			public double evaluate(NDimensionalPoint point) {
				return Operation.this.evaluate(function1, function2, point);
			}

		};
	}
}
