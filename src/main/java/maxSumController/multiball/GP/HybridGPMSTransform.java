package maxSumController.multiball.GP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

public class HybridGPMSTransform {

	//Generates a transform matrix for a given set of measurement groupings.
	public static Matrix generate(int[] measurementGroupings) {
		
		int numRows = measurementGroupings.length;
		
		Map<Integer, List<Integer>> measurementGroups = 
			new HashMap<Integer, List<Integer>>();
		List<Integer> groupIndicies = new LinkedList<Integer>();
		
		for(int i=0; i<numRows; i++){
			Integer groupIndex = new Integer(measurementGroupings[i]);
			List<Integer> group = measurementGroups.get(groupIndex);
			if (group == null) {
				group = new LinkedList<Integer>();
				measurementGroups.put(groupIndex, group);
				groupIndicies.add(groupIndex);
			}
			group.add(new Integer(i));
		}
		
		Matrix transformMatrix = new Matrix(numRows - groupIndicies.size(), 
				numRows, 0);
		
		int rowNumber = 0;
		int groupNumber = 0;
		for(Integer groupIndex : groupIndicies) {
			Iterator<Integer> group = measurementGroups.get(groupIndex).iterator();
			int groupMember = group.next().intValue();
			while(group.hasNext()){
				transformMatrix.set(rowNumber, groupMember, 1);
				groupMember = group.next().intValue();
				transformMatrix.set(rowNumber, groupMember, -1);
				rowNumber++;
			}
			groupNumber++;
		}
		
		return transformMatrix;
	}

}
