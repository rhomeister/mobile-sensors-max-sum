package maxSumController.discrete.bb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface TripleStore<S1, S2, S3> {

	S3 get(Object key1, Object key2);

	S3 put(S1 key1, S2 key2, S3 value);

	Collection<S3> getValuesForFirstKey(S1 key1);

	Collection<S3> getValuesForSecondKey(S2 key2);

	Set<S1> getKeySet1();

	Map<S2, S3> get(S1 key);

}
