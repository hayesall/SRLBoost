package edu.wisc.cs.will.FOPC;

import edu.wisc.cs.will.Utils.Utils;

import java.util.Collection;
import java.util.Map;

/*
 * @author shavlik
 *
 */
public class TermAsLiteral extends Literal {

	public final Term term;
	/*
	 * This is a dummy class.  It is used, during parsing, to hold a term inside something of type Literal.
	 */
	TermAsLiteral(HandleFOPCstrings stringHandler, Term term) {
		this.term          = term;
		this.stringHandler = stringHandler;
		if (term == null) { Utils.error("Cannot have term=null here."); }
	}

    @Override
	public Collection<Variable> collectFreeVariables(Collection<Variable> boundVariables) {
		Utils.error("Should not be called.");
		return null;
	}

    @Override
	public TermAsLiteral copy(boolean recursiveCopy) {
		return new TermAsLiteral(this.stringHandler, term.copy(recursiveCopy));
	}

	@Override
	public boolean containsFreeVariablesAfterSubstitution(BindingList theta) {
		if (term == null || theta == null) { return false; }
		return term.freeVariablesAfterSubstitution(theta);
	}

    @Override
	public TermAsLiteral applyTheta(Map<Variable,Term> bindings) {
		Term newTerm = term.applyTheta(bindings);
		return new TermAsLiteral(stringHandler, newTerm);
	}

	@Override
	public int hashCode() { // Need to have equal objects produce the same hash code.
		final int prime = 67;
		int result = 1;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}	
    @Override
	public boolean equals(Object other) {
		if (this == other) { return true; }
		if (!(other instanceof TermAsSentence)) { return false; }
		return term.equals(((TermAsSentence) other).term);
	}
	
    @Override
	public boolean containsVariables() {
		return term.containsVariables();
	}
	
    @Override
	public BindingList variants(Sentence other, BindingList bindings) {
		if (!(other instanceof TermAsLiteral)) { return null; }
		return term.variants(((TermAsLiteral) other).term, bindings);
	}

    @Override
	public String toPrettyString(String newLineStarter, int precedenceOfCaller, BindingList bindingList) {
		return " >>> " + term.toPrettyString(newLineStarter, precedenceOfCaller, bindingList) + " <<< ";
	}
    @Override
	public String toString(int precedenceOfCaller, BindingList bindingList) {
		return " >>> " + term.toString(precedenceOfCaller, bindingList) + " <<< ";
	}

}
