package boundedMaxSum;

import java.util.Map;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;

public class BoundedMaxSumResult {

		private double optimalUtility;

		private double actualUtility;
		
		private double bound;

		private Map<? extends DiscreteVariable<?>, DiscreteVariableState> optimalConfiguration;

		private Map<DiscreteInternalVariable<?>, DiscreteVariableState> actualConfiguration;

		private int numColours;

		private int numNodes;

		private double averageConnectionsPerNode;

		private int edges;

		private int edgesDeleted;

		private double simpleUpperBound;

		private double upperBound;

		private double treeUtility;

		private long completionTime;

		private double commOverhead;

		public int getEdges() {
			return edges;
		}

		public void setSimpleUpperBound(double simpleUpperBound) {
			this.simpleUpperBound = simpleUpperBound;
		}

		public void setUpperBound(double upperBound) {
			this.upperBound = upperBound;
		}

		public void setTreeUtility(double treeUtility) {
			this.treeUtility = treeUtility;
		}

		public void setEdges(int edges) {
			this.edges = edges;
		}

		public int getEdgesDeleted() {
			return edgesDeleted;
		}

		public void setEdgesDeleted(int edgesDeleted) {
			this.edgesDeleted = edgesDeleted;
		}

		public double getOptimalUtility() {
			return optimalUtility;
		}

		public void setOptimalUtility(double optimalUtility) {
			this.optimalUtility = optimalUtility;
		}

		public double getActualUtility() {
			return actualUtility;
		}

		public void setActualUtility(double actualUtility) {
			this.actualUtility = actualUtility;
		}

		public double getBound() {
			return bound;
		}

		public void setBound(double bound) {
			this.bound = bound;
		}

		public Map<? extends DiscreteVariable<?>, DiscreteVariableState> getOptimalConfiguration() {
			return optimalConfiguration;
		}

		public void setOptimalConfiguration(
				Map<? extends DiscreteVariable<?>, DiscreteVariableState> optimalConfiguration) {
			this.optimalConfiguration = optimalConfiguration;
		}

		public Map<DiscreteInternalVariable<?>, DiscreteVariableState> getActualConfiguration() {
			return actualConfiguration;
		}

		public void setActualConfiguration(
				Map<DiscreteInternalVariable<?>, DiscreteVariableState> actualConfiguration) {
			this.actualConfiguration = actualConfiguration;
		}

		public int getNumColours() {
			return numColours;
		}

		public void setNumColours(int numColours) {
			this.numColours = numColours;
		}

		public int getNumNodes() {
			return numNodes;
		}

		public void setNumNodes(int numNodes) {
			this.numNodes = numNodes;
		}

		public double getAverageConnectionsPerNode() {
			return averageConnectionsPerNode;
		}

		public void setAverageConnectionsPerNode(
				double averageConnectionsPerNode) {
			this.averageConnectionsPerNode = averageConnectionsPerNode;
		}

		
		public double getTreeUtility() {
			return treeUtility;
		}
		
		public double getUpperBound() {
			return upperBound;
		}
		
		public double getSimpleUpperBound() {
			return simpleUpperBound;
		}
		
		public String toCSV() {
			StringBuffer sb = new StringBuffer();
			sb.append(numNodes);
			sb.append(',');
			sb.append(averageConnectionsPerNode);
			sb.append(',');
			sb.append(optimalUtility);
			sb.append(',');
			sb.append(actualUtility);
			sb.append(',');
			sb.append(treeUtility);
			sb.append(',');
			sb.append(upperBound);
			sb.append(',');
			sb.append(simpleUpperBound);
			sb.append(',');
			sb.append(edges);
			sb.append(',');
			sb.append(edgesDeleted);
			sb.append(',');
			sb.append(completionTime);
			sb.append(',');
			sb.append(commOverhead);

			return sb.toString();
		}

		public void setMSCompletionTime(long l) {
			// TODO Auto-generated method stub
			completionTime = l;
		}

		public void setCommOverhead(double commOverhead) {
			// TODO Auto-generated method stub
			this.commOverhead = commOverhead; 
		}
	}

