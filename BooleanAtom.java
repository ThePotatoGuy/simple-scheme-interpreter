/**
 * Boolean Atom
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class BooleanAtom extends SchemeObject{
    
    public static final int BOOLEAN_ATOM_ID = 3;
    
    public BooleanAtom(boolean value){
        this.value = value;
    }
    
    public SchemeObject clone(){
        return new BooleanAtom(value);
    }
    
    public int getID(){
        return BOOLEAN_ATOM_ID;
    }
    
    public ObjectType getType(){
        return ObjectType.BOOLEAN_ATOM;
    }
    
    public boolean getValue(){
        return value;
    }
    
    public String toString(){
        return getType().getValue() + ":" + value;
    }
    
    private boolean value;
}
