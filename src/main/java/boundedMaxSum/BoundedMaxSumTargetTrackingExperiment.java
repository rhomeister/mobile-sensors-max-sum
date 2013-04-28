package boundedMaxSum;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.OptimisedDiscreteMarginalMaximisation;
import boundedMaxSum.treeformation.NodeType;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormationController;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent;
import boundedMaxSum.treeformation.oldghs.LinkEdge;

public class BoundedMaxSumTargetTrackingExperiment extends
		BoundedMaxSumLinkExperiments {

	protected int numberOfSensors;

	protected int numberOfTargets;

	public static void main(String[] args) {
		BoundedMaxSumTargetTrackingExperiment exp = new BoundedMaxSumTargetTrackingExperiment();

		int targets = 4;
		int sensors = 4;
		int colNum = 3;
		double visDistRange = 2.0;
		// TODO delete it when doing exp.
		REPETITIONS = 1;

		for (int i = 8; i < 9; i++) {
			targets = i;
			sensors = i;
			String filename = "tracking";
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(filename
						+ "-" + sensors + "-" + targets + ".dat"));
				for (int repetitions = 0; repetitions < REPETITIONS; repetitions++) {
					BoundedMaxSumTTResult res = exp
							.runExperimentsTargetTracking(targets, sensors,
									colNum, visDistRange);
					if (!Double.isNaN(res.getActualUtility())) {
						System.out.println(res.toCSV());
						out.write(res.toCSV() + "\n");
					}
				}
				out.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	protected BoundedMaxSumTTResult runExperimentsTargetTracking(int targets,
			int sensors, int colNum, double visDistRange) {
		TargetTrackingGenerator ttg = new TargetTrackingGenerator(targets,
				sensors);
		ttg.generateConfiguration(colNum, visDistRange);

		return runTTExperimentInstance(ttg);

	}

	protected BoundedMaxSumTTResult runTTExperimentInstance(
			TargetTrackingGenerator ttg) {
		BoundedMaxSumTTResult result = new BoundedMaxSumTTResult();
		numberOfSensors = ttg.getNumberOfSensors();
		numberOfTargets = ttg.getNumberOfTargets();
		try {
			buildFactorGraphs(ttg);

			if (debug) {
				System.out.println("Factor graph built");
			}

			result.setNumberOfSensors(numberOfSensors);
			result.setNumberOfTargets(numberOfTargets);
			double dep = 0.;
			for (DiscreteInternalVariable var : controller.initVariables) {
				dep += var.getFunctionDependencies().size();
			}
			result.setAverageConnectionsPerNode(dep / (double) numberOfSensors);

			computeUtility(numberOfSensors, result);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			result.setActualUtility(Double.NaN);
		}

		return result;
	}

	protected void buildFactorGraphs(TargetTrackingGenerator ttg) {

		if (discardedFunctions != null) {
			discardedFunctions.clear();
		}

		if (debug) {
			System.out.println("TargetToSensors Map = "
					+ ttg.getTargetToSensorsMap());
			System.out.println("SensorToTargets Map = "
					+ ttg.getSensorToTargetsMap());
		}

		treeController = new GHSTreeFormationController();
		treeController.addListener(this);

		controller = new BoundedDiscreteMaxSumController("agent",
				new OptimisedDiscreteMarginalMaximisation());

		// domain = createVariableDomain(numberOfColours);

		Map<TrackingSensor, Set<TrackingObject>> sensorToTargetsMap = ttg
				.getSensorToTargetsMap();
		Map<TrackingObject, Set<TrackingSensor>> targetToSensorsMap = ttg
				.getTargetToSensorsMap();

		// create variables
		for (TrackingSensor tss : sensorToTargetsMap.keySet()) {

			DiscreteVariableDomain<TrackingObject> vardomain = TrackingDomainFactory
					.buildTrackingDomain(sensorToTargetsMap.get(tss));
			TrackingVariable internalVariable = new TrackingVariable("v"
					+ tss.getId(), vardomain, tss);

			controller.addInternalVariable(internalVariable);
			// GHSTreeFormingAgent node = new GHSTreeFormingAgent(nodeId, null);
			GHSTreeFormingAgent node = new GHSTreeFormingAgent(tss.getId(),
					(GHSTreeFormationController) treeController,
					internalVariable.getName());
			node.setType(NodeType.VARIABLE);
			treeController.addAgent(node);
		}

		// create functions
		for (TrackingObject to : targetToSensorsMap.keySet()) {

			GHSTreeFormingAgent fAgent = new GHSTreeFormingAgent(to.getId(),
					(GHSTreeFormationController) treeController, "f"
							+ to.getId());
			fAgent.setType(NodeType.FUNCTION);
			treeController.addAgent(fAgent);

			Set<TrackingSensor> sensors = ttg.getObservingSensors(to);
			Set<DiscreteInternalVariable<TrackingObject>> variables = new HashSet<DiscreteInternalVariable<TrackingObject>>();

			for (TrackingSensor trackingSensor : sensors) {
				DiscreteInternalVariable<TrackingObject> trackVar = (DiscreteInternalVariable<TrackingObject>) controller
						.getInitVariable("v" + trackingSensor.getId());
				variables.add(trackVar);
			}

			LinkBoundedInternalFunction func = new TargetTrackingPayoffFunction(
					"f" + to.getId(), to, variables);
			func.setOwningAgentIdentifier(agent);

			for (DiscreteInternalVariable discreteInternalVariable : variables) {
				func.addVariableDependency(discreteInternalVariable);
				discreteInternalVariable.addFunctionDependency(func);
			}

			for (TrackingSensor ts : sensors) {
				DiscreteInternalVariable variable1 = controller
						.getInternalVariable("v" + ts.getId());
				GHSTreeFormingAgent agent1 = treeController.getAgent("v"
						+ ts.getId());
				agent1.addEdge(new LinkEdge(fAgent, agent1, func, variable1));
				fAgent.addEdge(new LinkEdge(agent1, fAgent, func, variable1));
			}

			controller.addInternalFunction(func);

		}

		if (debug) {
			System.out.println("treecontroller " + treeController);
			System.out.println("loopy controller " + controller);
		}

	}

}
