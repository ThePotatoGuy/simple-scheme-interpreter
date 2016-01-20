/**
 * Function Object (for functions!)
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class Function extends SchemeObject{
	
	public static final int FUNCTION_ID = 10;
	public static final String FUNCTION_VALUE = "#<procedure>";
	
	public Function(){
		this.params = null;
		this.body = null;
	}
	
	public Function(ConsCell params, SchemeObject body){
		this.params = params;
		this.body = body;
	}
	
	public SchemeObject clone(){
		ConsCell tempParams = null;
		SchemeObject tempBody = null;
		if(params != null){
			tempParams = (ConsCell)params.clone();
		}
		if(body != null){
			tempBody = body.clone();
		}
		return new Function(tempParams,tempBody);
	}
	
	public SchemeObject getBody(){
		return body;
	}
	
	public int getID(){
		return FUNCTION_ID;
	}
	
	public ConsCell getParams(){
		return params;
	}
	
	public ObjectType getType(){
		return ObjectType.FUNCTION;
	}
	
	public void setParams(ConsCell params){
		this.params = params;
	}
	
	public void setBody(SchemeObject body){
		this.body = body;
	}
	
	public String toString(){
		return getType().getValue() + ":" + FUNCTION_VALUE;
	}
	
	private ConsCell params;
	private SchemeObject body;
}
