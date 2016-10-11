/**
 * Symbol Atom
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class SymbolAtom extends SchemeObject{
    
    public static final int SYMBOL_ATOM_ID = 2;
    public static final String SYMBOL_QUOTE = "quote";
    public static final char SYMBOL_QUOTE_VALUE = '\'';
    
    public SymbolAtom(String value){
        this.value = value;
    }
    
    public SchemeObject clone(){
        return new SymbolAtom(value);
    }
    
    public int getID(){
        return SYMBOL_ATOM_ID;
    }
    
    public ObjectType getType(){
        return ObjectType.SYMBOL_ATOM;
    }
    
    public String getValue(){
        return value;
    }
    
    public String toString(){
        return getType().getValue() + ":" + value;
    }
    
    private String value;
}
