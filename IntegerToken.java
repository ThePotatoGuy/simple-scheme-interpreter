/**
 * Integer Token
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class IntegerToken extends Token{
    
    public static final int INTEGER_TOKEN_ID = 3;
    
    public IntegerToken(long value){
        this.value = value;
    }
    
    public int getID(){
        return INTEGER_TOKEN_ID;
    }
    
    public TokenType getType(){
        return TokenType.INTEGER_TOKEN;
    }
    
    public long getValue(){
        return value;
    }
    
    public String toString(){
        return getType().getValue() + ":\t" + value;
    }
    
    private long value;
}
