package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

public class CombinationGenerator<T> {

	private Map<Collection<T>, Map<Integer, Collection<List<T>>>> cachedResults = new HashMap<Collection<T>, Map<Integer, Collection<List<T>>>>();

	private Collection<T> collection;

	public CombinationGenerator(Collection<T> collection) {
		Validate.notNull(collection);
		this.collection = collection;
	}

	public Collection<List<T>> getCombinations(int size) {
		Validate.isTrue(size > 0);
		Validate.isTrue(size <= collection.size());

		return getCombinationsHelper(collection, size);
	}

	private Collection<List<T>> getCombinationsHelper(Collection<T> collection,
			int size) {
		List<List<T>> result = new ArrayList<List<T>>();

		Collection<List<T>> cachedResult = getCachedResult(collection, size);

		if (cachedResult != null) {
			return cachedResult;
		} else if (size == 1) {
			ArrayList<T> list;
			for (T t : collection) {
				result.add(list = new ArrayList<T>());
				list.add(t);
			}
		} else {
			List<T> list = new ArrayList<T>(collection);

			for (int i = 0; i < list.size() - size + 1; i++) {
				Collection<List<T>> subCombinations = getCombinationsHelper(
						list.subList(i + 1, list.size()), size - 1);

				for (Collection<T> subCombination : subCombinations) {
					List<T> combination = new ArrayList<T>();
					combination.add(list.get(i));
					combination.addAll(subCombination);
					result.add(combination);
				}
			}
		}

		addCachedResult(collection, size, result);

		return result;
	}

	private Collection<List<T>> getCachedResult(Collection<T> collection,
			int size) {
		if (cachedResults.containsKey(collection)
				&& cachedResults.get(collection).containsKey(size))
			return cachedResults.get(collection).get(size);
		else
			return null;
	}

	private void addCachedResult(Collection<T> collection, int size,
			Collection<List<T>> result) {
		if (!cachedResults.containsKey(collection)) {
			cachedResults.put(collection,
					new HashMap<Integer, Collection<List<T>>>());
		}

		cachedResults.get(collection).put(size, result);
	}
}
