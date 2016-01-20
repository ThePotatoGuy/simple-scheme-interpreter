/**
 * ScannerTest.java - test driver for scanner module.
 * COMP141 - Spring 2015
 * @author Andre Allan Ponce
 * Assignment 7
 */
 
 import java.util.Scanner;

public class ScannerTest{
	public static void main(String[] args){
		boolean debugMode = false;
		if(args.length > 0 && args[0].equals("--debug")){
			debugMode = true;
		}
		boolean blankNotEntered = true;
		Scanner input = new Scanner(System.in);
		TokenScanner tokenScanner = new TokenScanner("TokenScanner");
		while(blankNotEntered){
			System.out.print("SCHEME: ");
			String text = "";
			if(input.hasNextLine()){
				text = input.nextLine();
				System.out.println(text);
			}
			if(text.length() == 0){
				blankNotEntered = false;
				System.out.println();
			}
			else{
				try{
					tokenScanner.init(debugMode);
					tokenScanner.scanString(text);
					System.out.println(tokenScanner.toString());
				}
				catch(ScannerException e){
					System.out.println(e.getMessage()+"\n");
				}
			}
		}
	}
}
