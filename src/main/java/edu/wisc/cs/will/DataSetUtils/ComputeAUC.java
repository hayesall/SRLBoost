package edu.wisc.cs.will.DataSetUtils;

import edu.wisc.cs.will.Utils.Utils;
import edu.wisc.cs.will.Utils.condor.CondorFile;
import edu.wisc.cs.will.Utils.condor.CondorFileWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class computes the AUC(ROC/PR) using http://mark.goadrich.com/programs/AUC/
 * Since this jar requires filename input and output, this class writes and reads files for
 * computing AUC.
 * TODO Write our own code OR get the source code for the JAR to compute AUC
 * @author Tushar Khot
 *
 */
public class ComputeAUC {
	
	private static final String  temporaryFileForAUC = "aucTemp";
	public static boolean deleteAUCfilesAfterParsing = true;
	// TODO use a better(more complete) regex for floats
	private static final String  PRPattern   = ".*Area Under the Curve for Precision - Recall is ([\\d\\.eE-]+).*";
	private static final String  ROCPattern  = ".*Area Under the Curve for ROC is ([\\d\\.eE-]+).*";

	private double ROC = Double.NaN;
	private double PR  = Double.NaN;
	private final double CLL;
	private double minRecallForPR = 0;
	private StringBuffer outputFromAUC;
	private String       aucFile                       = null;
	private String       outputFromAUC_txtfile         = null;
	private String       outputFromAUCfiltered_txtfile = null;

	public ComputeAUC(List<Double> positiveExampleProbabilities,
			          List<Double> negativeExampleProbabilities,
					  String useDirectoryForTempFile,
					  String aucJarLocation,
					  String extraMarker,
					  double minRecallForPR, boolean useLockFiles) {
		CLL = getCLL(positiveExampleProbabilities, negativeExampleProbabilities);
		// The AUC code crashes if all the examples are of the same category.
		if (Utils.getSizeSafely(positiveExampleProbabilities) < 1) {
			Utils.println("\nNo positive examples in ComputeAUC.  Using AUC-ROC = 0.5 and AUC-PR = 0.0");
			ROC = 0.5;
			PR  = 0.0;
			return;
		}
		if (Utils.getSizeSafely(negativeExampleProbabilities) < 1) {
			Utils.println("\nNo negative examples in ComputeAUC.  Using AUC-ROC = 1.0 and AUC-PR = 1.0");
			ROC = 1.0;
			PR  = 1.0;
			return;
		}
		
		this.minRecallForPR = minRecallForPR;
		// USE command line argument aucJarPath to provide a different jarLocation.
		String jarLocation = "auc.jar";
		if (aucJarLocation != null) {
			jarLocation = aucJarLocation + "/auc.jar";
		}
		if (!(new CondorFile(jarLocation)).exists()) {
			Utils.error("\nThis jar cannot be found: " + jarLocation + "\n Set path with `-aucJarPath [LOCATION]`\n");
		}
		if (extraMarker == null) { extraMarker = ""; }
		aucFile = useDirectoryForTempFile + temporaryFileForAUC + extraMarker + Utils.defaultFileExtensionWithPeriod;
		File  f = Utils.ensureDirExists(aucFile);
		if (f == null) {
			Utils.waitHere("The jar file doesn't exist: " + aucFile);
			return;
		}
		if (useLockFiles) try {
			Utils.obtainLock(f);
		} catch (IOException e) {
			Utils.reportStackTrace(e);
			Utils.error("Something went wrong: " + e);
		}
		writeExamplesToFile(positiveExampleProbabilities, negativeExampleProbabilities, aucFile); 		
		computeAUCFromJar(useDirectoryForTempFile, extraMarker, aucFile, jarLocation);
		parseResultsFromOutput();
		if (deleteAUCfilesAfterParsing) { deleteCreatedFiles(); } 
		if (useLockFiles)               { Utils.releaseLock(f); }
	}
	
	private double getCLL(List<Double> posProb,
				List<Double> negProb) {
		Utils.println("%Pos=" + posProb.size());
		Utils.println("%Neg=" + negProb.size());
		double llSum = 0;
		for (Double prob : posProb) {
			if (prob == 0) {
				prob = 1e-6;
			}
			llSum += Math.log(prob);
		}
		Utils.println("%LL:" + llSum);
		for (Double prob : negProb) {
			if (prob == 1) {
				prob = 1 - 1e-6;
			}
			llSum += Math.log(1 - prob);
		}
		Utils.println("%LL:" + llSum);
		return llSum/(posProb.size() + negProb.size());
	}
	
