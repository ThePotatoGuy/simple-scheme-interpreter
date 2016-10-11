/**
 * Custom exception class for the evaluator
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

public class EvaluatorException extends Exception{
    
    public EvaluatorException(){
        super(UNKNOWN_ERROR);
    }
    
    public EvaluatorException(String message){
        super(EVALUATOR_PREFIX + message);
    }
    
    private static final String UNKNOWN_ERROR = "Unidentified EvaluatorException";
    private static final String EVALUATOR_PREFIX = "Evaluator Exception: ";
}
