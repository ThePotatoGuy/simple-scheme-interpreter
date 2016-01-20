/**
 * Token Type constants
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public enum TokenType{
	SYMBOL_TOKEN("Symbol"), 
	BOOLEAN_TOKEN("Boolean"),
	INTEGER_TOKEN("Integer"),
	REAL_TOKEN("Real"),
	PUNCTUATION_TOKEN("Punctuation"),
	STRING_TOKEN("String"),
	CHARACTER_TOKEN("Character");
	
	TokenType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	private String value;
}
