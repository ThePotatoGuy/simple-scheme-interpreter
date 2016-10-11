/**
 * Interpreter for the Scheme-like language
 * Is capable of two levels of debug information
 * 
 * COMP 141 - Spring 2015
 * @author Andre Allan Ponce
 * Assignment 7
 */

import java.util.Scanner;

public class SchemeInterpreter{
    public static void main(String[] args){
        boolean debugMode = false;
        boolean debugMode2 = false;
        if(args.length > 0 && args[0].length() > 1){
            if(args[0].charAt(0) == '-' && args[0].charAt(1) == 'v'){
                debugMode = true;
                if(args[0].length() > 2 && args[0].charAt(2) == 'v'){
                    debugMode2 = true;
                }
            }
        }
        boolean blankNotEntered = true;
        Scanner input = new Scanner(System.in);
        TokenScanner tokenScanner = new TokenScanner("TokenScanner");
        TokenParser tokenParser = new TokenParser("TokenParser");
        Evaluator evaluator = new Evaluator("Evaluator");
        while(blankNotEntered){
            System.out.print("SCHEME: ");
            String text = "";
            if(input.hasNextLine()){
                text = input.nextLine();
                //System.out.println(text);
            }
            if(text.length() == 0){
                blankNotEntered = false;
                System.out.println();
            }
            else{
                try{
                    tokenScanner.init(debugMode2);
                    tokenScanner.scanString(text);
                    if(debugMode){
                        System.out.println(tokenScanner.toString());
                    }
                    tokenParser.init(debugMode2);
                    tokenParser.parse(tokenScanner.getTokens());
                    if(debugMode){
                        System.out.println(tokenParser.toString());
                    }
                    evaluator.init(debugMode2);
                    System.out.println(evaluator.evaluate(tokenParser.getTree()));
                    
                }
                catch(ScannerException e){
                    System.out.println(e.getMessage()+"\n");
                }
                catch(ParserException e){
                    System.out.println(e.getMessage()+"\n");
                }
                catch(EvaluatorException e){
                    System.out.println(e.getMessage()+"\n");
                }
            }
        }
    }
}
