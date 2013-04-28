package maxSumController.multiball.GP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import uk.ac.soton.ecs.gp4j.bmc.BasicPrior;
import uk.ac.soton.ecs.gp4j.bmc.GaussianProcessMixture;
import uk.ac.soton.ecs.gp4j.bmc.GaussianProcessRegressionBMC;
import uk.ac.soton.ecs.gp4j.gp.GaussianProcess;
import uk.ac.soton.ecs.gp4j.gp.GaussianProcessPrediction;
import uk.ac.soton.ecs.gp4j.gp.covariancefunctions.SquaredExponentialCovarianceFunction;
import Jama.Matrix;

import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.multiball.MultiballVariableState;
import maxSumController.multiball.math.NormalDist;
import maxSumController.multiball.nonlinearoptimiser.linesearch.TrueMarginalFunctionEstimation;

public class HybridGPMSMarginalEstimate implements
		TrueMarginalFunctionEstimation {
	private int testSamples;
	private int maxIterationMemory;
	private double boundDist;
	private int[] priorSampleCounts;
	private double[] priorMeans;
	private double[] priorStandardDevs;
	private Map<MultiballVariableState, Double> currentDomainTVals;
	private double[] xVals;
	private double[] yVals;
	private double[] tVals;
	private MarginalFunctionMemory memory;
	private GaussianProcessMixture predictor;
	private int iterationSkips;
	private int iteration;
	private double bestSoFar;
	private int worstCurrentIndex;
	private double worstCurrentVal;
	private MultiballVariableState[] stateArray;
	private NoiseVariance noise;
	
	public HybridGPMSMarginalEstimate(int maxIterationMemory, int[] priorSampleCounts, double[] priorMeans,
			double[] priorStandardDevs, double boundErrorProbability, int testSamples,
			NoiseVariance noise) {
		this(maxIterationMemory, priorSampleCounts, priorMeans,
				priorStandardDevs, boundErrorProbability, testSamples,
				noise, 1);
	}

	public HybridGPMSMarginalEstimate(int maxIterationMemory, int[] priorSampleCounts, double[] priorMeans,
				double[] priorStandardDevs, double boundErrorProbability, int testSamples,
				NoiseVariance noise, int iterationSkips) {
		
		iteration = -1;
		this.iterationSkips = iterationSkips;

		this.testSamples = testSamples;
		this.maxIterationMemory = maxIterationMemory;
		this.priorSampleCounts = priorSampleCounts;
		this.priorMeans = priorMeans;
		this.priorStandardDevs = priorStandardDevs;
		this.noise = noise;
				
		boundDist = NormalDist.xnormi(boundErrorProbability);
		currentDomainTVals = new HashMap<MultiballVariableState, Double>();
		memory = new MarginalFunctionMemory(maxIterationMemory);
		xVals = null;
		yVals = null;
		tVals = null;
		predictor = null;
		stateArray = null;
	}
	
	
	
	
	@Override
	public TrueMarginalFunctionEstimation copy() {
		return new HybridGPMSMarginalEstimate(maxIterationMemory, priorSampleCounts, 
				priorMeans, priorStandardDevs, NormalDist.normp(boundDist), testSamples, noise,
				iterationSkips);
	}

	@Override
	public double[] getSearchDivisions() {
		return xVals;
	}

	@Override
	public MultiballVariableState stateToLose() {

		if((iteration%iterationSkips)!= 0) return null;
		 // decide if worth dropping a state or not

		if(bestSoFar > worstCurrentVal) {

			memory.setLastValuable();
			
			currentDomainTVals.remove(stateArray[worstCurrentIndex]);
			return stateArray[worstCurrentIndex];
		} else {
			return null;
		}
	}
	
	private double getAndIncrementTVal(MultiballVariableState state){
		Double result = currentDomainTVals.get(state);
		double t = (result == null)?(-1):result.doubleValue();
		
		currentDomainTVals.put(state, new Double(t + 1));
		return t;
	}

	@Override
	public void updateEstimate(DiscreteMarginalValues<MultiballVariableState> observedValues) {
		iteration++;
		iteration %= iterationSkips;
		// turn variableDomain into an array of X values
		
		Set<MultiballVariableState> states = observedValues.getValues().keySet();
		xVals = new double[states.size()];
		stateArray = new MultiballVariableState[xVals.length];
		
		int i=0;
		for(MultiballVariableState state : states) {
			xVals[i] = state.getValue();
			stateArray[i] = state;
			i++;
		}
		 // turn marginalFunction into an array of Y values and t values
		
		yVals = new double[xVals.length];
		tVals = new double[xVals.length];

		for(i=0; i<xVals.length; i++){
			yVals[i] = observedValues.getValue(stateArray[i]);
			tVals[i] = getAndIncrementTVal(stateArray[i]);
		}

		memory.add(xVals, yVals, tVals);

		if((iteration%iterationSkips)!= 0) return;
		
		Matrix transform = HybridGPMSTransform.generate(memory.iterationNum);
		
		Matrix trainY = transform.times(memory.Y);

		// create Gaussian process Bayesian Markov chain to estimate worth of setting
		// states to new values
		
		GaussianProcessRegressionBMC regression = new GaussianProcessRegressionBMC();
		regression.setCovarianceFunction(new HybridGPMSCovarianceFunction(
				SquaredExponentialCovarianceFunction.getInstance(),
				transform, noise));

		BasicPrior[] priors = new BasicPrior[priorMeans.length];
		for(i=0; i<priorMeans.length; i++){
			priors[i] = new BasicPrior(priorSampleCounts[i], priorMeans[i],
					priorStandardDevs[i]);
		}

		regression.setPriors(priors);
		
		predictor = regression.calculateRegression(
				memory.X, trainY);
		
		worstCurrentVal = Double.POSITIVE_INFINITY;
		
		for(i=0; i<xVals.length; i++) {
			double worth = evaluate(xVals[i], tVals[i]+1);
			if(worth < worstCurrentVal){
				worstCurrentVal = worth;
				worstCurrentIndex = i;
			}
		}

		bestSoFar = worstCurrentVal;

	}

	/*
	public void printMemory() {
		System.out.print("X ");
		memory.X.transpose().print(1, 2);
		System.out.print("Y ");
		memory.Y.transpose().print(1, 2);
		for(int i=0; i<memory.iterationNum.length;i++ ) 
			System.out.print(memory.iterationNum[i] + " ");
		System.out.println();
	}*/
	
	@Override
	public double evaluate(double possibleNewState){
		return evaluate(possibleNewState, 0);
	}
	
	private double evaluate(double possibleNewState, double possibleNewT){
		if((iteration%iterationSkips)!= 0) return 0;

		Matrix testX = new Matrix(1, 2, 0);
		testX.set(0, 0, possibleNewState);
		testX.set(0, 1, possibleNewT);

		List<GaussianProcess> processes = predictor.getGaussianProcesses();

		double result = 0;

		for(GaussianProcess process : processes){
			double[] logHyper = process.getLogHyperParameters();
			double weight = predictor.getWeight(process);

			double currentBound = Double.NEGATIVE_INFINITY;
	
			for(int i=0; i<xVals.length; i++){
				currentBound = Math.max(currentBound,
						xVals[i] + (boundDist *
						Math.sqrt(noise.calculateVariance(logHyper, tVals[i]))));
			}
			
			GaussianProcessPrediction prediction = process
					.calculatePrediction(testX);
		
			double mean = prediction.getMean().get(0,0);
			double standardDev = prediction.getStandardDeviation().get(0,0);
			double noiseStandardDev = 
				Math.sqrt(noise.calculateVariance(logHyper, possibleNewT));
	

			/*
			 *  The "bound" derived from a measurement is computed as that measurement 
			 *  minus a constant times the standard deviation of the noise modelled at 
			 *  that location.
			 *  This has a normal distribution, and so we can use the same expected gain
			 *  as before. 
			 */
			
			result += weight * expectedGain(currentBound, mean + (boundDist * noiseStandardDev),
					standardDev);
		}
		
		bestSoFar = Math.max(bestSoFar, result);
		return result;
	}

	private double expectedGain(double currentBound, double mean, double sd){
	return (currentBound 
			+ ((mean-currentBound)*NormalDist.phi(currentBound, mean, sd))
			- (sd * NormalDist.N(currentBound, mean, sd)));
	}

	/*
	Old plan, generate a bunch of multivariate normal samples and then 
	compare what the expected difference is between the best lower bounds produced
	by these samples.
	  
	Problem: didn't work very well, too slow and not fair test.
	 	
	 	private double evaluate(double possibleNewState, double possibleNewT){
		int n = xVals.length;
		Matrix testX = new Matrix(n + 1, 2, 0);
		for(int i=0; i<n; i++) {
			testX.set(i, 0, xVals[i]);
			testX.set(i, 1, tVals[i] + 1);
		}
		testX.set(n, 0, possibleNewState);
		testX.set(n, 1, possibleNewT);

		List<GaussianProcess> processes = predictor.getGaussianProcesses();

		double[] predictorLowerBound = new double[n+1];
		for(int i=0; i<=n; i++) predictorLowerBound[i] = 0;

		
		for(GaussianProcess process : processes){
			double[] logHyper = process.getLogHyperParameters();
			double weight = predictor.getWeight(process);

			double[] noiseStandardDev = new double[n+1];
	
			for(int i=0; i<=n; i++){
				
				noiseStandardDev[i] = noise.calculateVariance(logHyper, testX.get(i, 1));
			}
	
			
			GaussianProcessPrediction prediction = process
					.calculatePrediction(testX);
			 
	
			Matrix samples = NormalDist.genSamples(n+1, testSamples, rand);
			Matrix standardDev = prediction.getStandardDeviation();
			Matrix mean = prediction.getMean();
			double[] arraySampleStandardDev = new double[n+1];
			double[] arrayMean = new double[n+1];
			for(int i=0; i<=n; i++) arraySampleStandardDev[i] = standardDev.get(i, 0);
			for(int i=0; i<=n; i++) arrayMean[i] = mean.get(i, 0);
	
			double[] processLowerBound = new double[n+1];
	
			for(int i=0; i<=n; i++) {
				double sampleLowerBound;
				processLowerBound[i] = 0;
				for(int k=0; k<testSamples; k++){
					sampleLowerBound = Double.NEGATIVE_INFINITY;
					for(int j = ((i==0)?1:0); j<=n; j++) {
						if(j==i) continue;
						double singleSampleBound = arrayMean[j] + (arraySampleStandardDev[j]*samples.get(j,k)) + 
							(boundDist * noiseStandardDev[j]);
						if (singleSampleBound > sampleLowerBound) sampleLowerBound =
							singleSampleBound;
					}
					processLowerBound[i] += sampleLowerBound;
				}
				processLowerBound[i] /= testSamples;
			}
			
			for(int i=0; i<=n; i++) predictorLowerBound[i] += 
				weight * processLowerBound[i];
		}
		double result = predictorLowerBound[0];
		int losingIndex = 0;
		
		for(int i=1; i<n; i++) {
			if(result > predictorLowerBound[i]) {
				result = predictorLowerBound[i];
				losingIndex = i;
			}
		}
		
		
		result -= predictorLowerBound[n];
		
		if((bestSoFar == null) || (result > bestSoFar.doubleValue())){
			bestSoFar = new Double(result);
			bestSoFarLosingIndex = losingIndex;
		}

		return result;
	}

	 */
	
	
	
	
}
