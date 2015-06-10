/*
 * Token.java
 */


/**
 *
 * @author olga
 */

public class Token {
    private int startPosition; // starting position
    private int endPosition;   // ending position
    private String value;
    
    /** Creates a new instance of Token */
    public Token(String inputS, int pStart, int pEnd) {
        value         = inputS;
        startPosition = pStart;
        endPosition   = pEnd;
    }
    public int    startPosition(){return startPosition;};
    public int    endPosition(){return endPosition;};
    public String Value(){return value;};
}
