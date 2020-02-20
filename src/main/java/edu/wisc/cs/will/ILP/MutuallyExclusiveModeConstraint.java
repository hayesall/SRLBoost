package edu.wisc.cs.will.ILP;

import edu.wisc.cs.will.FOPC.Literal;
import edu.wisc.cs.will.FOPC.PredicateNameAndArity;
import edu.wisc.cs.will.Utils.Utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
 * @author twalker
 */
public class MutuallyExclusiveModeConstraint implements ModeConstraint {

    private final static int debugLevel = 0;

    private final Set<PredicateNameAndArity> mutuallyExclusiveModes;

    private final int maxOccurances;

    MutuallyExclusiveModeConstraint(Collection<PredicateNameAndArity> mutuallyExclusiveModes, int maxOccurances) {
        this.mutuallyExclusiveModes = new HashSet<>(mutuallyExclusiveModes);
        this.maxOccurances = maxOccurances;
    }

    @Override
    public Set<PredicateNameAndArity> applyConstraint(SingleClauseNode nodeBeingExpanded, Set<PredicateNameAndArity> eligibleExpansionModes, boolean isMutable) {

        Set<PredicateNameAndArity> result = null;

        if ( debugLevel >= 2) {
            Utils.print("MutuallyExclusiveMode: Checking constraint on " + nodeBeingExpanded.getClause() + ".\n" );
        }

        if (nodeBeingExpanded.bodyLength() >= maxOccurances) {
            boolean removeModes = false;

            if (mutuallyExclusiveModes != null) {
                int count = 0;

                SingleClauseNode node = nodeBeingExpanded;
                while (node != null) {
                    Literal lit = node.literalAdded;
                    PredicateNameAndArity pna = lit.getPredicateNameAndArity();
                    if (mutuallyExclusiveModes.contains(pna)) {
                        count++;
                    }

                    if (count >= maxOccurances) {
                        removeModes = true;
                        break;
                    }

                    node = node.getParentNode();
                }

                if (removeModes) {
                    if (!isMutable) {
                        result = new HashSet<>(eligibleExpansionModes);
                    }

                    for (PredicateNameAndArity predicateNameAndArity : mutuallyExclusiveModes) {
                        assert result != null;
                        result.remove(predicateNameAndArity);
                        
                    }

                    if ( debugLevel >= 1 ) {
                            Utils.print("MutuallyExclusiveModeConstrain: Removing mode " + mutuallyExclusiveModes + ".\n" );
                        }
                }

            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "MutuallyExclusiveModes: MaxOccurances = " + maxOccurances + ", Predicates: {" + Utils.toString(mutuallyExclusiveModes, ", ") + "}";
    }
}
