package boundedMaxSum;

import junit.framework.TestCase;

public class testTargetTrackingGen extends TestCase{

	private TargetTrackingGenerator ttg;
	

	@Override
	protected void setUp() throws Exception {
		ttg = new TargetTrackingGenerator(10,10);
	}
	
	
	public void testGenerateConfig() throws Exception {
		ttg.generateConfiguration(3,1.0);
		System.out.println("sensors = "+ttg.getSensorToTargetsMap().keySet());
		System.out.println("targets = "+ttg.getTargetToSensorsMap().keySet());
		System.out.println(" sensor to target map = "+ttg.getSensorToTargetsMap());
		System.out.println(" target to sensors map = "+ttg.getTargetToSensorsMap());		
	
		BoundedMaxSumTargetTrackingExperiment exp = new BoundedMaxSumTargetTrackingExperiment();
	
		BoundedMaxSumTTResult result = exp.runTTExperimentInstance(ttg);
		System.out.println(result.toCSV());
	}
}
