/**
 * Scanner for scanning in text and turning it into tokens
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

import java.util.StringTokenizer; // for splitting tokens into strings by whitespaces
import java.util.ArrayList;

public class TokenScanner{
    
    public TokenScanner(String joke){}
    
    //==================================================================+
    // PUBLIC METHODS                                                   |
    //==================================================================+
    
    public ArrayList<Token> getTokens(){
        return tokens;
    }
    
    public void init(boolean debugMode){
        tokens = new ArrayList<Token>();
        input = "";
        range = 0;
        state = STATE_EMPTY;
        this.debugMode = debugMode;
    }
    
    /**
     * splits a String into tokens using StringTokenizer, then parses each 
     * token for more tokens
     * 
     * @param text - the text to tokenize
     * 
     */
    public void scanString(String text) throws ScannerException{
        input = text;
        tokens.clear();
        StringTokenizer st = new StringTokenizer(text);
        while(st.hasMoreTokens()){
            try{
                String token = st.nextToken();
                if(debugMode){
                    System.out.println(token);
                }
                parseNextToken(token);
            }
            catch(ScannerException e){
                throw e;
            }
        }
    }
    
    /**
     * formats tokens into a readable format
     * @return String representation of tokens
     */
    public String toString(){
        String result = "";
        result = "\n"
            + LABEL_INPUT +"\n"
            + input +"\n\n";
        for(int i = 0; i < tokens.size();i++){
            result = result + tokens.get(i).toString() + "\n";
        }
        return result;
    }
    
    //==================================================================+
    // PRIVATE METHODS                                                  |
    //==================================================================+

    /**
     * parses text to create tokens
     * Each token path is decided from scanEmpty
     * each char in text is examined
     * residual tokens at the end of the text will be made after the loop,
     * provided they fit the requirements of tokens
     * 
     * @param text - the text to parse
     */
    private void parseNextToken(String text) throws ScannerException{
        if(text.length() == 0){
            state = STATE_ERROR;
            throw new ScannerException();
        }
        for(int i = 0; i < text.length(); i++){
            if(debugMode){
                System.out.println(text.charAt(i) + ":"+text.length()+":"+i+"==> current_state: "+state);
            }
            switch(state){
                case STATE_EMPTY:{
                    try{
                        scanEmpty(text.charAt(i),i);
                    }
                    catch(ScannerException e){
                        throw e;
                    }
                    break;
                }
                case STATE_BOOL:{
                    try{
                        scanBoolean(text.charAt(i));
                    }
                    catch(ScannerException e){
                        throw e;
                    }
                    break;
                }
                case STATE_NUM_INT:
                case STATE_NUM_DEC:{
                    try{
                        scanNumber(text.charAt(i),text,i);
                    }
                    catch(ScannerException e){
                        throw e;
                    }
                    break;
                }
                case STATE_SYM:{
                    try{
                        scanSymbol(text.charAt(i),text,i);
                    }
                    catch(ScannerException e){
                        throw e;
                    }
                    break;
                }
                case STATE_ERROR:{
                    throw new ScannerException();
                }
                default:{
                    break; // we shouldnt be going here
                }
            }
        }
        switch(state){
            case STATE_BOOL:{
                throw new ScannerException(BAD_BOOLEAN_MESSAGE+"NULL");
            }
            case STATE_NUM_DEC:
            case STATE_NUM_INT:{
                try{
                    scanNumber('\n',text,text.length());
                }
                catch(ScannerException e){
                    throw e;
                }
                break;
            }
            case STATE_SYM:{
                try{
                    scanSymbol('\n',text,text.length());
                }
                catch(ScannerException e){
                    throw e;
                }
                break;
            }
            default:{
                break; // we good, probably
            }
        }
    }
    
    private boolean isAlpha(char text){
        return Character.isLetter(text);
    }
    
    private boolean isBooleanHash(char text){
        return text == '#';
    }
    
    private boolean isNumeric(char text){
        return isNumericSymbol(text) || Character.isDigit(text);
    }
    
    private boolean isNumericSymbol(char text){
        return text == '.' || text == '+' || text == '-';
    }
    
    private boolean isPunctuation(char text){
        return text == '(' || text == ')' || text == '\'';
    }
    
    /**
     * scans for booleans in the form #t or #f
     * Assumes scanEmpty has found a #
     * 
     * @param text - the char we are examining
     */
    private void scanBoolean(char text) throws ScannerException{
        if(text == 't'){
            tokens.add(new BooleanToken(true));
            state = STATE_EMPTY;
        }
        else if(text == 'f'){
            tokens.add(new BooleanToken(false));
            state = STATE_EMPTY;
        }
        else{
            state = STATE_ERROR;
            if(text == '\n'){
                throw new ScannerException(BAD_BOOLEAN_MESSAGE+"newline");
            }
            else{
                throw new ScannerException(BAD_BOOLEAN_MESSAGE+text);
            }
        }
    }
    
    /**
     * Scans the first character after a token to decide what to scan for
     * All subsequent other scan methods assume that this method scans the
     * first char for each token
     * 
     * @param text - the first char of a possible token
     * @param index - the index of this char in the token/expr
     */
    private void scanEmpty(char text, int index) throws ScannerException{
        if(isPunctuation(text)){
            tokens.add(new PunctuationToken(text));
            range++;
        }
        else if(isBooleanHash(text)){
            state = STATE_BOOL;
            range = index;
        }
        else if(isAlpha(text)){
            state = STATE_SYM;
            range = index;
        }
        else if(isNumeric(text)){
            if(text == '.'){
                state = STATE_NUM_DEC;
            }
            else{
                state = STATE_NUM_INT;
            }
            range = index;
        }
        else{
            state = STATE_ERROR;
            if(text == '\n'){
                throw new ScannerException(INVALID_CHARACTER_MESSAGE+"newline");
            }
            else{
                throw new ScannerException(INVALID_CHARACTER_MESSAGE+text);
            }
        }
    }
    
    /**
     * scans for number
     * if the char is a '.', switch state to decimal mode
     * if already in decimal, another '.' will throw error
     * if number ends after + or -, throw error
     * creates number token by extractin substring number from expr
     * and parsing it according to current numberstate
     * 
     * @param text - the char we are examining
     * @param expr - the token/expression
     * @param index - the index text is located in expr
     */
    private void scanNumber(char text, String expr, int index) throws ScannerException{
        if(text == '.'){
            switch(state){
                case STATE_NUM_INT:{
                    state = STATE_NUM_DEC;
                    break;
                }
                case STATE_NUM_DEC:{
                    state = STATE_ERROR;
                    throw new ScannerException(BAD_NUMBER_DECIMAL_MESSAGE);
                }
                default:{
                    break; // we should never go here
                }
            }
        }
        else if(!Character.isDigit(text)){
            if(index - range == 1 && isNumericSymbol(expr.charAt(range))){
                state = STATE_ERROR;
                if(text == '\n'){
                    throw new ScannerException(BAD_NUMBER_DIGIT_MESSAGE+"newline");
                }
                else{
                    throw new ScannerException(BAD_NUMBER_DIGIT_MESSAGE+text);
                }
            }
            else{
                String result = "";
                if(expr.charAt(range) == '-'){
                    result = result + "-";
                    range++;
                }
                else if(expr.charAt(range) == '+'){
                    range++;
                }
                result = result + expr.substring(range,index);
                try{
                    switch(state){
                        case STATE_NUM_INT:{
                            long numResult = Long.parseLong(result);
                            tokens.add(new IntegerToken(numResult));
                            state = STATE_EMPTY;
                            break;
                        }
                        case STATE_NUM_DEC:{
                            double numResult = Double.parseDouble(result);
                            tokens.add(new RealToken(numResult));
                            state = STATE_EMPTY;
                            break;
                        }
                        default:{
                            break; // we should not be going here
                        }
                    }
                    if(index != expr.length()){
                        scanEmpty(text,index);
                    }
                }
                catch(NumberFormatException e){
                    state = STATE_ERROR;
                    throw new ScannerException(BAD_NUMBER_MESSAGE+result);
                }
            }
        }
    }
    
    /**
     * scans for symbol
     * creates symbol token once a nonsymbol token is found
     * 
     * @param text - the char we are examining
     * @param expr - the token/expression
     * @param index - the index text is at in the expr
     * 
     */
    private void scanSymbol(char text, String expr, int index) throws ScannerException{
        if(!isAlpha(text) && !Character.isDigit(text)){
            String result = expr.substring(range,index);
            tokens.add(new SymbolToken(result));
            state = STATE_EMPTY;
            if(index != expr.length()){
                scanEmpty(text,index);
            }
        }
    }
    
    //==================================================================+
    // PRIVATE ATTRIBUTES                                               |
    //==================================================================+
    
    private ArrayList<Token> tokens;
    private String input;
    private int state;
    private int range;
    private boolean debugMode = false;
    
    // states used for creating tokens
    private static final int STATE_BOOL = 1;
    private static final int STATE_SYM = 2;
    private static final int STATE_NUM_INT = 3;
    private static final int STATE_NUM_DEC = 4;
    private static final int STATE_EMPTY = 5;
    private static final int STATE_ERROR = 6;
    
    // exception messages
    private static final String BAD_BOOLEAN_MESSAGE = "badly formed boolean - expected 't' or 'f', found ";
    private static final String BAD_NUMBER_MESSAGE = "badly formed integer - expected a number, found ";
    private static final String BAD_NUMBER_DECIMAL_MESSAGE = "badly formed number - multiple decimal points";
    private static final String BAD_NUMBER_DIGIT_MESSAGE = "badly formed number - expected a digit, found ";
    private static final String INVALID_CHARACTER_MESSAGE = "Invalid Character: ";
    
    // toString labels:
    private static final String LABEL_INPUT = "Input Expression:";
    private static final String LABEL_RESULT = "Scanner Result:";
}
    
