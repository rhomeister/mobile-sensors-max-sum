package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import maxSumController.continuous.LineSegment;
import maxSumController.continuous.PieceWiseLinearFunction;

import org.apache.commons.lang.Validate;

import util.CombinationGenerator;

public class MultiVariatePieceWiseLinearFunction implements
		MultiVariateFunction {

	private static final boolean PERFORM_SIMPLIFICATION = false;

	private NCube domain;

	private SimplexPartitioning partitioning;

	private NDimensionalPointMap<Double> values = new NDimensionalPointMap<Double>();

	private int dimensions;

	public MultiVariatePieceWiseLinearFunction(NCube domain) {
		this(new SimplexPartitioning(domain));
	}

	/**
	 * @param point
	 * @return
	 */
	protected List<Set<NSimplex>> getAdjacentParallelSimplexGroups(
			NDimensionalPoint point) {
		List<NSimplex> adjacentSimplices = partitioning
				.getAdjacentSimplices(point);

		List<Set<NSimplex>> groups = new ArrayList<Set<NSimplex>>();

		for (NSimplex simplex : adjacentSimplices) {
			boolean assigned = false;
			NSimplex valueSimplex = simplex.createValueSimplex(values);

			for (Set<NSimplex> group : groups) {
				NSimplex valueSimplex2 = group.iterator().next()
						.createValueSimplex(values);
				if (valueSimplex.isParallel(valueSimplex2)) {
					group.add(simplex);
					assigned = true;
					break;
				}
			}

			if (!assigned) {
				Set<NSimplex> simplexGroup = new HashSet<NSimplex>();
				groups.add(simplexGroup);
				simplexGroup.add(simplex);
			}
		}

		List<Set<NSimplex>> result = new ArrayList<Set<NSimplex>>();

		for (Set<NSimplex> group : groups) {
			result.addAll(partitionIntoConnectedSimplices(group));
		}

		return result;
	}

	/**
	 * Splits the list of simplices passed as parameter into groups of simplices
	 * that are either connected by a shared edge, or are connected through a
	 * path of simplices
	 * 
	 * @param simplices
	 * @return
	 */
	protected List<Set<NSimplex>> partitionIntoConnectedSimplices(
			Collection<NSimplex> simplices) {

		List<Set<NSimplex>> result = new ArrayList<Set<NSimplex>>();

		for (NSimplex simplex : simplices) {
			List<Set<NSimplex>> assignedTo = new ArrayList<Set<NSimplex>>();

			for (Set<NSimplex> group : result) {
				for (NSimplex simplex2 : group) {
					if (simplex.hasSharedSurface(simplex2)) {
						group.add(simplex);
						assignedTo.add(group);
						break;
					}
				}
			}

			Set<NSimplex> simplexGroup = new HashSet<NSimplex>();
			result.add(simplexGroup);
			if (assignedTo.isEmpty()) {
				simplexGroup.add(simplex);
			} else {
				result.removeAll(assignedTo);

				for (Set<NSimplex> assignedGroup : assignedTo) {
					simplexGroup.addAll(assignedGroup);
				}
			}
		}

		return result;
	}

	protected MultiVariatePieceWiseLinearFunction(
			SimplexPartitioning partitioning) {
		this.partitioning = partitioning;
		this.domain = partitioning.getDomain();
		this.dimensions = domain.getDimensionCount();
	}

	public void setValue(NDimensionalPoint coordinate, double value) {
		values.put(coordinate, value);
	}

	public void addPartition(NSimplex partition) {
		Validate.isTrue(partition.getDimensionCount() == dimensions);

		for (NDimensionalPoint point : partition.getPoints()) {
			if (!values.containsKey(point))
				setValue(point, 0.0);
		}

		partitioning.add(partition);
	}

	public double evaluate(NDimensionalPoint point) {
		Validate
				.isTrue(point.getDimensionCount() == domain.getDimensionCount());
		// Validate.isTrue(domain.contains(point));

		// improve performance of evaluate method
		if (values.containsKey(point)) {
			return values.get(point);
		}

		NSimplex simplex = partitioning.getEnclosingSimplex(point);

		if (simplex == null)
			return Double.NaN;
		else {
			double[] coefficients = simplex.getCoefficients(point);
			double result = 0.0;

			// linearly combine values based on coefficients
			for (int i = 0; i < coefficients.length; i++) {
				Double double1 = values.get(simplex.getPoints()[i]);

				result += coefficients[i] * double1;
			}

			return result;
		}
	}

	public int getDimensionCount() {
		return dimensions;
	}

	public NCube getDomain() {
		return domain;
	}

	private MultiVariatePieceWiseLinearFunction merge(
			MultiVariatePieceWiseLinearFunction function, Operation operation) {
		Validate.isTrue(function.getDomain().equals(domain), "Expected: "
				+ domain + ", actual" + function.domain);

		if (function.getPartitioning().getSimplices().size() > getPartitioning()
				.getSimplices().size())
			return function.merge(this, operation);

		// create union of domains
		SimplexPartitioning mergedPartitioning = new SimplexPartitioning(domain);

		MultiVariatePieceWiseLinearFunction result = new MultiVariatePieceWiseLinearFunction(
				mergedPartitioning);

		for (NSimplex simplex : partitioning.getSimplices())
			mergedPartitioning.add(simplex);

		setFunctionValues(operation, function, result);

		for (NSimplex simplex : function.partitioning.getSimplices()) {
			for (NDimensionalLine edge : simplex.getEdges()) {
				for (NDimensionalPoint intersection : mergedPartitioning
						.getIntersections(edge)) {
					result.splitIfNecessary(intersection, operation, this,
							function);
				}
			}
		}

		// for (NDimensionalPoint point : function.partitioning
		// .getDefiningCoordinates()) {
		// result.splitIfNecessary(point, operation, this, function);
		// }

		return result;
	}

	protected void splitIfNecessary(NDimensionalPoint point,
			Operation operation, MultiVariatePieceWiseLinearFunction operand1,
			MultiVariatePieceWiseLinearFunction operand2) {
		double value = operation.evaluate(operand1, operand2, point);
		double difference = Math.abs(evaluate(point) - value);

		if (difference > 1e-8) {
			getPartitioning().split(point);
			setValue(point, value);
		}
	}

	public MultiVariatePieceWiseLinearFunction addUnivariateFunction(
			PieceWiseLinearFunction function, int variableIndex) {

		Validate.isTrue(getDimensionCount() == 2);

		int otherVariableIndex = variableIndex == 1 ? 0 : 1;

		SimplexPartitioning mergedPartitioning = new SimplexPartitioning(domain);

		MultiVariatePieceWiseLinearFunction result = new MultiVariatePieceWiseLinearFunction(
				mergedPartitioning);

		for (NSimplex simplex : partitioning.getSimplices())
			mergedPartitioning.add(simplex);

		for (double x : function.getIntervalEndpoints()) {
			NDimensionalPoint start = new NDimensionalPoint(2);
			start.setCoordinate(variableIndex, x);
			start.setCoordinate(otherVariableIndex, domain
					.getDomainStart(otherVariableIndex));
			NDimensionalPoint end = new NDimensionalPoint(2);
			end.setCoordinate(variableIndex, x);
			start.setCoordinate(otherVariableIndex, domain
					.getDomainEnd(otherVariableIndex));

			mergedPartitioning.split(new NDimensionalLine(start, end));
		}

		for (NDimensionalPoint point : mergedPartitioning
				.getDefiningCoordinates()) {
			double variable = point.getCoordinates()[variableIndex];

			result.setValue(point, function.evaluate(variable)
					+ evaluate(point));
		}

		return result;

	}

	public NDimensionalPointSet getIntersections(
			MultiVariatePieceWiseLinearFunction function) {

		Set<NSimplex> valueSimplexes1 = partitioning.getValueSimplexes(values);
		Set<NSimplex> valueSimplexes2 = function.partitioning
				.getValueSimplexes(function.values);

		NDimensionalPointSet result = new NDimensionalPointSet();

		for (NSimplex simplex1 : valueSimplexes1) {
			for (NSimplex simplex2 : valueSimplexes2) {
				result.addAll(simplex1.getIntersections(simplex2));
			}
		}

		return result;
	}

	public SimplexPartitioning getPartitioning() {
		return partitioning;
	}

	public List<LineSegment> project(int dimensionIndex) {
		List<LineSegment> result = new ArrayList<LineSegment>();

		for (NSimplex simplex : partitioning.getSimplices()) {
			for (NDimensionalLine edge : simplex.getEdges()) {
				double x1 = edge.getStart().getCoordinates()[dimensionIndex];
				double y1 = values.get(edge.getStart());
				double x2 = edge.getEnd().getCoordinates()[dimensionIndex];
				double y2 = values.get(edge.getEnd());

				if (x1 != x2)
					result.add(new LineSegment(x1, y1, x2, y2));
			}
		}

		return result;
	}

	public Map<NDimensionalPoint, Double> getValues() {
		return values;
	}

	public double evaluate(double... coordinates) {
		return evaluate(new NDimensionalPoint(coordinates));
	}

	public MultiVariatePieceWiseLinearFunction subtract(
			MultiVariatePieceWiseLinearFunction function) {
		return add(function.multiply(-1.0));
	}

	public MultiVariatePieceWiseLinearFunction add(
			MultiVariatePieceWiseLinearFunction function) {
		// System.out.println("Summing functions with " + getSimplexCount() + "
		// "
		// + function.getSimplexCount() + " simplices");

		MultiVariatePieceWiseLinearFunction result = merge(function,
				Operation.ADD);
		setFunctionValues(Operation.ADD, function, result);
		result.simplify();

		MultiVariatePieceWiseLinearFunctionUtilities.verifyAddition(this,
				function, result);

		return result;
	}

	public MultiVariatePieceWiseLinearFunction max(
			MultiVariatePieceWiseLinearFunction function) {
		return maxOrMin(function, Operation.MAXIMUM);
	}

	public MultiVariatePieceWiseLinearFunction min(
			MultiVariatePieceWiseLinearFunction function) {
		return maxOrMin(function, Operation.MINIMUM);
	}

	private MultiVariatePieceWiseLinearFunction maxOrMin(
			MultiVariatePieceWiseLinearFunction function, Operation operation) {

		// System.out.println("Max/Min between functions with "
		// + getSimplexCount() + " " + function.getSimplexCount()
		// + " simplices");

		Validate.isTrue(operation == Operation.MAXIMUM
				|| operation == Operation.MINIMUM);

		MultiVariatePieceWiseLinearFunction result = merge(function, operation);

		Set<NSimplex> valueSimplexes1 = partitioning.getValueSimplexes(values);
		Set<NSimplex> valueSimplexes2 = function.partitioning
				.getValueSimplexes(function.values);

		for (NSimplex simplex1 : valueSimplexes1) {
			for (NSimplex simplex2 : valueSimplexes2) {

				Collection<NDimensionalPoint> intersections = simplex1
						.getIntersections(simplex2);

				result.splitLines(intersections, operation, this, function);
			}
		}

		setFunctionValues(operation, function, result);

		result.simplify();

		return result;
	}

	private void splitLines(Collection<NDimensionalPoint> intersections,
			Operation operation, MultiVariatePieceWiseLinearFunction function1,
			MultiVariatePieceWiseLinearFunction function2) {
		for (NDimensionalPoint point1 : intersections) {
			splitIfNecessary(point1, operation, function1, function2);

			for (NDimensionalPoint point2 : intersections) {
				if (!point1.equals(point2)) {

					NDimensionalLine line = new NDimensionalLine(point1, point2);

					for (NDimensionalPoint intersection : getPartitioning()
							.getIntersections(line)) {
						splitIfNecessary(intersection, operation, function1,
								function2);
					}
				}
			}
		}
	}

	private void setFunctionValues(Operation operation,
			MultiVariatePieceWiseLinearFunction otherOperand,
			MultiVariatePieceWiseLinearFunction result) {
		for (NDimensionalPoint point : result.getPartitioning()
				.getDefiningCoordinates()) {
			result.setValue(point, operation.evaluate(evaluate(point),
					otherOperand.evaluate(point)));
		}
	}

	public double getValue(NDimensionalPoint point) {
		Validate.isTrue(values.containsKey(point), point + " " + values);
		return values.get(point);
	}

	private MultiVariatePieceWiseLinearFunction scalarOperation(
			Operation operation, double scalar) {
		MultiVariatePieceWiseLinearFunction result = new MultiVariatePieceWiseLinearFunction(
				partitioning.copy());

		for (NDimensionalPoint point : partitioning.getDefiningCoordinates()) {
			result.setValue(point, operation.evaluate(getValue(point), scalar));
		}

		return result;
	}

	public void simplify() {
		simplify(PERFORM_SIMPLIFICATION);
	}

	public void simplify(boolean performSimplification) {
		if (!performSimplification)
			return;

		boolean changed = true;

		do {
			changed = false;
			NDimensionalPointSet definingCoordinates = new NDimensionalPointSet(
					partitioning.getDefiningCoordinates());

			for (NDimensionalPoint point : definingCoordinates) {
				if (attemptMerge(point)) {
					changed = true;
					break;
				}
			}
		} while (changed);
	}

	private boolean attemptMerge(NDimensionalPoint point) {
		List<Set<NSimplex>> groups = getAdjacentParallelSimplexGroups(point);

		for (Set<NSimplex> simplexGroup : groups) {
			CombinationGenerator<NSimplex> generator = new CombinationGenerator<NSimplex>(
					simplexGroup);

			// check all combinations of simplices in this group and try to
			// merge them
			for (int subsetSize = simplexGroup.size(); subsetSize >= 2; subsetSize--) {
				Collection<List<NSimplex>> combinations = generator
						.getCombinations(subsetSize);

				for (List<NSimplex> collection : combinations) {
					if (!isClique(collection)) {
						continue;
					}

					NSimplex merge = NSimplex.merge(collection, point);

					if (merge == null) {
						continue;
					}

					if (merge.getBoundaryNumber(point) == subsetSize - 1) {
						mergePartitions(collection, point);
						return true;
					}
				}
			}
		}

		return false;
	}

	public void mergePartitions(Collection<NSimplex> set,
			NDimensionalPoint point) {
		getPartitioning().removeAll(set);
		addPartition(NSimplex.merge(set, point));
	}

	/**
	 * Tests whether all simplices share an edge with all other simplices in the
	 * set
	 * 
	 * @param collection
	 * @return
	 */
	private boolean isClique(List<NSimplex> list) {
		for (int i = 0; i < list.size(); i++) {
			NSimplex simplex1 = list.get(i);
			for (int j = i + 1; j < list.size(); j++) {
				NSimplex simplex2 = list.get(j);
				if (!simplex1.hasSharedSurface(simplex2)) {
					return false;
				}
			}
		}
		return true;
	}

	public MultiVariatePieceWiseLinearFunction multiply(double scalar) {
		return scalarOperation(Operation.MULTIPLY, scalar);
	}

	public MultiVariatePieceWiseLinearFunction subtract(double scalar) {
		return scalarOperation(Operation.ADD, -scalar);
	}

	public MultiVariatePieceWiseLinearFunction add(double value) {
		return scalarOperation(Operation.ADD, value);
	}

	public int getSimplexCount() {
		return partitioning.getSimplices().size();
	}

	public MultiVariatePieceWiseLinearFunction min(double value) {
		return min(MultiVariatePieceWiseLinearFunctionUtilities
				.createConstantFunction(domain, value));
	}

	public MultiVariatePieceWiseLinearFunction max(double value) {
		return max(MultiVariatePieceWiseLinearFunctionUtilities
				.createConstantFunction(domain, value));
	}
}