package maxSumController.multiball.GP;

import java.util.List;

import maxSumController.multiball.math.InterpolationFunction;
import maxSumController.multiball.math.NormalDist;
import Jama.Matrix;
import uk.ac.soton.ecs.gp4j.gp.GaussianProcess;
import uk.ac.soton.ecs.gp4j.gp.GaussianProcessPrediction;
import uk.ac.soton.ecs.gp4j.gp.covariancefunctions.CovarianceFunction;

public class GPMaximisationGainInterpolation extends GPInterpolation {

	public GPMaximisationGainInterpolation(CovarianceFunction function,
			int[] priorSampleCounts, double[] priorMeans,
			double[] priorStandardDevs) {
		super(function, priorSampleCounts, priorMeans, priorStandardDevs);
	}
	
	@Override
	public InterpolationFunction copy() {
		return new GPMaximisationGainInterpolation(function, priorSampleCounts, priorMeans,
				priorStandardDevs);
	}

	@Override
	public double evaluate(double state) {
		Matrix testX = new Matrix(1,1, state);
		
		List<GaussianProcess> processes = predictor.getGaussianProcesses();
		
		double result = 0;
		
		for(GaussianProcess process : processes){
			GaussianProcessPrediction prediction = 
				process.calculatePrediction(testX);
			double mean = prediction.getMean().get(0, 0);
			double sd = prediction.getStandardDeviation().get(0, 0);
			result += predictor.getWeight(process) * (currentMax
					+ ((mean-currentMax)*NormalDist.phi(currentMax, mean, sd))
					- (sd * NormalDist.N(currentMax, mean, sd)));
		}

		return result;
	}



}
