/**
 * Empty List Object (because they are treated differently)
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class EmptyListObject extends SchemeObject{
    
    public static final int EMPTY_LIST_ID = 1;
    
    public EmptyListObject(){
        this.value = "()";
    }
    
    public SchemeObject clone(){
        return new EmptyListObject();
    }
    
    public int getID(){
        return EMPTY_LIST_ID;
    }
    
    public ObjectType getType(){
        return ObjectType.EMPTY_LIST;
    }
    
    public String getValue(){
        return value;
    }
    
    public String toString(){
        return getType().getValue() + ":" + value;
    }
    
    private String value;
}
