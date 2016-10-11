/**
 * Custom Exception for Parser
 * 
 * @author Andre Allan Ponce
 */

public class ParserException extends Exception{
    
    public ParserException(){
        super(UNKNOWN_ERROR);
    }
    
    public ParserException(String message){
        super(PARSER_PREFIX + message);
    }
    
    private static final String UNKNOWN_ERROR = "Unidentified Parser Exception";
    private static final String PARSER_PREFIX = "Parser Exception: ";
}
