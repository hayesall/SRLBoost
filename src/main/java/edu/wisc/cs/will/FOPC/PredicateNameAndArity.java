package edu.wisc.cs.will.FOPC;

import java.util.Collections;
import java.util.List;

public class PredicateNameAndArity {

    private final PredicateName predicateName;

    private final int arity;

    public PredicateNameAndArity(PredicateName predicateName, int arity) {
        this.predicateName = predicateName;
        this.arity = arity;
    }

    public PredicateNameAndArity(DefiniteClause definiteClause) {
        Literal clauseHead = definiteClause.getDefiniteClauseHead();

        this.predicateName = clauseHead.predicateName;
        this.arity = clauseHead.numberArgs();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PredicateNameAndArity other = (PredicateNameAndArity) obj;
        if (this.predicateName != other.predicateName) {
            return false;
        }
        return this.arity == other.arity;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.predicateName != null ? this.predicateName.hashCode() : 0);
        hash = 17 * hash + this.arity;
        return hash;
    }

    public PredicateName getPredicateName() {
        return predicateName;
    }

    public int getArity() {
        return arity;
    }

    /* Returns all of the Predicate specification attached to the predicate/arity.
     *
     */
    public List<PredicateSpec> getPredicateSpecs() {
        List<PredicateSpec> result = predicateName.getTypeListForThisArity(arity);
        if ( result == null ) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    public void markAsSupportingPredicate(boolean okIfDup) {
        predicateName.markAsSupportingPredicate(arity, okIfDup);
    }

    public boolean isInlined() {
        return predicateName.isaInlined(arity);
    }

    public boolean isNonOperational() {
        return predicateName.isNonOperational(arity);
    }

    public void setCost(double cost) {
        getPredicateName().setCost(arity, cost, false);
    }

    public boolean isDeterminateOrFunctionAsPred() {
        return predicateName.isDeterminateOrFunctionAsPred(arity);
    }

    public int getDeterminateOrFunctionAsPredOutputIndex() {
        return predicateName.getDeterminateOrFunctionAsPredOutputIndex(arity);
    }

    public void setContainsCallable() {
        predicateName.setContainsCallable(arity);
    }

    public boolean getContainsCallable() {
        return predicateName.isContainsCallable(arity);
    }

    @Override
    public String toString() {
        return predicateName + "/" + arity;
    }
}
