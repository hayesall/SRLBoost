package edu.wisc.cs.will.FOPC_MLN_ILP_Parser;

import edu.wisc.cs.will.Utils.Utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

/*
 * @author shavlik
 *
 * The built-in StreamReader doens't support more than one pushBack.
 * This code extends it to handle K pushbacks.
 *  
 * Essentially a window of the last K items is maintained, and within that window pushing and popping is supported.
 * 
 * If some complications arise (like the need for doingSuperCall): convert to a WRAPPER instead of an EXTENSION to StreamTokenizer.
 *  
 */
public class StreamTokenizerJWS extends StreamTokenizerTAW {
	protected final static int debugLevel = 0;   // Used to control output from this project (0 = no output, 1=some, 2=much, 3=all).
	
	private final int      k;                  // Keep the last k items around.
	private int      counter       = -1; // The location of the current token (in a "wrap around" buffer).
	private int      itemsInBuffer =  0;
	private int      itemsToReturn =  0; // When this is zero, need to call the underlying StreamTokenizer.
	private final int[]    saved_ttype;
	private final String[] saved_sval;
	private final double[] saved_nval;
	private final int[]    saved_lineno;
	
	private boolean  doingSuperCall = false; // Apparently when dealing with comments, there is a recursive call to nextToken.  So have to know to ignore that.

	StreamTokenizerJWS(Reader reader) {
		super(reader);
		this.k          = 8;
		saved_ttype     = new int[8];
		saved_sval      = new String[8];
		saved_nval      = new double[8];
		saved_lineno    = new int[8];
	}
	
	int prevToken() {
		if (itemsInBuffer < 2) { return Integer.MIN_VALUE; } // Nothing yet to return.
		int prevCounter = (counter - 1 + k) % k;
		return saved_ttype[prevCounter];
	}

	private String holdString        = null; 
	private int    holdStringcounter = -1;
	void pushBack(String str) { // A hack to allow a string to be pushed (can only do this ONCE).  prevToken wont work ...
		if ("-".equals(str)) { Utils.waitHere("pushing back a '-' as a string ..."); }
		if ("+".equals(str)) { Utils.waitHere("pushing back a '+' as a string ..."); }
		holdString        = str;
		holdStringcounter = 1;
	}

	public int nextToken() throws IOException {
        if (holdStringcounter >= 0) {
            holdStringcounter--;
            if (holdStringcounter >= 0) {
                return TT_WORD;
            }
			holdString = null;
        }

        if (doingSuperCall) {
            return super.nextToken();
        } // See comment above where doingSuperCall is defined.
        else if (itemsToReturn > 0) { // Have buffered items (due to pushBack's) to return.  Pop the stack.
            itemsToReturn--;
            counter = (counter + 1) % k;
            int result = saved_ttype[counter];
            if (debugLevel > 1) {
                Utils.println("Got a buffered token: '" + reportCurrentToken() + "'");
            }
            return result;
        }



        // Ran out of pushed-back items, so access the underlying StreamTokenizer.
        doingSuperCall = true; // See comment above where doingSuperCall is defined.
        int superNextToken = super.nextToken();
        doingSuperCall = false;

        counter =   (counter + 1) % k;
        saved_ttype[ counter] = super.ttype;
        saved_sval[  counter] = super.sval;
        saved_nval[  counter] = super.nval;
        saved_lineno[counter] = super.lineno();
        itemsInBuffer = Math.min(k, itemsInBuffer + 1); // This only matters until the buffer gets full.
        if (debugLevel > 1) {
            Utils.println("Got a true token and counter=" + counter + " superType='" + (char) super.ttype + "' sval=" + super.sval + " line# = " + super.lineno());
        }
        return superNextToken;
    }
	
