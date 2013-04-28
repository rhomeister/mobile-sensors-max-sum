package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

public class NDimensionalPointMap<T> implements Map<NDimensionalPoint, T> {

	private NDimensionalPointSet keys = new NDimensionalPointSet();

	private List<T> values = new ArrayList<T>();

	public NDimensionalPointMap(NDimensionalPointMap<T> adjacencyMap) {
		keys.addAll(adjacencyMap.keys);
		values.addAll(adjacencyMap.values);
	}

	public NDimensionalPointMap() {

	}

	public void clear() {
		keys.clear();
		values.clear();
	}

	public boolean containsKey(Object key) {
		return keys.contains(key);
	}

	public boolean containsValue(Object value) {
		return values.contains(value);
	}

	public Set<java.util.Map.Entry<NDimensionalPoint, T>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public T get(Object key) {
		int i = keys.indexOf(key);

		if (i == -1)
			return null;
		else
			return values.get(i);
	}

	public boolean isEmpty() {
		return keys.isEmpty();
	}

	public Set<NDimensionalPoint> keySet() {
		return new HashSet<NDimensionalPoint>(keys);
	}

	public T put(NDimensionalPoint key, T value) {
		if (keys.contains(key)) {
			int indexOf = keys.indexOf(key);
			Validate.isTrue(indexOf >= 0);
			T oldValue = get(key);

			values.set(indexOf, value);

			return oldValue;
		} else {
			keys.add(key);
			values.add(value);

			Validate.isTrue(keys.size() == values.size());
			return null;
		}
	}

	public void putAll(Map<? extends NDimensionalPoint, ? extends T> m) {
		for (NDimensionalPoint point : m.keySet()) {
			put(point, m.get(point));
		}
	}

	public T remove(Object key) {
		if (keys.contains(key)) {
			int indexOf = keys.indexOf(key);
			Validate.isTrue(indexOf >= 0);
			T oldValue = get(key);

			values.remove(indexOf);
			keys.remove(indexOf);

			return oldValue;
		} else {
			return null;
		}
	}

	public int size() {
		return keys.size();
	}

	public Collection<T> values() {
		return values;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("{");

		for (int i = 0; i < keys.size(); i++) {
			buffer.append(keys.get(i) + "=" + values.get(i) + ", ");
		}

		buffer = buffer.delete(buffer.length() - 2, buffer.length());
		buffer.append("}");

		return buffer.toString();
	}

	public NDimensionalPointSet getPointSet() {
		return keys;
	}
}
