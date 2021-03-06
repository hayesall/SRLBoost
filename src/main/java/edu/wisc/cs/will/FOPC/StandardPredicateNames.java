/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wisc.cs.will.FOPC;

import java.util.HashSet;
import java.util.Set;

/*
 * @author twalker
 */
public class StandardPredicateNames { // A few FUNCTION names also appear here; for instance, sometimes we need to convert a literal to a function.

    final PredicateName dateToString;

    final PredicateName dateToUTCstring;

    final PredicateName dateToMRstring;

    final PredicateName convertDateToLong;

    final PredicateName isa_variable; // NOTE: the same stringHandler needs to be used throughout so the same strings get mapped to the same PredicateName instances.

    public final PredicateName var;

    final PredicateName isa_constant; // Also note: this mapping is case-independent.

    final PredicateName atomic;

    final PredicateName isa_numericConstant;

    public final PredicateName number;

    final PredicateName isaInteger;

    final PredicateName isaFloat;

    final PredicateName isaDouble;

    final PredicateName isa_stringConstant;

    final PredicateName atom;

    final PredicateName nonvar;

    public final PredicateName list;

    final PredicateName compound;

    public final PredicateName is;

    final PredicateName halt;

    public final PredicateName sort;

    final FunctionName pullOutNthArgFunction;

    public final PredicateName print;

    public final PredicateName write; // A synonym for 'print.'

    public final PredicateName waitHere;

    public final PredicateName wait; // A synonym for 'waitHere.'

    final PredicateName readEvalPrint;

    final PredicateName findAllCollector;

    final PredicateName allCollector;

    final PredicateName bagOfCollector;

    final PredicateName setOfCollector;

    public final PredicateName first;

    public final PredicateName rest;

    public final PredicateName push;

    public final PredicateName remove;

    public final PredicateName reverse;

    public final PredicateName position;

    public final PredicateName length;

    final PredicateName nth;

    final PredicateName nthPlus1;

    // These are also defined in a library.  Note can use fast version via functions, eg:  ?X is union(?Y, ?Z).
    // Libraries override (I [JWS] believe).
    final PredicateName appendFast;
    final PredicateName intersectionFast;
    final PredicateName unionFast;

    final PredicateName listsEquivalent;

    final PredicateName addListOfNumbers;

    final PredicateName multListOfNumbers;

    final PredicateName countProofsCollector;

    final PredicateName countUniqueBindingsCollector;

    final PredicateName assertName;

    final PredicateName assertifnotName;

    final PredicateName assertifunknownName;

    final PredicateName atomConcat;

    final PredicateName atomLength;

    final PredicateName atomChars;

    final PredicateName setCounter,  setCounterB,  setCounterC,  setCounterD,  setCounterE;
    final PredicateName incrCounter, incrCounterB, incrCounterC, incrCounterD, incrCounterE;

    final PredicateName tokenizeString;

    public final PredicateName implicit_call;

    public final PredicateName trueName;

    public final PredicateName falseName;

    public final PredicateName fail;

    public final PredicateName repeat;

    public final PredicateName once;

    public final PredicateName call;

    public final PredicateName cut;

    public final PredicateName cutMarker;

    public final PredicateName findAll;

    public final PredicateName all;

    public final PredicateName setOf;

    public final PredicateName bagOf;

    public final PredicateName countProofs;

    public final PredicateName countUniqueBindings;

    public final PredicateName then;

    public final PredicateName negationByFailure;

    public final FunctionName negationByFailureAsFunction;

    public final PredicateName spy;

    public final PredicateName nospy;

    public final PredicateName nospyall;

    public final PredicateName trace;

    public final PredicateName notrace;

    public final PredicateName retract;

    public final PredicateName retractall;

    final PredicateName createUniqueStringConstant;

    public final PredicateName consCell;

    final FunctionName plusFunction;  // TODO build in a hash table of synonyms?

    final FunctionName minusFunction;

    final FunctionName timesFunction;

    final FunctionName divideFunction;

    final FunctionName intDivFunction;

    final FunctionName intFunction;

    final FunctionName modFunction;

    final FunctionName minFunction;

    final FunctionName maxFunction;

    final FunctionName absFunction;

    final FunctionName sinFunction;

    final FunctionName cosFunction;

    final FunctionName tanFunction;

    final FunctionName sinhFunction;

    final FunctionName coshFunction;

    final FunctionName tanhFunction;

    final FunctionName asinFunction;

    final FunctionName acosFunction;

    final FunctionName atanFunction;

