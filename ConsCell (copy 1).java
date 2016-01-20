/**
 * ConsCell structure for storing the tree
 * 
 * @author Andre Allan Ponce
 */

//import java.util.ArrayList;
/**
public class ConsCell<T>{
	
	public ConsCell(){
		children = new ArrayList<ConsCell<T>>(2);
		hasCar = false;
		hasCdr = false;
	}
	
	public ConsCell(T data){
		super();
		this.data = data;
	}
	
	public ConsCell<T> getCar(){
		return children.get(0);
	}
	
	public ConsCell<T> getCdr(){
		return children.get(1);
	}
	
	public T getData(){
		return data;
	}
	
	public boolean hasCar(){
		return hasCar;
	}
	
	public boolean hasCdr(){
		return hasCdr;
	}
	
	public void setCar(T data){
		hasCar = true;
		children.set(0,new ConsCell<T>(data));
	}
	
	public void setCdr(T data){
		hasCdr = true;
		children.set(1,new ConsCell<T>(data));
	}
	
	public void setData(T data){
		this.data = data;
	}
	
	private boolean hasCar;
	private boolean hasCdr;
	private T data;
	private ArrayList<ConsCell<T>> children;
}
//*/