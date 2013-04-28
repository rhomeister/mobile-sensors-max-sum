package resultProcessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class extractConvCycle {
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
		int convCycle = -1;
		double finalConflicts = -1;
		int currCycle = 0;
		boolean converged = false;

		while (line != null) {
			// System.out.println(line);
			StringTokenizer t = new StringTokenizer(line);
			if (t.hasMoreTokens()) {
				int currTs = new Integer(t.nextToken()).intValue();
				currCycle = currTs;
			} else {
				System.out.println("WRONG FILE FORMAT: missing time steps");
				return;
			}
			if (t.hasMoreTokens()) {
				double currConflicts = new Double(t.nextToken()).doubleValue();
				finalConflicts = currConflicts;
			} else {
				System.out.println("WRONG FILE FORMAT: missing conflicts");
				return;
			}
			if (t.hasMoreTokens()) {
				double currMessages = new Double(t.nextToken()).doubleValue();
				if (currMessages != 0) {
					convCycle = currCycle;
					// converged = true;
				}
			} else {
				System.out.println("WRONG FILE FORMAT: missing messages");
				return;
			}
			line = in.readLine();
		}

		System.out.print(currCycle + "\t" + finalConflicts + "\t" + convCycle
				+ "\n");
	}

}
