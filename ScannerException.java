/**
 * Custom Exception for Scanner
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class ScannerException extends Exception{
	
	public ScannerException(){
		super(UNKNOWN_ERROR);
	}
	
	public ScannerException(String message){
		super(SCANNER_PREFIX + message);
	}
	
	private static final String UNKNOWN_ERROR = "Unidentified ScannerException";
	private static final String SCANNER_PREFIX = "Scanner Exception: ";
}