    final FunctionName atan2Function; // From Java: Returns the angle theta from the conversion of rectangular coordinates (x, y) to polar coordinates (r, theta).

    final FunctionName logFunction;

    final FunctionName expFunction;

    final FunctionName sqrtFunction;

    final FunctionName sqrtSafeFunction;

    final FunctionName sqrtAbsFunction;

    final FunctionName powFunction;

    final FunctionName starStarFunction;

    final FunctionName randomFunction;  // A number uniformly drawn from [0,1).  Uses the 'random' used elsewhere in this code so that runs can be repeated deterministically.

    final FunctionName ceilFunction; // From Java: Returns the smallest (closest to negative infinity) double value that is greater than or equal to the argument and is equal to a mathematical integer.

    final FunctionName floorFunction;

    final FunctionName roundFunction;

    final FunctionName signFunction;

    final FunctionName hypotFunction; // From Java: Returns sqrt(x^2 +y^2) without intermediate overflow or underflow.

    final FunctionName toDegreesFunction; // Since in Java's Math class, might as well include them.

    final FunctionName toRadiansFunction;

    final FunctionName lengthFunction;   // Since this returns a number, do here as well as in DoBuiltInListProcessing.

    final FunctionName positionFunction; // Since this returns a number, do here as well as in DoBuiltInListProcessing.

    final FunctionName minus2Function;

    final PredicateName unify2;

    public final PredicateName unify;

    final PredicateName ununifiable;

    final PredicateName ununifiable2;

    public final PredicateName equal;

    final PredicateName equal2;

    final PredicateName diff;

    final PredicateName notEqual;

    public final PredicateName ground;

    final PredicateName copyTerm;

    public final PredicateName gt;  // Prefix versions of these comparators haven't been provided.

    final PredicateName gt2;

    public final PredicateName lt;

    final PredicateName lt2;

    final PredicateName gte;   // gte = greater-than-or-equal

    final PredicateName gte2;

    public final PredicateName lte;   // lte = less-than-or-equal

    final PredicateName lte2;

    final PredicateName lte3;

    final PredicateName equalNumbers;  // Equal numbers.

    final PredicateName notEqualNumbers; // Not equal numbers.

    final PredicateName equalDotDot;

    public final Set<PredicateName> buildinPredicates;

