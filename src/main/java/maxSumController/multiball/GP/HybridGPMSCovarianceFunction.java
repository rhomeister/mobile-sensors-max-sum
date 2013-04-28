package maxSumController.multiball.GP;


import uk.ac.soton.ecs.gp4j.gp.covariancefunctions.CovarianceFunction;
import Jama.Matrix;

public class HybridGPMSCovarianceFunction implements CovarianceFunction {

	// Co-variance function for hybrid Gaussian process max sum optimiser.
	//
	// The trainX values need to have a dimension added. The very
	// last co-ordinate should indicate what iteration of the algorithm
	// this measurement was taken (so that values are compared to each other
	// correctly, and normalisation is not a problem).
	//
	// The initial loghyper parameters are for the NoiseVariance function.
	//
	// Other loghyper parameters are for the underlying covariance function
	// for setting smoothness on the resulting posterior.
	//
	// This co-variance function automatically adds damped noise.
	//
	// When processing, the inputs trainY should be transformed so as to avoid
	// being affected by measurement normalisation. The same transform should be
	// given to this function using setTransform.
	
	private CovarianceFunction function;
	private Matrix transform;
	private NoiseVariance noise;
	
	public HybridGPMSCovarianceFunction(CovarianceFunction baseCovarianceFunction,
			Matrix transformMatrix, NoiseVariance noiseVariance) {
		function = baseCovarianceFunction;
		transform = transformMatrix;
		noise = noiseVariance;
	}
	
	private void forceSymmetry(Matrix M){
		for(int i = 0; i<M.getColumnDimension(); i++){
			for(int j=(i+1); j<M.getRowDimension(); j++){
				M.set(i, j, M.get(j,i));
			}
		}
	}
	
	public Matrix calculateCovarianceMatrix(double[] loghyper, Matrix trainX) {

		int colCount = trainX.getColumnDimension();
		int rowCount = trainX.getRowDimension();

		Matrix noiseMatrix = new Matrix(rowCount, rowCount);

		for (int i = 0; i < rowCount; i++)
			noiseMatrix.set(i, i, noise.calculateVariance(loghyper, trainX.get(i, colCount-1)));

		Matrix result = transform.times(
				noiseMatrix.plus(function.calculateCovarianceMatrix
						(functionLogHyper(loghyper), stripTime(trainX))).times(transform.transpose()));
		forceSymmetry(result);
		return result;
	}

	public Matrix calculateTestCovarianceMatrix(double[] loghyper, Matrix testX) {
		int rowCount = testX.getRowDimension();
		int colCount = testX.getColumnDimension();

		Matrix noiseMatrix = new Matrix(rowCount, 1);
		
			
		for (int i = 0; i < rowCount; i++)
			noiseMatrix.set(i, 0, noise.calculateVariance(loghyper, testX.get(i, colCount-1)));

		return noiseMatrix.plus(function.calculateTestCovarianceMatrix
				(functionLogHyper(loghyper), stripTime(testX)));
	}

	public Matrix calculateTrainTestCovarianceMatrix(double[] loghyper,
			Matrix trainX, Matrix testX) {

		return transform.times(function.
				calculateTrainTestCovarianceMatrix(loghyper, 
						stripTime(trainX), stripTime(testX)));
	}

	public int getHyperParameterCount(Matrix trainX) {
		return noise.hyperParameterCount() + function.getHyperParameterCount(stripTime(trainX));
	}
	
	private Matrix stripTime(Matrix trainX) {
		return trainX.getMatrix(0, trainX.getRowDimension()-1, 
				0, trainX.getColumnDimension()-2);
	}

	private double[] functionLogHyper(double[] logHyper) {
		double[] result = new double[logHyper.length -noise.hyperParameterCount()];
		System.arraycopy(logHyper, noise.hyperParameterCount(), result, 0, result.length);
		return result;
	}
	
	public void setTransform(Matrix transformMatrix){
		transform = transformMatrix;
	}

}
