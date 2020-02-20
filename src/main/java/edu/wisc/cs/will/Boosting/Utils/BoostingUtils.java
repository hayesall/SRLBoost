package edu.wisc.cs.will.Boosting.Utils;

import edu.wisc.cs.will.Boosting.EM.HiddenLiteralSamples;
import edu.wisc.cs.will.Boosting.RDN.RegressionRDNExample;
import edu.wisc.cs.will.DataSetUtils.Example;
import edu.wisc.cs.will.FOPC.*;
import edu.wisc.cs.will.Utils.ProbDistribution;
import edu.wisc.cs.will.Utils.RegressionValueOrVector;
import edu.wisc.cs.will.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * @author Tushar Khot
 *
 */
public class BoostingUtils {

	public static List<Example> convertToListOfExamples(List<RegressionRDNExample> examples) {
		if (examples == null) { return null; }
		List<Example> results = new ArrayList<>(examples.size());
		results.addAll(examples);
		return results;
	}

	public static List<RegressionRDNExample> castToListOfRegressionRDNExamples(List<Example> examples) {
		if (examples == null) { return null; }
		List<RegressionRDNExample> results = new ArrayList<>(examples.size());
		for (Example ex : examples) { results.add((RegressionRDNExample)ex); }
		return results;
	}
	
	public static RegressionValueOrVector getRegressionValueOrVectorFromTerm(Term leafTerm) {
		if (leafTerm instanceof NumericConstant) {
			return new RegressionValueOrVector(((NumericConstant) leafTerm).value.doubleValue());
		}
		if (leafTerm instanceof ConsCell) {
			ConsCell valarray = (ConsCell)leafTerm;
			double[] regVec = new double[valarray.length()];
			int index = 0;
			for (Term term : valarray) {
				double val  = ((NumericConstant) term).value.doubleValue();
				regVec[index++] = val;
			}
			return new RegressionValueOrVector(regVec);
		}
		Utils.error("Uknown type of constant in leaf: " + leafTerm.toPrettyString());
		return null;
	}

	public static String getLabelForModelNumber(int modelNumber) {
		return (modelNumber > 0 ? "Model" + modelNumber : ""); // Model 0 is only implicitly named, in case we only want one model.
	}

	public static String getLabelForCurrentModel() {
		return "";
	}

	public static String getLabelForResultsFileMarker() {
		return "";
	}

	public static String getModelFile(CommandLineArguments cmdArgs, String target, boolean includeExtension) {
		String filename = cmdArgs.getModelDirVal() + "bRDNs/" + target;
		if (cmdArgs.getModelFileVal() != null) {
			 filename += "_" + cmdArgs.getModelFileVal();
		}
		filename += getLabelForCurrentModel() + (includeExtension ? ".model" : "");
		Utils.ensureDirExists(filename);
		return filename;
	}

	public static List<PredicateNameAndArity> convertBodyModesToPNameAndArity(Set<PredicateNameAndArity> pnames) {
		List<PredicateNameAndArity> pars = new ArrayList<>();
		for (PredicateNameAndArity predicate : pnames) {
			// For each spec for the predicate
			for (PredicateSpec spec : predicate.getPredicateName().getTypeList()) {
				if (spec.getTypeSpecList() != null) {
					int arity = spec.getTypeSpecList().size();
					PredicateNameAndArity par = new PredicateNameAndArity(predicate.getPredicateName(), arity);
					// TODO(TVK) use a set.
					if (!pars.contains(par)) {
						pars.add(par);
					}
				}
			}
		}
		return pars;
	}


	public static double sigmoid(double numRegExp, double denoRegExp) {
		return 1/ (Math.exp(denoRegExp - numRegExp) + 1);
	}

	public static String getCheckPointFile(String saveModelName) {
		return saveModelName + ".ckpt";
	}

	public static String getCheckPointExamplesFile(String saveModelName) {
		return saveModelName + ".ckptEgs";
	}

	public static String getCheckPointFlattenedLitFile(String saveModelName) {
		return saveModelName + ".ckptLits";
	}

	public static double computeHiddenStateCLL(
			HiddenLiteralSamples sampledStates,
			Map<String, List<RegressionRDNExample>> hiddenExamples) {
		double cll=0;
		double counter = 0;
		double accuracyCounter = 0;
		double correct = 0;
		for (String predName : hiddenExamples.keySet()) {
			for (RegressionRDNExample eg : hiddenExamples.get(predName)) {
				ProbDistribution probDist = sampledStates.sampledProbOfExample(eg);
				double prob;
				if (probDist.isHasDistribution()) {
					double[] probs = probDist.getProbDistribution();
					
					int mostLikelyState = -1;
					double highestProb = 0.0; 
					for (int i = 0; i < probs.length; i++) {
						if (probs[i] > highestProb) {
							highestProb = probs[i];
							mostLikelyState = i;
						}
						if (eg.getOriginalHiddenLiteralVal() == i) {
							prob = probs[i];
						} else {
							prob = 1 - probs[i];
						}
						if (prob == 0) {
							prob = 1e-5;
						}
						cll += Math.log(prob);
						counter++;
					}
					if (mostLikelyState == eg.getOriginalHiddenLiteralVal()) {
						correct++;
					}
					accuracyCounter++;
				} else {
					prob = probDist.getProbOfBeingTrue();
					if (eg.getOriginalHiddenLiteralVal() == 0) {
						// False example with true prob < 0.5 ?
						if (prob < 0.5) {
							correct++;
						}
						prob = 1-prob;
					} else {
						// True example with true prob >= 0.5
						if (prob >= 0.5) {
							correct++;
						}
					}
					if (prob == 0) {
						prob = 1e-5;
					}
					cll += Math.log(prob);
					counter++;
					accuracyCounter++;
				}
				
			}
		}
		Utils.println("Hidden data accuracy: " + (correct / accuracyCounter) + " (" + correct + "/" + accuracyCounter + ").");
		return cll/counter;
		
	}
	
	
	
}
