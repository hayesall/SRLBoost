package edu.wisc.cs.will.ILP;

import edu.wisc.cs.will.FOPC.RelevanceStrength;

/*
 * @author twalker
 */
public interface RelevantInformation {
    RelevanceStrength getRelevanceStrength();
    boolean isRelevanceFromPositiveExample();
    void setRelevanceFromPositiveExample(boolean positive);

    RelevantInformation getGeneralizeRelevantInformation();

    RelevantInformation copy();

    boolean isValidAdvice(AdviceProcessor ap);

    boolean subsumes(RelevantInformation that);

}
