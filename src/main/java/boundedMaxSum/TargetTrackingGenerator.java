package boundedMaxSum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TargetTrackingGenerator {

	private int numberOfTargets;
	private int numberOfSensors;
	private Map<TrackingObject, Set<TrackingSensor>> targetToSensorsMap = new HashMap<TrackingObject, Set<TrackingSensor>>();
	private Map<TrackingSensor, Set<TrackingObject>> sensorToTargetsMap = new HashMap<TrackingSensor, Set<TrackingObject>>();
	private boolean debug = true;

	public TargetTrackingGenerator(int targets, int sensors) {
		numberOfSensors = sensors;
		numberOfTargets = targets;
		//generateConfiguration(3,1.0);
	}

	public void setConfiguration(Map<TrackingObject, Set<TrackingSensor>> map){
		this.targetToSensorsMap = map;
		if (debug) {
			System.out.println("set config "+targetToSensorsMap);
		}
		this.sensorToTargetsMap = generateSensorToTargetMap(targetToSensorsMap);
		numberOfSensors = sensorToTargetsMap.keySet().size();
		numberOfTargets = targetToSensorsMap.keySet().size();
	}
	
	
	private Map<TrackingSensor, Set<TrackingObject>> generateSensorToTargetMap(
			Map<TrackingObject, Set<TrackingSensor>> tsm) {
		Map<TrackingSensor, Set<TrackingObject>> res = new HashMap<TrackingSensor, Set<TrackingObject>>();
		for (TrackingObject target : tsm.keySet()) {
			Set<TrackingSensor> sensors = tsm.get(target);
			for (TrackingSensor trackingSensor : sensors) {
//				res.keySet().add(trackingSensor);
				if (res.get(trackingSensor)!=null){
					res.get(trackingSensor).add(target);
				} else {
					Set<TrackingObject> targets = new HashSet<TrackingObject>();
					targets.add(target);
					res.put(trackingSensor, targets);
				}	
			}
		}
		return res;
	}

	public void generateConfiguration(int numCol, double rangeVisDist) {
		// create targets, sensors, targetToSensorsMap and generate the other
		targetToSensorsMap.clear();
		sensorToTargetsMap.clear();
		
		Set<TrackingObject> targets = new HashSet<TrackingObject>();
		Set<TrackingSensor> sensors = new HashSet<TrackingSensor>();
		int numColumn = numCol;
		double interSensDist = 1.0;
		double vis = rangeVisDist*interSensDist;
		for (int i=0;i<numberOfSensors;i++){
			double x = ((i%numColumn)*interSensDist);
			double y = ((int)(i/numColumn)*interSensDist);
			TrackingSensor currentSensor = new TrackingSensor(i,x,y,vis);
			sensors.add(currentSensor);
		}
		for (int i=0;i<numberOfTargets;i++){
			double x = ((i%numColumn)*interSensDist) + Math.random()*interSensDist;
			double y = ((int)(i/numColumn)*interSensDist) + Math.random()*interSensDist;
			TrackingObject currentTarget = new TrackingObject(i+numberOfSensors,x,y);
			if (debug) {
				System.out.println("adding target" + currentTarget);
			}
			targets.add(currentTarget);
		}	
		if (debug){
			System.out.println("targets "+targets);
			System.out.println("sensors "+sensors);			
		}
		setConfiguration(targets, sensors);
	}

	public int getNumberOfSensors() {
		return numberOfTargets;
	}

	public int getNumberOfTargets() {
		return numberOfSensors;
	}

	public Map<TrackingSensor, Set<TrackingObject>> getSensorToTargetsMap() {
		return sensorToTargetsMap;
	}

	public Map<TrackingObject, Set<TrackingSensor>> getTargetToSensorsMap() {
		return targetToSensorsMap;
	}

	public Set<TrackingSensor> getObservingSensors(TrackingObject to) {
		return targetToSensorsMap.get(to);
	}

	public void setConfiguration(Set<TrackingObject> targets,
			Set<TrackingSensor> sensors) {
		Map<TrackingObject, Set<TrackingSensor>> map = generateTargetToSensorsMap(targets,sensors);
		this.setConfiguration(map);
	}

	private Map<TrackingObject, Set<TrackingSensor>> generateTargetToSensorsMap(
			Set<TrackingObject> targets, Set<TrackingSensor> sensors) {
		Map<TrackingObject, Set<TrackingSensor>> res = new HashMap<TrackingObject, Set<TrackingSensor>>();
		for (TrackingObject target : targets) {
			Set<TrackingSensor> possibleSensors = new HashSet<TrackingSensor>();
			for (TrackingSensor sensor : sensors) {
				if (sensor.getDistance(target)<=sensor.getVisibilityRange()){
					possibleSensors.add(sensor);
				}
			}
			res.put(target, possibleSensors);
		}
		if (debug) {
			System.out.println(res);
		}
		
		return res;
	}

}
