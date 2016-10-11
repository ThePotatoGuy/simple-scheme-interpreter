/**
 * Punctuation Token
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class PunctuationToken extends Token{
    
    public static final int PUNCTUATION_TOKEN_ID = 5;
    public static final char PUNCTUATION_TOKEN_OPEN = '(';
    public static final char PUNCTUATION_TOKEN_CLOSED = ')';
    
    public PunctuationToken(char value){
        this.value = value;
    }
    
    public int getID(){
        return PUNCTUATION_TOKEN_ID;
    }
    
    public TokenType getType(){
        return TokenType.PUNCTUATION_TOKEN;
    }
    
    public char getValue(){
        return value;
    }
    
    public String toString(){
        return getType().getValue() + ":\t" + value;
    }
    
    private char value;
}
