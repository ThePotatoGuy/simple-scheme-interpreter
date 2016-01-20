/**
 * Scope block (which is really like a wrapper for the HashMap)
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

import java.util.HashMap;

public class ScopeBlock{
	
	public ScopeBlock(boolean debugMode){
		this.debugMode = debugMode;
		blockMap = new HashMap<String,SchemeObject>();
	}
	
	public void clear(){
		blockMap.clear();
	}
	
	public void define(String symbol, SchemeObject def){
		if(debugMode){
			System.out.println("Inserting new entry for "+symbol+"->"+def.toString());
		}
		blockMap.put(symbol, def);
	}
	
	public SchemeObject lookup(String symbol){
		return blockMap.get(symbol);
	}
	
	private HashMap<String,SchemeObject> blockMap;
	private boolean debugMode;
}
