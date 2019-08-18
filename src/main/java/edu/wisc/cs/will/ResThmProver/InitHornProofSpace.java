package edu.wisc.cs.will.ResThmProver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.wisc.cs.will.FOPC.Clause;
import edu.wisc.cs.will.FOPC.HandleFOPCstrings;
import edu.wisc.cs.will.FOPC.Literal;
import edu.wisc.cs.will.ResThmProver.HornClauseProverChildrenGenerator.CutLiteral;
import edu.wisc.cs.will.ResThmProver.HornClauseProverChildrenGenerator.CutMarkerNode;
import edu.wisc.cs.will.stdAIsearch.Initializer;
import edu.wisc.cs.will.stdAIsearch.OpenList;
import edu.wisc.cs.will.Utils.Utils;

/*
 * @author shavlik
 *
 */
public class InitHornProofSpace extends Initializer {

    InitHornProofSpace(HornClauseProver task) {
		this.task                     = task;
	}


    void loadNegatedSimpleQuery(Literal negatedQuery, OpenList openList) {
        loadNegatedConjunctiveQuery(Collections.singletonList(negatedQuery), openList);
    }

	public void loadNegatedConjunctiveQuery(List<Literal> negatedQueryLiterals, OpenList openList) {

        HornSearchNode[] nodes;        
        
        HornClauseProverChildrenGenerator.proofCounter = 0;
        HornClauseProverChildrenGenerator.cutMarkerCounter = 0;
        long proofCount = HornClauseProverChildrenGenerator.proofCounter++;


        if ( containsCut(negatedQueryLiterals) ) {
            nodes = createCutMarkerNodes(negatedQueryLiterals, proofCount);
        }
        else {
            nodes = createNonCutNodes(negatedQueryLiterals, proofCount);
        }
        
        if ( openList != null ) initializeOpen(openList, nodes);

        if ( getHornClauseProver().getTraceLevel() > 0 ) {
            Utils.println(String.format("[%6d] Initial proof nodes:", proofCount));

            for (HornSearchNode node : nodes) {
                System.out.println(String.format("             [%d] %s", node.parentExpansionIndex, node.clause));
            }
        }
	}

    private HornSearchNode[] createNonCutNodes(List<Literal> negatedQueryLiterals, long proofCount) {
        Clause negatedQuery = getStringHandler().getClause(negatedQueryLiterals, false); // These are all negated (i.e., checked above), so tell Clause() that.
        

        HornSearchNode negatedQueryAsRootNode = new HornSearchNode(getHornClauseProver(), negatedQuery, null, proofCount, 0);

        HornSearchNode[] nodes = new HornSearchNode[1];
        nodes[0] = negatedQueryAsRootNode;
        
        return nodes;
    }

    public void initializeOpen(OpenList openList) {}
	
    private void initializeOpen(OpenList openList, HornSearchNode... nodes) {
        if ( openList != null ) {
            openList.clear();
            for(HornSearchNode hornSearchNode : nodes) {
                openList.addToEndOfOpenList(hornSearchNode);
            }
        }
	}

    private HornSearchNode[] createCutMarkerNodes(List<Literal> negatedQueryLiterals, long proofCount) {

        Literal literalBeingCut = getHornClauseProver().getStringHandler().getLiteral("directProofOfClause");
        CutMarkerNode cutMarkerNode = new CutMarkerNode((HornClauseProver)task, literalBeingCut, proofCount);
        Literal cutLiteral = new CutLiteral(getStringHandler(), cutMarkerNode);

        List<Literal> newQueryLiterals = replaceCutsWithMarkedLiteral(negatedQueryLiterals, cutLiteral);

        Clause newNegatedQuery    = getStringHandler().getClause(newQueryLiterals, false); // These are all negated (i.e., checked above), so tell Clause() that.

        HornSearchNode rootNode          = new HornSearchNode(getHornClauseProver(), newNegatedQuery, null, proofCount, 0);

        HornSearchNode[] nodes = new HornSearchNode[2];
        nodes[0]=rootNode;
        nodes[1]=cutMarkerNode;

        return nodes;
    }

    private List<Literal> replaceCutsWithMarkedLiteral(List<Literal> ruleBody, Literal cutLiteral) {
        List<Literal> newRuleBody = new ArrayList<>(ruleBody.size());
        for (Literal lit : ruleBody) {
            if (lit.predicateName == getStringHandler().standardPredicateNames.cut) {
                newRuleBody.add(cutLiteral);
            }
            else {
                newRuleBody.add(lit);
            }
        }
        if (HornClauseProver.debugLevel > 2) {
            Utils.println("markCutLiterals: " + newRuleBody);
        }
        return newRuleBody;
    }

    private HandleFOPCstrings getStringHandler() {
        return ((HornClauseProver) task).getStringHandler();
    }

    private boolean containsCut(List<Literal> negatedConjunctiveQuery) {
        if ( negatedConjunctiveQuery != null ) {
            for (Literal literal : negatedConjunctiveQuery) {
                if (( literal.predicateName == getStringHandler().standardPredicateNames.cut)) {
                    return true;
                }
            }
        }
        return false;
    }

    private HornClauseProver getHornClauseProver() {
        return (HornClauseProver)this.task;
    }


}