    StandardPredicateNames(HandleFOPCstrings stringHandler) {


        boolean hold = stringHandler.cleanFunctionAndPredicateNames;
        stringHandler.cleanFunctionAndPredicateNames = false;

        dateToString      = stringHandler.getPredicateName("dateToString");
        dateToUTCstring   = stringHandler.getPredicateName("dateToUTCstring");
        dateToMRstring    = stringHandler.getPredicateName("dateToMRstring");
        convertDateToLong = stringHandler.getPredicateName("convertDateToLong");
        isa_variable = stringHandler.getPredicateName("isa_variable"); // NOTE: the same stringHandler needs to be used throughout so the same strings get mapped to the same PredicateName instances.
        var = stringHandler.getPredicateName("var");
        isa_constant = stringHandler.getPredicateName("isa_constant"); // Also note: this mapping is case-independent.
        atomic = stringHandler.getPredicateName("atomic");
        isa_numericConstant = stringHandler.getPredicateName("isa_numericConstant");
        number = stringHandler.getPredicateName("number");
        isaInteger = stringHandler.getPredicateName("integer");
        isaFloat = stringHandler.getPredicateName("float");
        isaDouble = stringHandler.getPredicateName("double");
        isa_stringConstant = stringHandler.getPredicateName("isa_stringConstant");
        atom = stringHandler.getPredicateName("atom");
        nonvar = stringHandler.getPredicateName("nonvar");
        list = stringHandler.getPredicateName("list");
        compound = stringHandler.getPredicateName("compound");
        is = stringHandler.getPredicateName("is");
        halt = stringHandler.getPredicateName("halt");
        unify = stringHandler.getPredicateName("unify");
        unify2 = stringHandler.getPredicateName("=");
        ununifiable = stringHandler.getPredicateName("notUnify");
        ununifiable2 = stringHandler.getPredicateName("\\=");
        equal = stringHandler.getPredicateName("equal");
        equal2 = stringHandler.getPredicateName("==");
        diff = stringHandler.getPredicateName("diff");
        notEqual = stringHandler.getPredicateName("\\==");
        // not                 = stringHandler.getPredicateName("\\+");  // Note that there is also negationByFailure
        ground = stringHandler.getPredicateName("ground");
        copyTerm = stringHandler.getPredicateName("copy_term");
        gt = stringHandler.getPredicateName(">");  // Prefix versions of these comparators haven't been provided.
        gt2 = stringHandler.getPredicateName("gt");
        lt = stringHandler.getPredicateName("<");
        lt2 = stringHandler.getPredicateName("lt");
        gte = stringHandler.getPredicateName(">=");   // gte = greater-than-or-equal
        gte2 = stringHandler.getPredicateName("gte");
        lte = stringHandler.getPredicateName("=<");   // lte = less-than-or-equal
        lte2 = stringHandler.getPredicateName("<=");
        lte3 = stringHandler.getPredicateName("lte");
        equalNumbers = stringHandler.getPredicateName("=:=");  // Equal numbers.
        notEqualNumbers = stringHandler.getPredicateName("=\\="); // Not equal numbers.
        equalDotDot = stringHandler.getPredicateName("=..");
        print = stringHandler.getPredicateName("print");
        write = stringHandler.getPredicateName("write"); // A synonym for 'print.'
        waitHere = stringHandler.getPredicateName("waitHere");
        wait = stringHandler.getPredicateName("wait"); // A synonym for 'waitHere.'
        readEvalPrint = stringHandler.getPredicateName("readEvalPrintCollector");
        findAllCollector = stringHandler.getPredicateName("findAllCollector");
        allCollector = stringHandler.getPredicateName("allCollector");
        bagOfCollector = stringHandler.getPredicateName("bagofCollector");
        setOfCollector = stringHandler.getPredicateName("setofCollector");
        first = stringHandler.getPredicateName("first");
        rest = stringHandler.getPredicateName("rest");
        push = stringHandler.getPredicateName("push");
        remove = stringHandler.getPredicateName("remove");
        reverse = stringHandler.getPredicateName("reverse");
        position = stringHandler.getPredicateName("position");
        length = stringHandler.getPredicateName("length");
        nth = stringHandler.getPredicateName("nth");
        nthPlus1 = stringHandler.getPredicateName("nthPlus1");
        appendFast       = stringHandler.getPredicateName("append"); // Now defined in a Prolog library.  These versions are 'Fast' and don't do full unification (which especially matters for Union and Intersection).
        intersectionFast = stringHandler.getPredicateName("intersection");
        unionFast        = stringHandler.getPredicateName("union");
        listsEquivalent = stringHandler.getPredicateName("listsEquivalent");
        addListOfNumbers = stringHandler.getPredicateName("addListOfNumbers");
        multListOfNumbers = stringHandler.getPredicateName("multiplyListOfNumbers");
        countProofsCollector = stringHandler.getPredicateName("countProofsCollector");
        countUniqueBindingsCollector = stringHandler.getPredicateName("countUniqueBindingsCollector");
        assertName = stringHandler.getPredicateName("assert");
        assertifnotName = stringHandler.getPredicateName("assertifnot");
        assertifunknownName = stringHandler.getPredicateName("assertifunknown");
        atomConcat = stringHandler.getPredicateName("atom_concat"); // This is a standard name in Prolog, hence the underscore.
        atomLength = stringHandler.getPredicateName("atom_length"); // This is a standard name in Prolog, hence the underscore.
        atomChars  = stringHandler.getPredicateName("atom_chars");  // This is a standard name in Prolog, hence the underscore.
        setCounter   = stringHandler.getPredicateName("setCounter");
        setCounterB  = stringHandler.getPredicateName("setCounterB");
        setCounterC  = stringHandler.getPredicateName("setCounterC");
        setCounterD  = stringHandler.getPredicateName("setCounterD");
        setCounterE  = stringHandler.getPredicateName("setCounterE");
        incrCounter  = stringHandler.getPredicateName("incrCounter");
        incrCounterB = stringHandler.getPredicateName("incrCounterB");
        incrCounterC = stringHandler.getPredicateName("incrCounterC");
        incrCounterD = stringHandler.getPredicateName("incrCounterD");
        incrCounterE = stringHandler.getPredicateName("incrCounterE");
        tokenizeString  = stringHandler.getPredicateName("tokenizeString"); 
        implicit_call = stringHandler.getPredicateName("implicit_call");
        trueName = stringHandler.getPredicateName("true");
        falseName = stringHandler.getPredicateName("false");
        fail = stringHandler.getPredicateName("fail");
        repeat = stringHandler.getPredicateName("repeat");
        once = stringHandler.getPredicateName("once");
        call = stringHandler.getPredicateName("call");
        cut = stringHandler.getPredicateName("!");
        cutMarker = stringHandler.getPredicateName("cutMarker");
        findAll = stringHandler.getPredicateName("findAll");
        all = stringHandler.getPredicateName("all");
        setOf = stringHandler.getPredicateName("setOf");
        bagOf = stringHandler.getPredicateName("bagOf");
        countProofs = stringHandler.getPredicateName("countProofs");
        countUniqueBindings = stringHandler.getPredicateName("countUniqueBindings");
        then = stringHandler.getPredicateName("then");
        negationByFailure = stringHandler.getPredicateName("\\+");
        createUniqueStringConstant = stringHandler.getPredicateName("createUniqueStringConstant");
        sort = stringHandler.getPredicateName("sort");

        negationByFailureAsFunction = stringHandler.getFunctionName("\\+");
        plusFunction = stringHandler.getFunctionName("+"); // If another 'in-fix' operator is added to this list, need to edit FileParser.java.
        minusFunction = stringHandler.getFunctionName("-");
        minus2Function = stringHandler.getFunctionName("minus");
        timesFunction = stringHandler.getFunctionName("*");
        divideFunction = stringHandler.getFunctionName("/");   // Note that in essence 'true' division, rather than integer division, is used.
        intDivFunction = stringHandler.getFunctionName("/@"); // In ISO Prolog, this is '//' but that is a comment indicator to us.
        starStarFunction = stringHandler.getFunctionName("**");

        intFunction = stringHandler.getFunctionName("integer"); // Allow the user to force integer results.
        modFunction = stringHandler.getFunctionName("mod"); // Use Java's definition of mod.  Don't use a single-character symbol due to confusion between Java and Prolog.
        minFunction = stringHandler.getFunctionName("min");
        maxFunction = stringHandler.getFunctionName("max");
        absFunction = stringHandler.getFunctionName("abs");
        sinFunction = stringHandler.getFunctionName("sin");
        cosFunction = stringHandler.getFunctionName("cos");
        tanFunction = stringHandler.getFunctionName("tan");
        sinhFunction = stringHandler.getFunctionName("sinh");
        coshFunction = stringHandler.getFunctionName("cosh");
        tanhFunction = stringHandler.getFunctionName("tanh");
        asinFunction = stringHandler.getFunctionName("asin");
        acosFunction = stringHandler.getFunctionName("acos");
        atanFunction = stringHandler.getFunctionName("atan");
        atan2Function = stringHandler.getFunctionName("atan2");
        logFunction = stringHandler.getFunctionName("log");
        expFunction = stringHandler.getFunctionName("exp");
        powFunction = stringHandler.getFunctionName("pow");
        sqrtFunction = stringHandler.getFunctionName("sqrt");
        sqrtSafeFunction = stringHandler.getFunctionName("sqrtSafe");
        sqrtAbsFunction = stringHandler.getFunctionName("sqrtAbs");
        randomFunction = stringHandler.getFunctionName("random");
        ceilFunction = stringHandler.getFunctionName("ceiling"); // Also use 'ceil' since that is Java's name.
        floorFunction = stringHandler.getFunctionName("floor");
        roundFunction = stringHandler.getFunctionName("round");
        signFunction = stringHandler.getFunctionName("sign");
        hypotFunction = stringHandler.getFunctionName("hypot");
        toDegreesFunction = stringHandler.getFunctionName("toDegrees");
        toRadiansFunction = stringHandler.getFunctionName("toRadians");
        lengthFunction = stringHandler.getFunctionName("length"); // Explicitly list those list-processing functions that return numbers.
        positionFunction = stringHandler.getFunctionName("position");
        FunctionName isFunction = stringHandler.getFunctionName("is");
        FunctionName unifyFunction = stringHandler.getFunctionName("unify");
        FunctionName unify2Function = stringHandler.getFunctionName("=");
        FunctionName ununifiableFunction = stringHandler.getFunctionName("notUnify");
        FunctionName ununifiable2Function = stringHandler.getFunctionName("\\=");
        FunctionName equalFunction = stringHandler.getFunctionName("equal");
        FunctionName equal2Function = stringHandler.getFunctionName("==");
        FunctionName notEqualFunction = stringHandler.getFunctionName("\\==");
        // Prefix versions of these comparators haven't been provided.
        FunctionName gtFunction = stringHandler.getFunctionName(">");  // Prefix versions of these comparators haven't been provided.
        FunctionName gt2Function = stringHandler.getFunctionName("gt");
        FunctionName ltFunction = stringHandler.getFunctionName("<");
        FunctionName lt2Function = stringHandler.getFunctionName("lt");
        // gte = greater-than-or-equal
        FunctionName gteFunction = stringHandler.getFunctionName(">=");   // gte = greater-than-or-equal
        FunctionName gte2Function = stringHandler.getFunctionName("gte");
        // lte = less-than-or-equal
        FunctionName lteFunction = stringHandler.getFunctionName("=<");   // lte = less-than-or-equal
        FunctionName lte2Function = stringHandler.getFunctionName("<=");
        FunctionName lte3Function = stringHandler.getFunctionName("lte");
        // Equal numbers.
        FunctionName equalNumbersFunction = stringHandler.getFunctionName("=:=");  // Equal numbers.
        // Not equal numbers.
        FunctionName notEqualNumbersFunction = stringHandler.getFunctionName("=\\="); // Not equal numbers.
        FunctionName equalDotDotFunction = stringHandler.getFunctionName("=..");
        pullOutNthArgFunction = stringHandler.getFunctionName("pullOutNthArg");

        spy = stringHandler.getPredicateName("spy");
        nospy = stringHandler.getPredicateName("nospy");
        nospyall = stringHandler.getPredicateName("nospyall");
        trace = stringHandler.getPredicateName("trace");
        notrace = stringHandler.getPredicateName("notrace");

        retract = stringHandler.getPredicateName("retract");
        retractall = stringHandler.getPredicateName("retractall");

        consCell = stringHandler.getPredicateName("consCell");


        is.printUsingInFixNotation = true;
        unify2.printUsingInFixNotation = true;
        ununifiable2.printUsingInFixNotation = true;
        equal2.printUsingInFixNotation = true;
        notEqual.printUsingInFixNotation = true;
        gt.printUsingInFixNotation = true;
        lt.printUsingInFixNotation = true;
        gte.printUsingInFixNotation = true;
        lte.printUsingInFixNotation = true;
        lte2.printUsingInFixNotation = true;
        equalNumbers.printUsingInFixNotation = true;
        notEqualNumbers.printUsingInFixNotation = true;
        equalDotDot.printUsingInFixNotation = true;

        isFunction.printUsingInFixNotation = true;
        plusFunction.printUsingInFixNotation = true;
        minusFunction.printUsingInFixNotation = true;
        timesFunction.printUsingInFixNotation = true;
        divideFunction.printUsingInFixNotation = true;
        intDivFunction.printUsingInFixNotation = true;
        starStarFunction.printUsingInFixNotation = true;
        unify2Function.printUsingInFixNotation = true;
        ununifiable2Function.printUsingInFixNotation = true;
        equal2Function.printUsingInFixNotation = true;
        notEqualFunction.printUsingInFixNotation = true;
        gtFunction.printUsingInFixNotation = true;
        ltFunction.printUsingInFixNotation = true;
        gteFunction.printUsingInFixNotation = true;
        lteFunction.printUsingInFixNotation = true;
        lte2Function.printUsingInFixNotation = true;
        equalNumbersFunction.printUsingInFixNotation = true;
        notEqualNumbersFunction.printUsingInFixNotation = true;
        equalDotDotFunction.printUsingInFixNotation = true;

        call.setContainsCallable(1);
        negationByFailure.setContainsCallable(1);
        once.setContainsCallable(1);

        buildinPredicates = new HashSet<>(32);
        buildinPredicates.add(trueName);
        buildinPredicates.add(falseName);
        buildinPredicates.add(fail);
        buildinPredicates.add(repeat);
        buildinPredicates.add(once);
        buildinPredicates.add(call);
        buildinPredicates.add(implicit_call);
        buildinPredicates.add(cut);
        buildinPredicates.add(cutMarker);
        buildinPredicates.add(findAll);
        buildinPredicates.add(all);
        buildinPredicates.add(setOf);
        buildinPredicates.add(bagOf);
        buildinPredicates.add(countProofs);
        buildinPredicates.add(countUniqueBindings);
        buildinPredicates.add(then);
        buildinPredicates.add(negationByFailure);

        buildinPredicates.add(spy);
        buildinPredicates.add(nospy);
        buildinPredicates.add(nospyall);
        buildinPredicates.add(trace);
        buildinPredicates.add(notrace);

        buildinPredicates.add(retract);
        buildinPredicates.add(retractall);

        stringHandler.cleanFunctionAndPredicateNames = hold;
    }
}
