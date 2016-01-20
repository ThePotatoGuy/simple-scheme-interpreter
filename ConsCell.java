/**
 * ConsCell structure for storing pairs
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 * 2015-04-26:
 * - removed generic type for ConsCell
 */

public class ConsCell extends SchemeObject{
	
	public static final int CONS_CELL_ID = 21;
	
	// Tree strings
	public static final String CONS_CAR_MESSAGE = "ConsCell.car:";
	public static final String CONS_CDR_MESSAGE = "ConsCell.cdr:";
	
	public ConsCell(){
		car = null;
		cdr = null;
	}
	
	public ConsCell(ConsCell c){
		this(c.getCar(),c.getCdr());
	}
	
	public ConsCell(SchemeObject t1, SchemeObject t2){
		car = t1;
		cdr = t2;
	}
	
	public SchemeObject clone(){
		SchemeObject tempCar = null;
		SchemeObject tempCdr = null;
		if(car != null){
			tempCar = car.clone();
		}
		if(cdr != null){
			tempCdr = cdr.clone();
		}
		return new ConsCell(tempCar,tempCdr);
	}
	
	public SchemeObject getCar(){
		return car;
	}
	
	public SchemeObject getCdr(){
		return cdr;
	}
	
	public int getID(){
		return CONS_CELL_ID;
	}
	
	public ObjectType getType(){
		return ObjectType.CONS_CELL;
	}
	
	public int length(){
		if(cdr != null && cdr.getID() == CONS_CELL_ID){
			return 1+((ConsCell)cdr).length();
		}
		return 1;
	}
	
	public void setCar(SchemeObject c){
		car = c;
	}
	
	public void setCdr(SchemeObject c){
		cdr = c;
	}
	
	public String toString(){
		String temp = "";
		temp = temp + CONS_CAR_MESSAGE +"\n"
			+"  " + car.toString() + "\n"
			+CONS_CDR_MESSAGE + "\n"
			+"  " + cdr.toString();
		return temp;
	}
	
	private SchemeObject car;
	private SchemeObject cdr;
}