	/*
	 * Push back the last n items.
	 */
	void pushBack(int n) {
		for (int i = 0; i < n; i++) { pushBack(); }
	}
	
	
	public void pushBack() { // Pretend that something is being pushed on the stack.  If losing "future" items off the "bottom" of the stack, complain.
		// If this error occurs, simply set 'k' to a higher value and rerun.
		if (itemsToReturn >= k)             { if (debugLevel > 0) dump(); Utils.error("The buffered StreamTokenizerJWS has had too many push-backs and no longer has (or never had) the old items.  At line #" + lineno()); }
		if (itemsToReturn >= itemsInBuffer) { if (debugLevel > 0) dump(); Utils.error("Cannot do another pushBack() because have not yet read enough tokens.  At line #" + lineno()); }
		if (counter == 0) { counter = k - 1; } else { counter--; }
		if (debugLevel > 2) { Utils.println("Did a pushback to '" + reportCurrentToken() + "'"); }
		itemsToReturn++;
	}
	
	/*
	 * Rather than managing quoted strings here, require the caller to
	 * decide if it wants to get the sval associated with a pair of quote
	 * marks.
	 */
	String svalQuoted() {
		if (counter < 0)                     { if (debugLevel > 0) dump(); Utils.error("Need to call nextToken() before reading the stream's contents.  At line #" + lineno()); }
		return saved_sval[counter];
	}

	String sval() { // In the existing code these are not methods (presumably for speed reasons), but need to convert in order to buffer.
		if (holdStringcounter >= 0){ return holdString; }
		if (counter < 0)                     { if (debugLevel > 0) dump(); Utils.error("Need to call nextToken() before reading the stream's contents.  At line #" + lineno()); }
		if (saved_ttype[counter] != TT_WORD) { if (debugLevel > 0) dump(); Utils.error("Shouldn't ask for a WORD when the type != TT_WORD.  Read '" + reportCurrentToken() + "' at line #" + lineno()); }
		return saved_sval[counter];
	}
	
	double nval() { // In the existing code these are not methods (presumably for speed reasons), but need to convert in order to buffer.
		if (counter < 0)                                                   { Utils.error("Need to call nextToken() before reading the stream's contents.  At line #" + lineno()); }
		if (saved_ttype[counter] != TT_NUMBER) { if (debugLevel > 0) dump(); Utils.error("Shouldn't ask for a NUMBER when the type != TT_NUMBER.  Read '" + reportCurrentToken() + "' at line #" + lineno()); }
		return saved_nval[counter];
	}
	
	int ttype() { // In the existing code these are not methods (presumably for speed reasons), but need to convert in order to buffer.
		if (holdStringcounter >= 0) { return StreamTokenizer.TT_WORD; }
		if (counter < 0) { if (debugLevel > 0) dump(); Utils.error("Need to call nextToken() before reading the stream's contents.  At line #" + lineno()); }
		return saved_ttype[counter];
	}
	
	public int lineno() {
		// DO NOT use lineno() here as would lead to infinitely recursive calls.
		if (counter < 0) {
			dump();
			Utils.error("Need to call nextToken() before reading the stream's contents."); }
		return saved_lineno[counter];
	}
	
	String reportCurrentToken() {
		if (holdStringcounter >= 0){ return holdString; }
		if (ttype() == TT_WORD)    { return sval(); }
		if (ttype() == TT_NUMBER)  { return Double.toString(nval()); }
		return String.valueOf((char) ttype());
	}
	
	private void dump() {
		Utils.println("The contexts of the StreamTokenizer buffer (of size = " + itemsInBuffer + "):");
		int topOfBuffer    = (counter + itemsToReturn) % k;
		for (int i = 0; i < k; i++) {
			int index =  (topOfBuffer - i + k) % k; // Add the k so that no need to take mods of negative numbers.
			if (index >= itemsInBuffer) { continue; }
			Utils.print("  [" + index + "]");  if (index >= itemsInBuffer) { Utils.println("   <uninitialized>"); continue; }
			if  (index == counter) Utils.print("@"); // This is the current token.
			else                   Utils.print(" ");
			if (saved_ttype[index] == TT_WORD)   Utils.println("  word   =  " + saved_sval[index]);
			if (saved_ttype[index] == TT_NUMBER) Utils.println("  number =  " + saved_nval[index]);
			if (saved_ttype[index] != TT_WORD && saved_ttype[index] != TT_NUMBER) Utils.println("  ttype  =  " + (char)saved_ttype[index]);
		}
	}
}
