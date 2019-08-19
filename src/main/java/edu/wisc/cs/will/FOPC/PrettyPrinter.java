package edu.wisc.cs.will.FOPC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import edu.wisc.cs.will.FOPC.visitors.SentenceVisitor;
import edu.wisc.cs.will.FOPC.visitors.TermVisitor;

/*
 * @author twalker
 */
public class PrettyPrinter {

    private static final PrettyPrinterVisitor PRETTY_PRINTER_VISITOR = new PrettyPrinterVisitor();

    private static final int MIN_PRECEDENCE = 0;

    public static String print(Sentence s, String prefix, PrettyPrinterOptions options) {
        return print(s, prefix, prefix, options, null);
    }

    public static String print(Sentence s, String firstLinePrefix, String additionalLinesPrefix, PrettyPrinterOptions options, BindingList variableBindings) {

        FOPCPrettyPrinterData data = new FOPCPrettyPrinterData();
        if (variableBindings == null) {
            data.variableBindings = new BindingList();
        }
        else {
            data.variableBindings = variableBindings;
        }

        if (options != null) {
            data.options = options;
        }

        data.pushIndent(Math.max(additionalLinesPrefix.length(), firstLinePrefix.length()));

        PPResult r = s.accept(PRETTY_PRINTER_VISITOR, data);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(firstLinePrefix);

        appendWithPrefix(stringBuilder, r.getResultString(), additionalLinesPrefix);

        stringBuilder.append(data.options.getSentenceTerminator());


        return stringBuilder.toString();
    }

    public static String print(Term s, String firstLinePrefix, String additionalLinesPrefix, PrettyPrinterOptions options, BindingList variableBindings) {

        FOPCPrettyPrinterData data = new FOPCPrettyPrinterData();
        if (variableBindings == null) {
            data.variableBindings = new BindingList();
        }
        else {
            data.variableBindings = variableBindings;
        }

        if (options != null) {
            data.options = options;
        }

        data.pushIndent(Math.max(additionalLinesPrefix.length(), firstLinePrefix.length()));

        PPResult r = s.accept(PRETTY_PRINTER_VISITOR, data);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(firstLinePrefix);

        appendWithPrefix(stringBuilder, r.getResultString(), additionalLinesPrefix);

        stringBuilder.append(data.options.getSentenceTerminator());


        return stringBuilder.toString();
    }

    private static void appendWithPrefix(StringBuilder stringBuilder, String resultString, String prefix) {

        if (prefix != null && !prefix.isEmpty() && !resultString.isEmpty()) {

            int index = -1;
            int lastIndex = 0;

            while ((index = resultString.indexOf("\n", index + 1)) != -1) {
                String s = resultString.substring(lastIndex, index + 1);

                if (!s.isEmpty()) {
                    if (lastIndex != 0) {
                        stringBuilder.append(prefix);
                    }
                    stringBuilder.append(s);
                }

                lastIndex = index + 1;
            }

            if (lastIndex != 0) {
                stringBuilder.append(prefix);
            }
            stringBuilder.append(resultString.substring(lastIndex));

        }
        else {
            stringBuilder.append(resultString);
        }
    }

    private static int getMaxLineLength(String s) {
        int index = -1;
        int lastIndex = -1;
        int max = 0;

        while ((index = s.indexOf("\n", index + 1)) != -1) {
            max = Math.max(max, index - lastIndex - 1);
            lastIndex = index;
        }

        max = Math.max(max, s.length() - lastIndex - 1);
        return max;
    }

