package edu.wisc.cs.will.ILP;

import edu.wisc.cs.will.Utils.Utils;

import java.io.Serializable;

/*
 * @author shavlik
 *
 *  An entry in a Gleaner.
 */
class SavedClause implements Serializable {
	double recall;
	double F1;
	double score;

	SavedClause(Gleaner caller, SingleClauseNode clause) {
		// Holds a string that will be printed when the clause is dumped.
		// Annotation about what created this clause.

		// TODO(@hayesall): Nothing in this constructor appears to actually be used in the code base.

		try {
			clause.precision();
			recall      = clause.recall();
			F1          = clause.F(1);
			score       = clause.score;
			clause.getUptoKmissedPositiveExamples(5);
			clause.getUptoKcoveredNegativeExamples(5);
		} catch (Exception e) {
			Utils.reportStackTrace(e);
			Utils.error();
		}
	}
}
