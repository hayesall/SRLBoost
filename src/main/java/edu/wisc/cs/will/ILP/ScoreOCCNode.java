package edu.wisc.cs.will.ILP;

import edu.wisc.cs.will.Utils.Utils;
import edu.wisc.cs.will.stdAIsearch.SearchInterrupted;
import edu.wisc.cs.will.stdAIsearch.SearchNode;

public class ScoreOCCNode extends ScoreSingleClauseByAccuracy {

	// Note we ADD penalties here, since the final score gets negated.
	private   final static double scalingPenalties = 0.1; // For regression we might want to shift the penalties since prediction errors might be smaller or larger
	private   final static double bonusForBridgers = 10000.0; // Seems this should suffice, though for some uses of regression it might not.  Don't want to lose the true score, since that'll help sort.	
	
	public ScoreOCCNode() {
		super();
	}

	public double computeMaxPossibleScore(SearchNode nodeRaw) {
		SingleClauseNode node = (SingleClauseNode)nodeRaw;
		return -scalingPenalties * getPenalties(node, false); // In best case, could end up with NO singleton variables.
	}
	
	public double scoreThisNode(SearchNode nodeRaw) throws SearchInterrupted {

		SingleClauseNode node  = (SingleClauseNode)nodeRaw;
		System.out.println("TEMP: Calculating score for " + node);
		if (!Double.isNaN(node.score)) { return node.score; }
		
		double fit = node.oneClassScore();
		double penalty = scalingPenalties * (getPenalties(node, true));
		
		double score   = fit + penalty; // Add small penalties as a function of length and the number of singleton variables (so shorter better if accuracy the same).
		Utils.println("%     Score = " + Utils.truncate(-score, 6) + " (regressionFit = " + Utils.truncate(fit, 6) + ", penalties=" + penalty + ") for clause:  " + node);

		node.score = -score;

		// Since the code MAXIMIZES, negate here.
		return -score;
	}
	
	public double computeBonusScoreForThisNode(SearchNode nodeRaw) {
		// If a clause ends with a DETERMINATE literal, we want to allow it to be expanded
		// since the determinate literal by itself is (usually) of no help.
		SingleClauseNode node  = (SingleClauseNode)nodeRaw; 
		if (node.endsWithBridgerLiteral()) {
			return bonusForBridgers; 
		}
		return 0;
	}
}
