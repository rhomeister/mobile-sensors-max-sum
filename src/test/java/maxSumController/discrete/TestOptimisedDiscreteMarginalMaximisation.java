package maxSumController.discrete;

import junit.framework.TestCase;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ConflictFunction;

public class TestOptimisedDiscreteMarginalMaximisation extends TestCase {

	OptimisedDiscreteMarginalMaximisation marginalMaximisation;

	@Override
	protected void setUp() throws Exception {
		marginalMaximisation = new OptimisedDiscreteMarginalMaximisation();
	}

	public void testHasChanged() throws Exception {
		ConflictFunction conflictFunction = new ConflictFunction("bla");
		conflictFunction
				.addVariableDependency(new DiscreteInternalVariable<Color>("a",
						new ThreeColorsDomain()));

		marginalMaximisation.setVariables(conflictFunction
				.getVariableDependencies());

		assertFalse(marginalMaximisation.precomputed);

		marginalMaximisation.precomputed = true;

		marginalMaximisation.setVariables(conflictFunction
				.getVariableDependencies());

		assertTrue(marginalMaximisation.precomputed);

		conflictFunction
				.addVariableDependency(new DiscreteInternalVariable<Color>("b",
						new ThreeColorsDomain()));
		marginalMaximisation.setVariables(conflictFunction
				.getVariableDependencies());

		assertFalse(marginalMaximisation.precomputed);
	}

}