    public static String spaces(int count) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static class PrettyPrinterVisitor implements SentenceVisitor<PPResult, FOPCPrettyPrinterData>, TermVisitor<PPResult, FOPCPrettyPrinterData> {

        private PrettyPrinterVisitor() {
        }

        public PPResult visitOtherSentence(Sentence otherSentence, FOPCPrettyPrinterData data) {
            return new PPResult(otherSentence.toString(), false, Integer.MAX_VALUE);
        }

        public PPResult visitConnectedSentence(ConnectedSentence sentence, FOPCPrettyPrinterData data) {

            ConnectiveName connective = sentence.getConnective();

            PPResult result = new PPResult();

            int precedence = HandleFOPCstrings.getConnectivePrecedence_static(connective);
            result.setPrecedence(precedence);

            PPResult a = null;
            boolean multiline = false;

            data.pushIndent(connective.name.length());

            if (sentence.getSentenceA() != null) {
                a = sentence.getSentenceA().accept(this, data);
                multiline = a.isMultiline();
            }

            PPResult b = sentence.getSentenceB().accept(this, data);
            multiline = multiline || b.isMultiline();

            StringBuilder stringBuilder = new StringBuilder();

            String prefix = "";
            String prefix2 = "";
            if (multiline) {
                prefix = spaces(connective.name.length());
                prefix2 = spaces(connective.name.length() + 2);
            }

            if (a != null) {
                stringBuilder.append(prefix);

                if (/*a.multiline ||*/a.getPrecedence() > precedence) {
                    stringBuilder.append("(");
                    if (multiline && data.options.isNewLineAfterOpenParathesis()) {
                        stringBuilder.append("\n").append(prefix2);
                    }
                }

                appendWithPrefix(stringBuilder, a.resultString, prefix2);

                if (/*a.multiline ||*/a.getPrecedence() > precedence) {
                    if (multiline) {
                        stringBuilder.append("\n").append(prefix);
                    }
                    stringBuilder.append(")");
                }

                if (multiline) {
                    stringBuilder.append("\n");
                }
                else {
                    stringBuilder.append(" ");
                }
            }

            stringBuilder.append(connective.name);

            if (multiline) {
                stringBuilder.append("\n");
            }
            else {
                stringBuilder.append(" ");
            }

            if (b != null) {
                stringBuilder.append(prefix2);

                if (/*b.multiline ||*/b.getPrecedence() > precedence) {
                    stringBuilder.append("(");
                    if (multiline && data.options.isNewLineAfterOpenParathesis()) {
                        stringBuilder.append("\n").append(prefix2);
                    }
                }

                appendWithPrefix(stringBuilder, b.resultString, prefix2);

                if (/*b.multiline ||*/b.getPrecedence() > precedence) {
                    if (multiline) {
                        stringBuilder.append("\n").append(prefix);
                    }
                    stringBuilder.append(")");
                }
            }

            result.setMultiline(data.options.isMultilineOutputEnabled() && multiline);
            result.setResultString(stringBuilder.toString());

            data.popIndent();

            return result;
        }

        public PPResult visitQuantifiedSentence(QuantifiedSentence sentence, FOPCPrettyPrinterData data) {
            return sentence.body.accept(this, data);
        }

        public PPResult visitClause(Clause clause, FOPCPrettyPrinterData data) {

            PPResult result;


            if (clause.getNegLiteralCount() == 0) {
                if (clause.getPosLiteralCount() == 0) {
                    result = new PPResult("true", false, MIN_PRECEDENCE);
                }
                else {
                    result = prettyPrintLiterals(clause.getPositiveLiterals(), data);
                }
            }
            else if (clause.getPosLiteralCount() == 0) {
                PPResult negResult = prettyPrintLiterals(clause.getNegativeLiterals(), data);
                result = new PPResult(negResult.resultString, negResult.isMultiline(), MIN_PRECEDENCE);

            }
            else {
                if (data.options.isPrintClausesAsImplications()) {
                    PPResult negResult = prettyPrintLiterals(clause.getNegativeLiterals(), data);
                    PPResult posResult = prettyPrintLiterals(clause.getPositiveLiterals(), data);

                    result = new PPResult(negResult.getResultString() + " -> " + posResult.getResultString(), negResult.isMultiline() || posResult.isMultiline(), 1200);
                }
                else {

                    PPResult posResult = prettyPrintLiterals(clause.getPositiveLiterals(), data);

                    data.pushIndent(posResult.getMaximumWidth() + 4);

                    PPResult negResult = prettyPrintLiterals(clause.getNegativeLiterals(), data);

                    data.popIndent();

                    String prefix = spaces(Math.min(posResult.getMaximumWidth() + 4, data.options.getMaximumIndentationAfterImplication()));

                    StringBuilder stringBuilder = new StringBuilder();

                    stringBuilder.append(posResult.getResultString()).append(" :- ");

                    if (negResult.isMultiline() || posResult.isMultiline()) {
                        if (data.options.isNewLineAfterImplication()) {
                            stringBuilder.append("\n").append(prefix);
                        }
                        appendWithPrefix(stringBuilder, negResult.resultString, prefix);
                    }
                    else {
                        stringBuilder.append(negResult.resultString);

                    }

                    result = new PPResult(stringBuilder.toString(), negResult.isMultiline() || posResult.isMultiline(), 1200);
                }
            }

            return result;
        }

        public PPResult visitLiteral(Literal literal, FOPCPrettyPrinterData data) {

            List<? extends Term> terms = literal.getArguments();

            String pred = literal.predicateName.name;

            return prettyPrintTerms(pred, terms, literal.predicateName.printUsingInFixNotation, data);
        }

        public PPResult visitFunction(Function function, FOPCPrettyPrinterData data) {

            return prettyPrintTerms(function.functionName.name, function.getArguments(), function.functionName.printUsingInFixNotation, data);
        }

        public PPResult visitConsCell(ConsCell consCell, FOPCPrettyPrinterData data) {
            return prettyPrintConCell(consCell, data);
        }

        public PPResult visitVariable(Variable variable, FOPCPrettyPrinterData data) {

            PPResult result;

            if (data.variableBindings != null) {
                Term binding = data.variableBindings.getMapping(variable);

                if (binding != null) {
                    // We would like to allow binding to anything, but I don't think
                    // that is possible (or at least easy) since the StringConstant
                    // will always be quoted if we do an except on the constant.

                    // For now, if the term is a string constant, assume we are the
                    // onces who created the binding...
                    if (binding instanceof StringConstant) {
                        StringConstant stringConstant = (StringConstant) binding;
                        result = new PPResult(stringConstant.getBareName(), false, MIN_PRECEDENCE);
                    }
                    else {
                        result = binding.accept(this, data);
                    }
                }
                else if (data.options.isRenameVariables()) {

                    StringConstant variableName = data.getNextVariableName(variable);

                    data.variableBindings.addBinding(variable, variableName);
                    result = new PPResult(variableName.getBareName(), false, MIN_PRECEDENCE);
                }
                else {
                    result = new PPResult(variable.getName(), false, MIN_PRECEDENCE);
                }
            }
            else {
                return new PPResult(variable.name, false, MIN_PRECEDENCE);
            }

            return result;


        }

        public PPResult visitSentenceAsTerm(SentenceAsTerm sentenceAsTerm, FOPCPrettyPrinterData data) {

            Sentence s = sentenceAsTerm.sentence;

            return s.accept(this, data);
        }

        public PPResult visitLiteralAsTerm(LiteralAsTerm literalAsTerm, FOPCPrettyPrinterData data) {

            return literalAsTerm.itemBeingWrapped.accept(this, data);
        }

        public PPResult visitListAsTerm(ListAsTerm listAsTerm, FOPCPrettyPrinterData data) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (Term term : listAsTerm.objects) {
                PPResult r = term.accept(this, data);
                stringBuilder.append(r.resultString);
            }
            stringBuilder.append("]");
            return null;
        }

