package resultProcessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ResultProcessor {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Error, you should provide a file name");
			System.exit(0);
		}
		String filename = args[0];
		// System.out.println("Processing file "+filename);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));
		String line = in.readLine();
		ArrayList ts = new ArrayList();
		ArrayList<Double> conflicts = new ArrayList<Double>();
		ArrayList<Double> messages = new ArrayList<Double>();

		double totConflicts = 0.;
		double totMessages = 0.;
		int totalTs = 0;
		while (line != null) {
			StringTokenizer t = new StringTokenizer(line);
			double currTs = 0;
			double currConflicts = 0;
			double currMessages = 0;
			if (t.hasMoreTokens()) {
				currTs = new Integer(t.nextToken()).intValue();
			} else {
				System.out.println("WRONG FILE FORMAT: missing time steps");
				return;
			}
			if (t.hasMoreTokens()) {
				currConflicts = new Double(t.nextToken()).doubleValue();
			} else {
				System.out.println("WRONG FILE FORMAT: missing conflicts");
				return;
			}
			if (t.hasMoreTokens()) {
				currMessages = new Double(t.nextToken()).doubleValue();
			} else {
				System.out.println("WRONG FILE FORMAT: missing messages");
				return;
			}
			if (currMessages > -1.0) {

				ts.add(currTs);
				totalTs++;

				conflicts.add(currConflicts);
				totConflicts += currConflicts;

				messages.add(currMessages);
				totMessages += currMessages;
			} else {
				// System.out.println("not considering this
				// value"+currMessages);
			}
			line = in.readLine();
		}
		double avgConflicts = (double) (totConflicts / totalTs);
		double avgMessages = (double) (totMessages / totalTs);
		double varianceConflicts = 0.;
		double varianceMessages = 0.;

		for (int i = 0; i < totalTs; i++) {
			varianceConflicts += (avgConflicts - (conflicts.get(i)))
					* (avgConflicts - (conflicts.get(i)));
			varianceMessages += (avgMessages - (conflicts.get(i)))
					* (avgMessages - (conflicts.get(i)));
		}
		double stdDevConflicts = Math.sqrt(varianceConflicts / totalTs)
				/ Math.sqrt(totalTs);
		double stdDevMessages = Math.sqrt(varianceMessages / totalTs)
				/ Math.sqrt(totalTs);
		System.out.print("0\t" + (avgConflicts) + "\t" + (avgMessages) + "\t"
				+ (stdDevConflicts) + "\t" + (stdDevMessages) + "\n");
	}
}
