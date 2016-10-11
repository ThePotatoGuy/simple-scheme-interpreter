/**
 * Scheme Object Abstract Class
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 * 2015-04-23:
 * + added clone prototype
 */

public abstract class SchemeObject{
    
    /**
     * Debug ID
     */
    public static final int SCHEME_OBJECT_ID = 0;
    
    /**
     * copies this schemeObject and returns it (should recursively copy
     * children, too)
     * @return copy of this SchemeObject (and children)
     */
    public abstract SchemeObject clone();
    
    /**
     * each SchemeObject has a SchemeObject ID, given in each SchemeObject
     * @return the SchemeObjectID of this SchemeObject
     */
    public int getID(){
        return SCHEME_OBJECT_ID;
    }
    
    /**
     * retrieves the objectType of this token
     * @return ObjectType of this token
     */
    public abstract ObjectType getType();
    
    /**
     * Converts the ObjectType and value into one-liner String
     * @return String -> Object:value
     */
     public abstract String toString();
}