        public PPResult visitNumericConstant(NumericConstant numericConstant, FOPCPrettyPrinterData data) {
            return new PPResult(numericConstant.getName(), false, MIN_PRECEDENCE);
        }

        public PPResult visitStringConstant(StringConstant stringConstant, FOPCPrettyPrinterData data) {
            return new PPResult(stringConstant.toString(), false, MIN_PRECEDENCE);
        }

        public PPResult visitOtherTerm(Term term, FOPCPrettyPrinterData data) {
            return new PPResult(term.toString(), false, MIN_PRECEDENCE);
        }

        private PPResult prettyPrintLiterals(List<Literal> literals, FOPCPrettyPrinterData data) {

            StringBuilder stringBuilder = new StringBuilder();

            boolean multiline = false;

            if (literals.size() > 0) {

                List<PPResult> list = new ArrayList<>(literals.size());
                int maxLineWidth = 0;

                int totalWidth = 0;

                for (Literal literal : literals) {
                    PPResult tpp = literal.accept(this, data);
                    list.add(tpp);

                    multiline = tpp.isMultiline() || multiline;
                    maxLineWidth = Math.max(maxLineWidth, tpp.getMaximumWidth());

                    totalWidth += tpp.getMaximumWidth();
                }

                multiline = data.options.isMultilineOutputEnabled() && (multiline || totalWidth > data.options.getMaximumLineWidth() - data.getCurrentIndentation());

                String prefix = "";

                int maximumWidth = data.options.getMaximumLineWidth() - data.getCurrentIndentation();

                int currentWidth = 0;
                int termsOnLine = 0;

                boolean lastWasMultiline = false;

                for (int i = 0; i < list.size(); i++) {
                    PPResult tpp = list.get(i);
                    if (i > 0) {
                        stringBuilder.append(", ");
                        if (data.options.isMultilineOutputEnabled()) {
                            if (lastWasMultiline || tpp.multiline) {
                                stringBuilder.append("\n").append(prefix);
                                currentWidth = 0;
                                termsOnLine = 0;
                                lastWasMultiline = tpp.multiline;
                                multiline = true;
                            }
                            else {
                                if (currentWidth + tpp.getMaximumWidth() >= maximumWidth || (data.options.getMaximumLiteralsPerLine() > 0 && termsOnLine >= data.options.getMaximumLiteralsPerLine())) {
                                    stringBuilder.append("\n").append(prefix);
                                    currentWidth = 0;
                                    termsOnLine = 0;
                                    multiline = true;
                                }
                                lastWasMultiline = false;
                            }
                        }
                    }

                    appendWithPrefix(stringBuilder, tpp.resultString, prefix);

                    // We really should be adding the maximum width of the last line
                    // of the PPResult string.  However, if we are printing multiline
                    // statements, we will automatically add a
                    currentWidth += tpp.getMaximumWidth();
                    termsOnLine++;
                }
            }


            return new PPResult(stringBuilder.toString(), multiline, 1000);
        }

