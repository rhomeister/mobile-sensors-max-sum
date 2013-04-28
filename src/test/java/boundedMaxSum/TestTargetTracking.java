package boundedMaxSum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;


public class TestTargetTracking extends TestCase{
	private BoundedMaxSumTargetTrackingExperiment exp;
	private TargetTrackingGenerator ttg;
	private Map<TrackingObject, Set<TrackingSensor>> targetToSensorsMap;

	@Override
	protected void setUp() throws Exception {
		exp = new BoundedMaxSumTargetTrackingExperiment();

	}
	
	
	public void testBuildFactorGraph() throws Exception {
		
		ttg = new TargetTrackingGenerator(1,2);		
		targetToSensorsMap = new HashMap<TrackingObject, Set<TrackingSensor>>();
		TrackingObject target1 = new TrackingObject(1,1.,1.);
		TrackingSensor sensor1 = new TrackingSensor(2,0.,1.,2.);
		TrackingSensor sensor2 = new TrackingSensor(3,0.,2.,2.);
		Set<TrackingObject> targets = new HashSet<TrackingObject>();
		Set<TrackingSensor> sensors = new HashSet<TrackingSensor>();
		targets.add(target1);
		sensors.add(sensor1);
		sensors.add(sensor2);
		ttg.setConfiguration(targets,sensors);

		System.out.println("target To Sensors Map "+ttg.getTargetToSensorsMap());
		System.out.println("sensor To Targets Map "+ttg.getSensorToTargetsMap());
		
		exp.buildFactorGraphs(ttg);
		System.out.println("controller "+exp.controller);
		System.out.println("treecontroller "+exp.treeController);

		
	}
	
	public void testbuildFactorGraph2() throws Exception {
		ttg = new TargetTrackingGenerator(1,2);		
		targetToSensorsMap = new HashMap<TrackingObject, Set<TrackingSensor>>();
		TrackingObject target1 = new TrackingObject(1,1.,0.);
		TrackingObject target2 = new TrackingObject(2,.5,1.);
		TrackingObject target3 = new TrackingObject(3,1.5,1.);

		TrackingSensor sensor4 = new TrackingSensor(4,0.,0.,1.3);
		TrackingSensor sensor5 = new TrackingSensor(5,2.,0.,1.3);
		TrackingSensor sensor6 = new TrackingSensor(6,1.,2.,1.3);
		Set<TrackingObject> targets = new HashSet<TrackingObject>();
		Set<TrackingSensor> sensors = new HashSet<TrackingSensor>();
		targets.add(target1);
		targets.add(target2);
		targets.add(target3);
		
		
		sensors.add(sensor4);
		sensors.add(sensor5);
		sensors.add(sensor6);
		
		ttg.setConfiguration(targets,sensors);

		System.out.println("target To Sensors Map "+ttg.getTargetToSensorsMap());
		System.out.println("sensor To Targets Map "+ttg.getSensorToTargetsMap());
		
		exp.buildFactorGraphs(ttg);
		System.out.println("controller "+exp.controller);
		System.out.println("treecontroller "+exp.treeController);
		
	}


	public void testTreeFormation() throws Exception {
		ttg = new TargetTrackingGenerator(1,2);		
		targetToSensorsMap = new HashMap<TrackingObject, Set<TrackingSensor>>();
		TrackingObject target1 = new TrackingObject(1,1.,0.);
		TrackingObject target2 = new TrackingObject(2,.5,1.);
		TrackingObject target3 = new TrackingObject(3,1.5,1.);

		TrackingSensor sensor4 = new TrackingSensor(4,0.,0.,1.3);
		TrackingSensor sensor5 = new TrackingSensor(5,2.,0.,1.3);
		TrackingSensor sensor6 = new TrackingSensor(6,1.,2.,1.3);
		Set<TrackingObject> targets = new HashSet<TrackingObject>();
		Set<TrackingSensor> sensors = new HashSet<TrackingSensor>();
		targets.add(target1);
		targets.add(target2);
		targets.add(target3);
		
		
		sensors.add(sensor4);
		sensors.add(sensor5);
		sensors.add(sensor6);
		
		ttg.setConfiguration(targets,sensors);

		exp.buildFactorGraphs(ttg);
		
		exp.runBoundedMaxSum();
		
		
	}
	

	public void testExperiment() throws Exception {
		ttg = new TargetTrackingGenerator(1,2);		
		targetToSensorsMap = new HashMap<TrackingObject, Set<TrackingSensor>>();
		TrackingObject target1 = new TrackingObject(1,1.,0.);
		TrackingObject target2 = new TrackingObject(2,.5,1.);
		TrackingObject target3 = new TrackingObject(3,1.5,1.);

		TrackingSensor sensor4 = new TrackingSensor(4,0.,0.,1.3);
		TrackingSensor sensor5 = new TrackingSensor(5,2.,0.,1.3);
		TrackingSensor sensor6 = new TrackingSensor(6,1.,2.,1.3);
		Set<TrackingObject> targets = new HashSet<TrackingObject>();
		Set<TrackingSensor> sensors = new HashSet<TrackingSensor>();
		targets.add(target1);
		targets.add(target2);
		targets.add(target3);
		
		
		sensors.add(sensor4);
		sensors.add(sensor5);
		sensors.add(sensor6);
		
		ttg.setConfiguration(targets,sensors);

		BoundedMaxSumTTResult result = exp.runTTExperimentInstance(ttg);
		System.out.println(result.toCSV());
	}
	
	
}
