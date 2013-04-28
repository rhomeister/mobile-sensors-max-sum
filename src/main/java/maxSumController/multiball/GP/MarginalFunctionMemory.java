package maxSumController.multiball.GP;

import java.util.LinkedList;

import Jama.Matrix;

public class MarginalFunctionMemory {

	class IterationRecording{
		public boolean valuable;
		public double[] xVals;
		public double[] yVals;
		public double[] tVals;
		public IterationRecording(boolean valuable, double[] xVals, double[] yVals,
				double[] tVals){
			this.valuable = valuable;
			this.xVals = xVals;
			this.yVals = yVals;
			this.tVals = tVals;
		}
	}
	private LinkedList<IterationRecording> iterations;
	private int iterationsIndex;
	private int maxIterationMemory;

	public Matrix X;
	public Matrix Y;
	public int[] iterationNum;
	public int numDataPoints;
	
	public MarginalFunctionMemory(int maxIterationMemory){
		this.maxIterationMemory = maxIterationMemory;
		iterationsIndex = 0;
		iterations = new LinkedList<IterationRecording>();
		X = null;
		Y = null;
		iterationNum = null;
		numDataPoints = 0;
	}

	public void add(double[] xVals, double[] yVals,
		double[] tVals){
		
		// drop less valuable recording if space is limited
		if(iterations.size() == maxIterationMemory){
			while((iterationsIndex < maxIterationMemory) && 
					iterations.get(iterationsIndex).valuable) iterationsIndex++;
			
			if(iterationsIndex < maxIterationMemory){
				numDataPoints -= iterations.get(iterationsIndex).xVals.length;
				iterations.remove(iterationsIndex);
				while((iterationsIndex < (maxIterationMemory-1)) && 
						iterations.get(iterationsIndex).valuable) iterationsIndex++;	
			} else {
				numDataPoints -= iterations.get(0).xVals.length;
				iterations.remove(0);
				iterationsIndex = maxIterationMemory-1;
			}
		}
		
		// remember the values obtained in this iteration
		iterations.add(new IterationRecording(false, xVals, yVals, tVals));
		numDataPoints += xVals.length;
		setPublicFields();
	}

	public void setLastValuable(){
		iterations.getLast().valuable = true;
	}
	
	private void setPublicFields(){
		X = new Matrix(numDataPoints, 2);
		Y = new Matrix(numDataPoints, 1);
		iterationNum = new int[numDataPoints];
		
		int i = 0;
		int currentIterationNum = 0;

		for(IterationRecording recording : iterations){
			for(int j=0; j < recording.xVals.length; j++){
				X.set(i, 0, recording.xVals[j]);
				X.set(i, 1, recording.tVals[j]);
				Y.set(i, 0, recording.yVals[j]);
				iterationNum[i] = currentIterationNum;
				i++;
			}
			if(recording.valuable) currentIterationNum++;
		}
		
	}
	
}
