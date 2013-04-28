package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

public class SimplexPartitioning implements NDimensionalObject {

	private NCube domain;

	private List<NSimplex> partitions = new ArrayList<NSimplex>();

	private NDimensionalPointMap<List<NSimplex>> adjacencyMap = new NDimensionalPointMap<List<NSimplex>>();

	public SimplexPartitioning(NCube domain) {
		this.domain = domain;
	}

	public void add(NSimplex partition) {
		for (NDimensionalPoint point : partition.getPoints()) {
			Validate.isTrue(domain.contains(point), "Point is outside domain "
					+ point + " " + domain);
		}

		partitions.add(partition);

		for (NDimensionalPoint point : partition.getPoints()) {
			if (!adjacencyMap.containsKey(point))
				adjacencyMap.put(point, new ArrayList<NSimplex>());

			adjacencyMap.get(point).add(partition);
		}
	}

	public List<NSimplex> getAdjacentSimplices(NDimensionalPoint point) {
		if (!adjacencyMap.containsKey(point))
			return Collections.unmodifiableList(new ArrayList<NSimplex>());
		else
			return Collections.unmodifiableList(adjacencyMap.get(point));
	}

	public int getDimensionCount() {
		return domain.getDimensionCount();
	}

	public List<NSimplex> getSimplices() {
		return partitions;
	}

	public NSimplex getEnclosingSimplex(NDimensionalPoint point) {
		List<NSimplex> simplices = getEnclosingSimplices(point);

		if (simplices.isEmpty())
			return null;
		else
			return simplices.get(0);
	}

	public List<NSimplex> getEnclosingSimplices(NDimensionalPoint point) {
		// TODO optimise
		List<NSimplex> result = new ArrayList<NSimplex>();

		for (NSimplex simplex : partitions) {
			if (simplex.getBoundingBox().contains(point)
					&& simplex.contains(point))
				result.add(simplex);
		}

		return result;
	}

	public Set<NDimensionalPoint> getIntersections(NDimensionalLine line) {
		// TODO optimise
		Set<NDimensionalPoint> result = new HashSet<NDimensionalPoint>();

		for (NSimplex simplex : partitions) {
			result.addAll(simplex.getIntersections(line));
		}

		return result;
	}

	public SimplexPartitioning union(SimplexPartitioning partitioning) {
		SimplexPartitioning result = new SimplexPartitioning(domain);

		for (NSimplex simplex : partitions)
			result.add(simplex);

		for (NDimensionalPoint point : partitioning.getDefiningCoordinates()) {
			result.split(point);
		}

		for (NSimplex simplex : partitioning.getSimplices()) {
			for (NDimensionalLine edge : simplex.getEdges()) {
				result.split(edge);
			}
		}

		return result;
	}

	public void addAll(List<NSimplex> partitions) {
		for (NSimplex simplex : partitions) {
			add(simplex);
		}
	}

	public void split(NDimensionalLine edge) {
		for (NDimensionalPoint intersection : getIntersections(edge)) {
			split(intersection);
		}
	}

	public void split(NDimensionalPoint point) {
		if (adjacencyMap.containsKey(point))
			return;

		Collection<NSimplex> scheduledForRemoval = new ArrayList<NSimplex>();
		Collection<NSimplex> scheduledForAddition = new ArrayList<NSimplex>();

		for (NSimplex enclosingSimplex : getEnclosingSimplices(point)) {
			scheduledForRemoval.add(enclosingSimplex);

			Set<NSimplex> simplices = enclosingSimplex.split(point);

			for (NSimplex simplex : simplices) {
				scheduledForAddition.add(simplex);
			}
		}
		for (NSimplex simplex : scheduledForRemoval)
			removeAdjacency(simplex);

		partitions.removeAll(scheduledForRemoval);

		for (NSimplex simplex : scheduledForAddition)
			add(simplex);
	}

	private void removeAdjacency(NSimplex simplex) {
		for (NDimensionalPoint point : simplex.getPoints()) {
			adjacencyMap.get(point).remove(simplex);

			if (adjacencyMap.get(point).isEmpty()) {
				adjacencyMap.remove(point);
			}
		}
	}

	public NDimensionalPointSet getDefiningCoordinates() {
		return adjacencyMap.getPointSet();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (NSimplex simplex : partitions) {
			builder.append(simplex + "\n");
		}

		return builder.toString();
	}

	public NCube getDomain() {
		return domain;
	}

	public Set<NSimplex> getValueSimplexes(Map<NDimensionalPoint, Double> values) {
		Set<NSimplex> result = new HashSet<NSimplex>();
		for (NSimplex simplex : getSimplices()) {
			result.add(simplex.createValueSimplex(values));
		}

		return result;
	}

	public SimplexPartitioning copy() {
		SimplexPartitioning copy = new SimplexPartitioning(domain);

		copy.partitions = new ArrayList<NSimplex>(partitions);
		copy.adjacencyMap = new NDimensionalPointMap<List<NSimplex>>(
				adjacencyMap);

		return copy;
	}

	public void removeAll(Collection<NSimplex> set) {
		for (NSimplex simplex : set) {
			removeAdjacency(simplex);
			partitions.remove(simplex);
		}
	}
}