        private PPResult prettyPrintConCell(ConsCell consCell, FOPCPrettyPrinterData data) {

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("[");

            boolean multiline = false;

            data.pushIndent(1);

            List<PPResult> list = new ArrayList<>();
            int maxLineWidth = 0;

            int totalWidth = 0;

            int cellCount = 0;

            Term currentCell = consCell;

            boolean printBarForLastTerm = false;

            while (currentCell != null && currentCell != consCell.getStringHandler().getNil()) {

                PPResult tpp;

                if (data.options.getMaximumConsCells() != -1 && cellCount >= data.options.getMaximumConsCells()) {
                    tpp = new PPResult("...", false, 1000);
                    currentCell = null;
                }
                else if (currentCell instanceof ConsCell) {
                    ConsCell aCell = (ConsCell) currentCell;
                    Term head = aCell.car();
                    currentCell = aCell.getArgument(1);

                    tpp = head.accept(this, data);
                }
                else {
                    // Hmm...the currentCell isn't a consCell.  Probably a variable...
                    tpp = currentCell.accept(this, data);
                    currentCell = null;
                    printBarForLastTerm = true;
                }

                list.add(tpp);

                multiline = tpp.isMultiline() || multiline;
                maxLineWidth = Math.max(maxLineWidth, tpp.getMaximumWidth());

                totalWidth += tpp.getMaximumWidth();

                cellCount++;

            }

            multiline = data.options.isMultilineOutputEnabled() && (multiline || totalWidth > data.options.getMaximumLineWidth() - data.getCurrentIndentation());

            String prefix = spaces(1);

            if (multiline && data.options.isNewLineAfterOpenParathesis()) {
                stringBuilder.append("\n").append(prefix);
            }

            int maximumWidth = data.options.getMaximumLineWidth() - data.getCurrentIndentation();

            int currentWidth = 0;
            int termsOnLine = 0;

            boolean lastWasMultiline = false;

            int maxTermsPerLine = data.options.getMaximumTermsPerLine();


            for (int i = 0; i < list.size(); i++) {
                PPResult tpp = list.get(i);
                if (i > 0) {
                    
                    if (printBarForLastTerm && i == list.size() - 1) {
                        stringBuilder.append("|");
                    }
                    else {
                        stringBuilder.append(", ");
                    }

                    if (data.options.isMultilineOutputEnabled()) {
                        if (lastWasMultiline || tpp.multiline) {
                            stringBuilder.append("\n").append(prefix);
                            currentWidth = 0;
                            termsOnLine = 0;
                            lastWasMultiline = tpp.multiline;
                            multiline = true;
                        }
                        else {
                            if (currentWidth + tpp.getMaximumWidth() >= maximumWidth || (maxTermsPerLine > 0 && termsOnLine >= maxTermsPerLine)) {
                                stringBuilder.append("\n").append(prefix);
                                currentWidth = 0;
                                termsOnLine = 0;
                                multiline = true;
                            }
                            lastWasMultiline = false;
                        }
                    }
                }

                appendWithPrefix(stringBuilder, tpp.resultString, prefix);

                // We really should be adding the maximum width of the last line
                // of the PPResult string.  However, if we are printing multiline
                // statements, we will automatically add a
                currentWidth += tpp.getMaximumWidth();
                termsOnLine++;
            }

            if (multiline && data.options.isAlignParathesis()) {
                stringBuilder.append("\n").append(spaces(1));
            }


            stringBuilder.append("]");

            data.popIndent();

            return new PPResult(stringBuilder.toString(), multiline, MIN_PRECEDENCE);

        }

