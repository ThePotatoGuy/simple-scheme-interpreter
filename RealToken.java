/**
 * Real Token
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class RealToken extends Token{
	
	public static final int REAL_TOKEN_ID = 4;
	
	public RealToken(double value){
		this.value = value;
	}
	
	public int getID(){
		return REAL_TOKEN_ID;
	}
	
	public TokenType getType(){
		return TokenType.REAL_TOKEN;
	}
	
	public double getValue(){
		return value;
	}
	
	public String toString(){
		return getType().getValue() + ":\t\t" + value;
	}
	
	private double value;
}
