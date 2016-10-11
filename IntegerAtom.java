/**
 * Integer Atom
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class IntegerAtom extends SchemeObject{
    
    public static final int INTEGER_ATOM_ID = 4;
    
    public IntegerAtom(long value){
        this.value = value;
    }
    
    public SchemeObject clone(){
        return new IntegerAtom(value);
    }
    
    public int getID(){
        return INTEGER_ATOM_ID;
    }
    
    public ObjectType getType(){
        return ObjectType.INTEGER_ATOM;
    }
    
    public long getValue(){
        return value;
    }
    
    public String toString(){
        return getType().getValue() + ":" + value;
    }
    
    private long value;
}
