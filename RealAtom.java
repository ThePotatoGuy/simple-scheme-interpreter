/**
 * Real Atom
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class RealAtom extends SchemeObject{
	
	public static final int REAL_ATOM_ID = 5;
	
	public RealAtom(double value){
		this.value = value;
	}
	
	public SchemeObject clone(){
		return new RealAtom(value);
	}
	
	public int getID(){
		return REAL_ATOM_ID;
	}
	
	public ObjectType getType(){
		return ObjectType.REAL_ATOM;
	}
	
	public double getValue(){
		return value;
	}
	
	public String toString(){
		return getType().getValue() + ":" + value;
	}
	
	private double value;
}