	private void deleteCreatedFiles() {
		if (deleteAUCfilesAfterParsing) { 
			File f = new CondorFile(aucFile);
			if (f.exists() && !f.delete()) { Utils.warning("Could not delete: " + f); }
			f = new CondorFile(aucFile + ".pr");
			if (f.exists() && !f.delete()) { Utils.warning("Could not delete: " + f); }
			f = new CondorFile(aucFile + ".roc");
			if (f.exists() && !f.delete()) { Utils.warning("Could not delete: " + f); }
			f = new CondorFile(aucFile + ".spr");
			if (f.exists() && !f.delete()) { Utils.warning("Could not delete: " + f); }
			if (outputFromAUC_txtfile != null) {
				f = new CondorFile(outputFromAUC_txtfile);
				if (f.exists() && !f.delete()) { Utils.warning("Could not delete: " + f); }
			}
			if (outputFromAUCfiltered_txtfile != null) {
				f = new CondorFile(outputFromAUCfiltered_txtfile);
				if (f.exists() && !f.delete()) { Utils.warning("Could not delete: " + f); }
			}
		}
	}


	private void parseResultsFromOutput() {
		ROC = getDoubleFromPattern(ROCPattern, outputFromAUC);
		PR  = getDoubleFromPattern(PRPattern,  outputFromAUC);
	}
	
	private double getDoubleFromPattern(String pattern,	StringBuffer inputStr) {
		Pattern pat     = Pattern.compile(pattern);
		String  toMatch = inputStr.toString().replaceAll("\\n", " ").replaceAll("\\r", " "); // Line feeds cause problems in Windows (as of 6/8/10) - JWS.
		Matcher mat     = pat.matcher(toMatch);
		if (!mat.matches()) {
			Utils.warning("Didn't find a match of\n\n   " + pattern + "\n\n in\n\n   " + toMatch);
			return Double.NaN;
		}
		String dblStr = mat.group(1);		
		return Double.parseDouble(dblStr);
	}


	private void computeAUCFromJar(String workingDir, String extraMarker, String filename, String jarPath) {		
		// Put quotes if contains space e.g. due to "Documents And Settings"
		if (jarPath.contains(" ")) {
			jarPath = "\"" + jarPath + "\"";
		}
		
		if (filename.contains(" ")) {
			filename = "\"" + filename + "\"";
		}
		
		String command = "java -jar " + jarPath + " " + filename + " list " + minRecallForPR;
		try {
			Utils.println("\n% Running command: " + command); // See http://mark.goadrich.com/programs/AUC/
			Process     p  = Runtime.getRuntime().exec(command);
			InputStream is = p.getInputStream();
			
			outputFromAUC                         = new StringBuffer();
			StringBuilder outputFromAUC_unfiltered = new StringBuilder();
			int c;
			boolean startCollecting = false;
			while ((c = is.read()) != -1) {

				outputFromAUC_unfiltered.append((char)c);

				// Ignore everything before "Area under ... "
				if (c == 'A') {
					startCollecting = true;
				}

				// Newlines cause trouble during regex matching.
				if (c == '\n' || c == '\r') {
					c = ' ';
				}
				if (startCollecting) {
					outputFromAUC.append((char) c);
				}
			}
			Utils.println("% WAITING FOR command: " + command);
			p.waitFor();
			Utils.println("% DONE WAITING FOR command: " + command); // + " workingDir = " + workingDir);
			if (outputFromAUC.length() < 1) { 
				Utils.waitHere("Never collected any AUC output!"); 
			} else {
				outputFromAUC_txtfile         = Utils.replaceWildCards(workingDir + "/outputFromAUC"          + extraMarker + Utils.defaultFileExtensionWithPeriod);
				outputFromAUCfiltered_txtfile = Utils.replaceWildCards(workingDir + "/outputFromAUC_FILTERED" + extraMarker + Utils.defaultFileExtensionWithPeriod);
				Utils.writeStringToFile(outputFromAUC_unfiltered.toString(),  Utils.ensureDirExists(outputFromAUC_txtfile));
				Utils.writeStringToFile(outputFromAUC.toString(),             Utils.ensureDirExists(outputFromAUCfiltered_txtfile));
			}
		} catch (IOException | InterruptedException e) {
			Utils.reportStackTrace(e);
			Utils.error("Problem running: " + command);
		}
	}


	private void writeExamplesToFile(
			List<Double> positiveExampleProbabilities,
			List<Double> negativeExampleProbabilities,
			String filename) {
		try {
			Utils.ensureDirExists(filename);
			BufferedWriter writer = new BufferedWriter(new CondorFileWriter(filename, false)); // Create a new file.
			for (Double dbl : positiveExampleProbabilities) {
				writer.write(dbl + " 1\n");
			}
			for (Double dbl : negativeExampleProbabilities) {
				writer.write(dbl + " 0\n");
			}
			writer.close();
		} catch (IOException e) {
			Utils.reportStackTrace(e);
			Utils.error("Problem in writeExamplesToFile: " + filename);
		}
	}

	public double getROC() {
		return ROC;
	}

	public double getPR() {
		return PR;
	}
	
	public double getCLL() {
		return CLL;
	}
	
}
