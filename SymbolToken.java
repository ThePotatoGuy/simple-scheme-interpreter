/**
 * Symbol Token
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class SymbolToken extends Token{
	
	public static final int SYMBOL_TOKEN_ID = 1;
	
	public SymbolToken(String value){
		this.value = value;
	}
	
	public int getID(){
		return SYMBOL_TOKEN_ID;
	}
	
	public TokenType getType(){
		return TokenType.SYMBOL_TOKEN;
	}
	
	public String getValue(){
		return value;
	}
	
	public String toString(){
		return getType().getValue() + ":\t\t" + value;
	}
	
	private String value;
}
