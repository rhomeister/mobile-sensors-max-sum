package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import maxSumController.discrete.bb.TripleStore;

public class HashTripleStore<S1, S2, S3> implements TripleStore<S1, S2, S3> {

	private Map<S1, Map<S2, S3>> values = new HashMap<S1, Map<S2, S3>>();

	public S3 get(Object key1, Object key2) {
		Map<S2, S3> key1Values = values.get(key1);

		if (key1Values != null)
			return key1Values.get(key2);

		return null;
	}

	public S3 put(S1 key1, S2 key2, S3 value) {
		Map<S2, S3> key1Values = values.get(key1);

		if (key1Values == null) {
			key1Values = new HashMap<S2, S3>();
			values.put(key1, key1Values);
		}

		return key1Values.put(key2, value);
	}

	@Override
	public Set<S1> getKeySet1() {
		return values.keySet();
	}

	@Override
	public Map<S2, S3> get(S1 key) {
		return values.get(key);
	}

	@Override
	public String toString() {
		String result = "[";

		for (S1 key1 : values.keySet()) {
			for (S2 key2 : values.get(key1).keySet()) {
				result += "(" + key1 + ", " + key2 + ")="
						+ values.get(key1).get(key2) + ", ";
			}
		}

		result = result.substring(0, result.length() - 2);
		result += "]";

		return result;
	}

	public Collection<S3> getValuesForFirstKey(S1 key1) {
		return values.get(key1).values();

	};

	public Collection<S3> getValuesForSecondKey(S2 key2) {
		Collection<S3> result = new ArrayList<S3>();

		for (S1 key1 : values.keySet()) {
			result.addAll(values.get(key1).values());
		}

		return result;
	};

	public static void main(String[] args) {
		HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();

		hashMap.put(24, 3);
		hashMap.put(4, 1);

		System.out.println(hashMap);
	}
}
