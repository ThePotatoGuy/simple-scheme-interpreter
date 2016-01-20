/**
 * 
 * Object Type constants
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public enum ObjectType{
	CONS_CELL("ConsCell"),
	FUNCTION("Function"),
	EMPTY_LIST("EmptyList"),
	SYMBOL_ATOM("Symbol"),
	BOOLEAN_ATOM("Boolean"),
	INTEGER_ATOM("Integer"),
	REAL_ATOM("Real");
	
	ObjectType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	private String value;
}
