package maxSumController.multiball.GP;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

import Jama.Matrix;

import uk.ac.soton.ecs.gp4j.bmc.BasicPrior;
import uk.ac.soton.ecs.gp4j.bmc.GaussianProcessMixture;
import uk.ac.soton.ecs.gp4j.bmc.GaussianProcessRegressionBMC;
import uk.ac.soton.ecs.gp4j.gp.GaussianProcess;
import uk.ac.soton.ecs.gp4j.gp.GaussianProcessPrediction;
import uk.ac.soton.ecs.gp4j.gp.covariancefunctions.CovarianceFunction;
import uk.ac.soton.ecs.gp4j.gp.covariancefunctions.SquaredExponentialCovarianceFunction;

import maxSumController.multiball.math.InterpolationFunction;

public class GPInterpolation implements InterpolationFunction {

	protected CovarianceFunction function;
	protected int[] priorSampleCounts;
	protected double[] priorMeans;
	protected double[] priorStandardDevs;
	protected GaussianProcessMixture predictor;
	protected double currentMax;
	
	public GPInterpolation(CovarianceFunction function,
			int[] priorSampleCounts, double[] priorMeans,
			double[] priorStandardDevs){
		this.function = function;
		this.priorSampleCounts = priorSampleCounts;
		this.priorMeans = priorMeans;
		this.priorStandardDevs = priorStandardDevs;
	}
	
	@Override
	public InterpolationFunction copy() {
		return new GPInterpolation(function, priorSampleCounts, priorMeans,
				priorStandardDevs);
	}

	@Override
	public double evaluate(double state) {
		Matrix testX = new Matrix(1,1, state);

		return predictor.calculatePrediction(testX).getMean().get(0, 0);
	}

	@Override
	public double evaluateDerivative(double state) {
		// Do not use
		throw new NotImplementedException("GP Interpolation does not return derivatives.");
	}

	@Override
	public double evaluateSecondDerivative(double state) {
		// Do not use
		throw new NotImplementedException("GP Interpolation does not return derivatives.");
	}

	@Override
	public void setDataPoints(double[] stateValues, double[] functionValues) {
		Matrix trainX = new Matrix(stateValues.length, 1);
		Matrix trainY = new Matrix(stateValues.length, 1);
		currentMax = Double.NEGATIVE_INFINITY;
		for(int i=0; i<stateValues.length; i++){
			trainX.set(i, 0, stateValues[i]);
			trainY.set(i, 0, functionValues[i]);	
			currentMax = Math.max(currentMax, functionValues[i]);
		}
		GaussianProcessRegressionBMC regression = new GaussianProcessRegressionBMC();
		regression.setCovarianceFunction(function);

		BasicPrior[] priors = new BasicPrior[priorSampleCounts.length];
		for(int i=0; i<priorSampleCounts.length; i++){
			priors[i] = new BasicPrior(priorSampleCounts[i], priorMeans[i],
					priorStandardDevs[i]);
		}

		regression.setPriors(priors);
		
		predictor = regression.calculateRegression(
				trainX, trainY);

	}

}
