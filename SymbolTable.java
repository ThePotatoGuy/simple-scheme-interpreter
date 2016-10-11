/**
 * Symbol table (holds scopeblocks in an ArrayList like a stack)
 * (also orders the scopeblocks accordingly, with global scope at
 * the bottom of the stack)
 * (This is dynamic scope)
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */
 
import java.util.ArrayList;
import java.util.ListIterator;

public class SymbolTable{
    
    public SymbolTable(){
        debugMode = false;
        table = new ArrayList<ScopeBlock>();
        pushScope(); // this adds the global scope
    }
    
    public SymbolTable(boolean debugMode){
        this.debugMode = debugMode;
        table = new ArrayList<ScopeBlock>();
        pushScope(); // this adds the global scope
    }
    
    public void defineGlobal(String symbol, SchemeObject def){
        table.get(0).define(symbol,def);
    }
    
    public void defineLocal(String symbol, SchemeObject def){
        table.get(table.size()-1).define(symbol,def);
    }
    
    public SchemeObject lookup(String symbol){
        if(debugMode){
            System.out.print("Looking up: " + symbol);
        }
        for(int i = table.size()-1; i >= 0; i--){
            SchemeObject def = table.get(i).lookup(symbol);
            if(def != null){
                if(debugMode){
                    System.out.println("-->found it!");
                }
                return def;
            }
        }
        if(debugMode){
            System.out.println("-->not here");
        }
        return null;
    }
    
    public void popScope(){
        if(table.size() > 1){
            if(debugMode){
                System.out.println("Removing Scope Layer");
            }
            table.remove(table.size()-1);
        }
    }
    
    public void pushScope(){
        if(debugMode){
            System.out.println("Adding Scope Layer");
        }
        table.add(new ScopeBlock(debugMode));
    }
    
    public void setDebugMode(boolean debugMode){
        this.debugMode = debugMode;
    }
    
    private boolean debugMode;
    private ArrayList<ScopeBlock> table;
}