        private PPResult prettyPrintTerms(String pred, List<? extends Term> terms, boolean infix, FOPCPrettyPrinterData data) {

            StringBuilder stringBuilder = new StringBuilder();

            if (!infix) {
                stringBuilder.append(pred);
            }

            int maxTermsPerLine = data.options.getMaximumTermsPerLine();

            if ("\\+".equals(pred)) {
                // Special handling of negation.  We should really generalize this
                // for other things like call, forAll, etc.
                maxTermsPerLine = data.options.getMaximumLiteralsPerLine();
            }

            boolean multiline = false;


            int precedence = infix ? HandleFOPCstrings.getOperatorPrecedence_static(pred) : 999;

            if (terms != null && !terms.isEmpty()) {

                if (!infix) {
                    stringBuilder.append("(");

                    data.pushIndent(pred.length() + 1);
                }


                List<PPResult> list = new ArrayList<>(terms.size());
                int maxLineWidth = 0;

                int totalWidth = 0;

                for (int i = 0; i < terms.size(); i++) {

                    Term term = terms.get(i);

                    if (infix && i == 1) {
                        // Insert the infix operator...
                        PPResult tpp = new PPResult(" " + pred + " ", false, MIN_PRECEDENCE);
                        list.add(tpp);

                        totalWidth += tpp.getMaximumWidth();
                    }

                    PPResult tpp = term.accept(this, data);
                    //System.out.println(tpp);
                    multiline = !infix && (tpp.isMultiline() || multiline);

                    if (precedence < tpp.precedence) {
                        StringBuilder infixSB = new StringBuilder();
                        infixSB.append("(");
                        if (tpp.isMultiline() && data.options.isNewLineAfterOpenParathesis()) {
                            infixSB.append("\n ");
                        }
                        appendWithPrefix(infixSB, tpp.resultString, " ");
                        if (tpp.isMultiline() && data.options.isAlignParathesis()) {
                            infixSB.append("\n");
                        }
                        infixSB.append(")");
                        tpp = new PPResult(infixSB.toString(), tpp.multiline, MIN_PRECEDENCE);
                    }

                    list.add(tpp);

                    maxLineWidth = Math.max(maxLineWidth, tpp.getMaximumWidth());

                    totalWidth += tpp.getMaximumWidth();
                }

                multiline = data.options.isMultilineOutputEnabled() && (multiline || totalWidth > data.options.getMaximumLineWidth() - data.getCurrentIndentation());

                String prefix = spaces(pred.length() + 1);

                if (multiline && data.options.isNewLineAfterOpenParathesis()) {
                    stringBuilder.append("\n").append(prefix);
                }

                int maximumWidth = data.options.getMaximumLineWidth() - data.getCurrentIndentation();

                int currentWidth = 0;
                int termsOnLine = 0;

                boolean lastWasMultiline = false;

                for (int i = 0; i < list.size(); i++) {
                    PPResult tpp = list.get(i);
                    if (i > 0) {
                        if (!infix) {
                            stringBuilder.append(", ");
                        }
                        if (data.options.isMultilineOutputEnabled()) {
                            if (lastWasMultiline || tpp.multiline) {
                                stringBuilder.append("\n").append(prefix);
                                currentWidth = 0;
                                termsOnLine = 0;
                                lastWasMultiline = tpp.multiline;
                                multiline = true;
                            }
                            else {
                                if (!infix && (currentWidth + tpp.getMaximumWidth() >= maximumWidth || (maxTermsPerLine > 0 && termsOnLine >= maxTermsPerLine))) {
                                    stringBuilder.append("\n").append(prefix);
                                    currentWidth = 0;
                                    termsOnLine = 0;
                                    multiline = true;
                                }
                                lastWasMultiline = false;
                            }
                        }
                    }

                    appendWithPrefix(stringBuilder, tpp.resultString, prefix);

                    currentWidth += tpp.getMaximumWidth();
                    termsOnLine++;
                }

                if (multiline && data.options.isAlignParathesis()) {
                    stringBuilder.append("\n").append(spaces(pred.length()));
                }


                if (!infix) {
                    stringBuilder.append(")");
                    data.popIndent();
                }


            }

            return new PPResult(stringBuilder.toString(), multiline, precedence);
        }
    }

    public static class FOPCPrettyPrinterData {

        Queue<PrecedenceInfo> precedenceInfo = new LinkedList<>();

        PrettyPrinterOptions options = new PrettyPrinterOptions();

        BindingList variableBindings;

        int renamedVariableIndex = 0;

        FOPCPrettyPrinterData() {
            pushPrecedence(0);
        }

        void pushIndent(int additionalIndentation) {

            String newPrefix = getPrefix() + spaces(additionalIndentation);

            precedenceInfo.add(new PrecedenceInfo(newPrefix));
        }

        final void pushPrecedence(int precedence) {
            precedenceInfo.add(new PrecedenceInfo(precedence));
        }

        PrecedenceInfo getCurrentPrecedence() {
            return precedenceInfo.peek();
        }

        void popIndent() {
            precedenceInfo.remove();
        }

        public String getPrefix() {
            return getCurrentPrecedence().prefix;
        }

        int getCurrentIndentation() {
            return getCurrentPrecedence().currentIndentation;
        }

        StringConstant getNextVariableName(Variable variable) {
            StringConstant variableName = variable.getStringHandler().getAlphabeticalVariableName(renamedVariableIndex++);

            while (isNameUsed(variableName)) {
                variableName = variable.getStringHandler().getAlphabeticalVariableName(renamedVariableIndex++);
            }

            return variableName;
        }

        private boolean isNameUsed(StringConstant name) {
            boolean result = false;

            if (variableBindings != null) {
                for (Term term : variableBindings.theta.values()) {
                    if (term.equals(name)) {
                        result = true;
                        break;
                    }
                }
            }

            return result;
        }
    }

    public static class PrecedenceInfo {

        String prefix;

        int currentIndentation;

        PrecedenceInfo(String prefix) {
            this.prefix = prefix;
        }

        PrecedenceInfo(int currentIndentation) {
            this.currentIndentation = currentIndentation;
        }
    }

    public static final class PPResult {

        private String resultString;

        private boolean multiline;

        private int precedence;

        private int maximumWidth;

        private PPResult(String resultString, boolean multiline, int precedence) {
            setResultString(resultString);
            this.multiline = multiline;
            this.precedence = precedence;
        }

        private PPResult() {
        }

        @Override
        public String toString() {
            return "PPResult{" + "resultString=" + getResultString() + ", multi=" + isMultiline() + ", prec=" + getPrecedence() + ", maxWidth=" + getMaximumWidth() + '}';
        }

        String getResultString() {
            return resultString;
        }

        void setResultString(String resultString) {
            this.resultString = resultString;
            setMaximumWidth(getMaxLineLength(resultString));
        }

        boolean isMultiline() {
            return multiline;
        }

        void setMultiline(boolean multiline) {
            this.multiline = multiline;
        }

        public int getPrecedence() {
            return precedence;
        }

        public void setPrecedence(int precedence) {
            this.precedence = precedence;
        }

        int getMaximumWidth() {
            return maximumWidth;
        }

        void setMaximumWidth(int maximumWidth) {
            this.maximumWidth = maximumWidth;
        }
    }

    private PrettyPrinter() {
    }
}
