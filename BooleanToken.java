/**
 * Boolean Token
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class BooleanToken extends Token{
    
    public static final int BOOLEAN_TOKEN_ID = 2;
    
    public BooleanToken(boolean value){
        this.value = value;
    }
    
    public int getID(){
        return BOOLEAN_TOKEN_ID;
    }
    
    public TokenType getType(){
        return TokenType.BOOLEAN_TOKEN;
    }
    
    public boolean getValue(){
        return value;
    }
    
    public String toString(){
        return getType().getValue() + ":\t" + value;
    }
    
    private boolean value;
}
