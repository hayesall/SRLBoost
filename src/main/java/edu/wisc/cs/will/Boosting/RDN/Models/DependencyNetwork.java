package edu.wisc.cs.will.Boosting.RDN.Models;

import edu.wisc.cs.will.Boosting.RDN.WILLSetup;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/*
 * @author tkhot
 *
 */
public class DependencyNetwork {

	final Map<String, DependencyNode> stringRepToNode;
	
	DependencyNetwork() {
		stringRepToNode = new HashMap<>();
	}

	public void printNetworkInDOT(Writer stream) throws IOException {
		// Number all nodes.
		int counter=0;
		for (DependencyNode node : stringRepToNode.values()) {
			if (!node.ignoreNodeForDOT()) {
					node.setNumberForDOTGraph(counter++);
			}
		}
		stream.write("digraph RDN{\n");
		// Rename recursive nodes
		for (String stringRep : stringRepToNode.keySet()) {
			if (stringRep.startsWith(WILLSetup.recursivePredPrefix)) {
				DependencyNode node = stringRepToNode.get(stringRep);
				String newRep = stringRep.replace(WILLSetup.recursivePredPrefix, "");
				DependencyNode targetNode = stringRepToNode.get(newRep);
				node.setNumberForDOTGraph(targetNode.getNumberForDOTGraph());
			}
		}

		// For each node
		for (String stringRep : stringRepToNode.keySet()) {
			DependencyNode node = stringRepToNode.get(stringRep);
			if (!stringRep.startsWith(WILLSetup.recursivePredPrefix)) {
				if (node.getNumberForDOTGraph() != -1) {
					stream.write(node.getNumberForDOTGraph() + "[" + node.textForDOT() + "];\n");
				}
			}
			// Write edges
			for (int i = 0; i < node.numParents(); i++) {
				DependencyNode parent = node.getParent(i).getStart();
				if (node.getNumberForDOTGraph() != -1 && parent.getNumberForDOTGraph() != -1) {
					stream.write(parent.getNumberForDOTGraph() + " -> " + node.getNumberForDOTGraph() +
							"[" +  node.getParent(i).textForDOT() + "];\n");
				}
			}
		}
		stream.write("}\n");
	}


}
