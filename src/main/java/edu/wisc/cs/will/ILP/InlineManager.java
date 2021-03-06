package edu.wisc.cs.will.ILP;

import edu.wisc.cs.will.FOPC.*;
import edu.wisc.cs.will.ResThmProver.HornClausebase;
import edu.wisc.cs.will.Utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InlineManager {

	private final HandleFOPCstrings  stringHandler;
	private final HornClausebase     hornClauseKnowledgeBase;
	
	private final PredicateName      notPname;
	
	InlineManager(HandleFOPCstrings stringHandler, HornClausebase hornClauseKnowledgeBase) {
        this.stringHandler = stringHandler;
        this.hornClauseKnowledgeBase = hornClauseKnowledgeBase;

        this.notPname = stringHandler.standardPredicateNames.negationByFailure;
    }

	void addInventedClause(Literal head) {
		if (literalIsInlined(head)) { Utils.error("Already have inlined '" + head + "'."); }
		head.predicateName.addInliner(head.numberArgs());
	}
	
	private boolean literalIsInlined(Literal lit) { // TODO - put in Literals class if these seem to be more generally useful.
		if (lit == null) { return false; }
		PredicateName pName = lit.predicateName;
		return pName.isaInlined(lit.numberArgs());
	}
	
	private boolean literalIsaSupportingPredicate(Literal lit) {
		if (lit == null) { return false; }
		PredicateName pName = lit.predicateName;
		return pName.isaSupportingPredicate(lit.numberArgs());
	}
	
	private BindingList literalMatchesDeterminateClause(Literal lit, Clause c, BindingList bindings) {
		if (!c.isDefiniteClause()) { Utils.error("This code only handle definite clauses.  It was given: " + c); }
		Literal head = c.getDefiniteClauseHead();
		return Unifier.UNIFIER.unify(lit, head, bindings);
	}
	
	private Clause getLiteralDefinition(Literal lit) {
        if (lit == null) {
            return null;
        }
        if (literalIsInlined(lit)) {
            List<DefiniteClause> clauses = hornClauseKnowledgeBase.getAssertions(lit.predicateName, lit.getArity());
            if (clauses == null || clauses.size() != 1) {
                return null;
            }
            return clauses.get(0).getDefiniteClauseAsClause();
        }
        Utils.waitHere("getLiteralDefinition: this literal is not inlined: " + lit);
        return null;
    }

	private Clause getUnifiedLiteralDefinition(Literal lit, BindingList overallBindings) {
		if (lit == null)                 { Utils.error("Literal should be NULL here."); }
		getStringHandler().resetAllVariables(); // Need fresh variables for the following copy.
		Clause definer = getLiteralDefinition(lit);
		if ( definer == null)            { Utils.error("Expected a definition of this literal: " + lit); }
		else {
			definer = definer.copy(true);  // We need a fresh copy in case the same head appears more than once in a clause.
		}
		if (!definer.isDefiniteClause()) { Utils.error("This code only handle definitions that are definite clauses.  It was given: " + definer); }
		Literal     head = definer.posLiterals.get(0);
		BindingList bl   = Unifier.UNIFIER.unify(head, lit, overallBindings); // Doing this order will make it more likely that lit's variables remain.
		if (bl == null) {
			Utils.waitHere("Inliner could not be inserted; could not unify\n  " + lit + " and\n  " + head + "\n given overall bindings = " + overallBindings);
			return null;
		}
		// Will this next line mess up in recursion?  Hopefully not.
		overallBindings.addBindings(bl); // Delay this to here in case we figure out how to recover from that error.
		List<Literal> litsToKeep = new ArrayList<>(Utils.getSizeSafely(definer.negLiterals));
		if (definer.negLiterals != null) for (Literal innerLit : definer.negLiterals) if (!innerLit.predicateName.filter()) {
			litsToKeep.add(innerLit);
		}
		List<Literal> posLits = new ArrayList<>(1);
		posLits.add(head);
		return getStringHandler().getClause(posLits, litsToKeep).applyTheta(overallBindings.theta);
	}

	// Handle any 'inliners' in this clause.  Return a LIST of clauses,
	// where the FIRST clause is the new version of the provided clause,
	// and the REST of the Clauses (if any) are the SUPPORTING literals in
	// this clause.  (Recall that supporting literals are ones that need to accompany theories.)
	public List<Clause> handleInlinerAndSupportingClauses(Clause c) {
		if (c == null) { return null; }

		if (!c.isDefiniteClause()) { Utils.error("This code only handle definite clauses.  It was given: " + c); }

		Literal head = c.posLiterals.get(0);

		if (literalIsInlined(             head)) { Utils.error("This code assumes the HEAD is not an in-liner: "          + head + "."); } // TODO generalize
		if (literalIsaSupportingPredicate(head)) { Utils.error("This code assumes the HEAD is not a supporting literal: " + head + "."); }

		List<Clause> results = help_handleInlinerAndSupportingClauses(c, 0);
		if (results == null) { Utils.waitHere("Got no results from in-lining: " + c); return null; }
		VisitedClauses clausesVisited = new VisitedClauses(100000);  // Watch for duplicates in the Supporting Clauses.
		List<Clause>   newResults     = new ArrayList<>(results.size());
		for (Clause c2 : results) {
			Clause newClause = (Clause) getStringHandler().renameAllVariables(c2);
			newResults.add(newClause);
			clausesVisited.addClauseToClosed(getStringHandler(),newClause); // OK to add the 'main clause' here, since it would be odd to have the same clause as a main and supporting clause.
		}
		return newResults;
	}
	private final BindingList blToUse =  new BindingList();
	private List<Clause> help_handleInlinerAndSupportingClauses(Clause c, int depth) {

		if (c == null) { return null; }

		if (depth >  50) { Utils.severeWarning("Are some in-liners calling each other, producing an infinite loop?\n " + c); }
		if (depth > 100) { Utils.error("Seems to be an infinite loop.\n " + c); } // If this passed the severeWarning, and still a problem, simply give up.

        if (!c.isDefiniteClause()) { Utils.error("This code only handle definite clauses.  It was given: " + c); }
		
		List<Literal> newBodyLiterals = new ArrayList<>(c.getLength());
		Set<Clause>   supporters      = null; // Remove duplicates when possible, but might not too look for variants via VisitedClauses instance.
		BindingList   overallBindings = new BindingList(); // Need to keep track of all the bindings necessary to make the in-lines match up.

		if (c.negLiterals != null) for (Literal lit : c.negLiterals) {
			boolean isaInliner   = literalIsInlined(             lit);
			boolean isaSupporter = literalIsaSupportingPredicate(lit);
			
			// Here we assume any functions can/will be converted to a literal, e.g., they are inside a FOR-LOOP of some sort.
			// TODO - maybe we need to check the predicateName of lit and treat like we do for NOT (should also handle ONCE, CALL, etc ...), but risky to require names be in a list ...
			if (lit.predicateName != notPname && lit.numberArgs() > 0) for (Term t : lit.getArguments()) if (t instanceof Function) {
				Literal functAtLit = ((Function) t).convertToLiteral(getStringHandler());
				Iterable<Clause> definingClauses = getHornClauseKnowledgeBase().getPossibleMatchingBackgroundKnowledge(functAtLit, blToUse);
				if (definingClauses != null) for (Clause c2 : definingClauses) { // TODO clean up the duplicated code.
					blToUse.theta.clear();
					BindingList bl = literalMatchesDeterminateClause(functAtLit, c2, blToUse);
					if (bl == null) { continue; }
					List<Clause> recurResults = help_handleInlinerAndSupportingClauses(c2.applyTheta(bl.theta), depth + 1);
					if (supporters == null) { supporters = new HashSet<>(1); }
					//	Utils.println("%   supporters = " + recurResults);
					if (Utils.getSizeSafely(recurResults) > 0) { supporters.addAll(recurResults); }
				}
			}
			
			if (isaInliner && isaSupporter) { 
				Utils.error("This code assumes a literal is not BOTH an in-liner and a 'supporting' literal: " + lit + "."); // TODO generalize
			} else if (lit.predicateName == notPname) { // We want to leave these as is, but need to collecting any 'supporters' they need.

				if (isaInliner || isaSupporter) { Utils.error("This code assumes the negation-by-failure predicate is not an in-liner nor a 'supporting' literal: " + lit + "."); } // TODO generalize


                Clause clause = getStringHandler().getNegationByFailureContents(lit);
                if ( clause == null ) {
                    Utils.error("Expected term of negation to be Function or SentenceAsTerm.");
                }
                
                if ( clause.getNegLiteralCount() > 0 && clause.getPosLiteralCount() > 0 ) {
                    Utils.error("Negated clause should be composed of either positive literal or negative literal, but not both.");
                }

                if ( clause.getPosLiteralCount() == 0) {
                    // Make sure the negated clauses literals are positive
                    clause = clause.getStringHandler().getClause(clause.getNegativeLiterals(), clause.getPositiveLiterals());
                }

                if (Utils.getSizeSafely(clause.negLiterals) > 0) { Utils.error("Should not have negative literals inside a negation-by-failure.");         }
                if (Utils.getSizeSafely(clause.posLiterals) < 1) { Utils.error("Should have at least one positive literal inside a negation-by-failure."); }

                for (Literal pLit : clause.getPositiveLiterals()) {
                    if (literalIsaSupportingPredicate(pLit) || literalIsInlined(pLit)) { // If in-lined inside a NOT, need to convert to a SUPPORTER (could check if the body only has one literal ...).
                        // Next see if the body of the negation-by-failure has any-liners.  (I hate to duplicate this code, but seems easiest to do so.)
                        Iterable<Clause> definingClauses = getHornClauseKnowledgeBase().getPossibleMatchingBackgroundKnowledge(pLit, blToUse);
                        if (definingClauses != null) for (Clause c2 : definingClauses) {
                            blToUse.theta.clear();
                            BindingList bl = literalMatchesDeterminateClause(pLit, c2, blToUse);
                            if (bl == null) { continue; }

                            List<Clause> recurResults = help_handleInlinerAndSupportingClauses(c2.applyTheta(bl.theta), depth + 1);
                            if (supporters == null) { supporters = new HashSet<>(1); }
                            if (Utils.getSizeSafely(recurResults) > 0) { supporters.addAll(recurResults); }
                        }
                    }
                    else {
                       Clause litAsDefiniteClause = stringHandler.getClause(stringHandler.getLiteral(stringHandler.standardPredicateNames.trueName), pLit);
                        List<Clause> moreClauses = help_handleInlinerAndSupportingClauses(litAsDefiniteClause, depth+1);
                        if ( moreClauses != null && moreClauses.size() > 1 ) {
                            for (int i = 1; i < moreClauses.size(); i++) {
                                if (supporters == null) { supporters = new HashSet<>(1); }
                                supporters.add(moreClauses.get(i));
                            }
                        }
                    }
				}
				newBodyLiterals.add(lit);
			} else if (isaSupporter) {
				newBodyLiterals.add(lit);
				// Next see if the body of the supporter has any-liners.
				Iterable<Clause> definingClauses = getHornClauseKnowledgeBase().getPossibleMatchingBackgroundKnowledge(lit, null);
				if (definingClauses != null) for (Clause c2 : definingClauses)  {
					blToUse.theta.clear();
					BindingList bl = literalMatchesDeterminateClause(lit, c2, blToUse);
					if (bl == null) { continue; }
					if (supporters == null) { supporters = new HashSet<>(1); }
					List<Clause> recurResults = help_handleInlinerAndSupportingClauses(c2.applyTheta(bl.theta), depth + 1);
					
					if (Utils.getSizeSafely(recurResults) > 0) { supporters.addAll(recurResults); }
				}
			} else if (isaInliner) { // Need to replace this literal.
				Clause newDefn = getUnifiedLiteralDefinition(lit, overallBindings); // This will change overallBindings.
				
				List<Clause> recurResults = help_handleInlinerAndSupportingClauses(newDefn, depth + 1);
				if (Utils.getSizeSafely(recurResults) < 1) { Utils.error("recurResults = " + recurResults + " for newliner = " + lit + "\n newDefn = " + newDefn + "\n overall bindings =" + overallBindings); }
				
				if (supporters == null) { supporters = new HashSet<>(1); }
				Clause        result       = recurResults.remove(0);
				List<Literal> litsToInsert = result.negLiterals;
				// Would be an odd 'inliner' to have no body ...
				if (litsToInsert != null) newBodyLiterals.addAll(litsToInsert);
				else { Utils.waitHere("Have an inliner with no body! " + newDefn); }
				if (Utils.getSizeSafely(recurResults) > 0) { supporters.addAll(recurResults); }				
			} else { // Simply save.
				newBodyLiterals.add(lit);
			}			
		}
		Clause newClause = getStringHandler().getClause(c.posLiterals, newBodyLiterals, c.getExtraLabel());  // Note we are REUSING THE OLD HEAD.
		List<Clause> newListOfClauses = new ArrayList<>();
		Clause newClauseBound = newClause.applyTheta(overallBindings.theta);
		newListOfClauses.add(newClauseBound);
		if (Utils.getSizeSafely(supporters) < 1) { return newListOfClauses; }
		assert supporters != null;
		newListOfClauses.addAll(supporters); // These do not need to be unified since they are stand-alone.
		return newListOfClauses;		
	}

	private HandleFOPCstrings getStringHandler() {
        return stringHandler;
    }

	public HornClausebase getHornClauseKnowledgeBase() {
        return hornClauseKnowledgeBase;
    }

}
