package resultProcessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ConvCycleProcessor {
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
		ArrayList<Double> notconv = new ArrayList<Double>();

		double totConflicts = 0.;
		double totMessages = 0.;
		int totNotConv = 0;
		int totalTs = 0;
		int totalConvTs = 0;
		while (line != null) {
			// System.out.println(line);
			StringTokenizer t = new StringTokenizer(line);
			double currTs = 0;
			double currConflicts = 0;
			double currMessages = 0;
			if (t.hasMoreTokens()) {
				currTs = new Double(t.nextToken()).doubleValue();
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
			ts.add(currTs);
			totalTs++;

			conflicts.add(currConflicts);
			totConflicts += currConflicts;

			messages.add(currMessages);
			totMessages += currMessages;
			if (currMessages == currTs) {
				totNotConv++;
			}
			totalConvTs++;
			line = in.readLine();
		}
		double avgNotConv = (((double) totNotConv) / totalConvTs);
		double avgConflicts = -1.;
		double avgMessages = -1.;
		double varianceConflicts = -1.;
		double varianceMessages = -1.;
		double stdDevConflicts = -1.;
		double stdDevMessages = -1.;
		if (totalTs > 0) {
			avgConflicts = (double) (totConflicts / totalTs);
			avgMessages = (double) (totMessages / totalTs);

			varianceConflicts = 0.;
			varianceMessages = 0.;
			for (int i = 0; i < totalTs; i++) {
				varianceConflicts += (avgConflicts - (conflicts.get(i)))
						* (avgConflicts - (conflicts.get(i)));
				varianceMessages += (avgMessages - (messages.get(i)))
						* (avgMessages - (messages.get(i)));
			}
			stdDevConflicts = Math.sqrt(varianceConflicts / totalTs)
					/ Math.sqrt(totalTs);
			// stdDevConflicts = Math.sqrt(varianceConflicts/totalTs);
			stdDevMessages = Math.sqrt(varianceMessages / totalTs)
					/ Math.sqrt(totalTs);
			// stdDevMessages = Math.sqrt(varianceMessages/totalTs);
		}
		System.out.print(avgNotConv + "\t" + (avgConflicts) + "\t"
				+ (avgMessages) + "\t" + (stdDevConflicts) + "\t"
				+ (stdDevMessages) + "\n");
	}
}
