/**
 * Evaluator that takes in a syntax tree and returns the result
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */
import java.util.ArrayList;
import java.util.Formatter;

public class Evaluator{
    
    public Evaluator(String joke){
        symbolTable = new SymbolTable();
    }
    
    //==================================================================+
    // PUBLIC METHODS                                                   |
    //==================================================================+
    
    /**
     * Evaluates the expression and returns String representation of the result
     * 
     * @param expression - the expression to evaluate
     * @return String of the result
     * @throws EvaluatorException (multiple reasons) see evaluateExpression, buildStringFromResult
     */
    public String evaluate(SchemeObject expression) throws EvaluatorException{
        String temp = "";
        if(debugMode){
            temp = RESULT_LABEL + "\n";
        }
        temp = temp + buildStringFromResult(evaluateExpression(expression),true);
        return temp;
    }
    
    /**
     * Initializes the Evaluator
     * 
     * @param debugMode - toggles debugmode output
     */
    public void init(boolean debugMode){
        this.debugMode = debugMode;
        symbolTable.setDebugMode(debugMode);
    }
    
    //==================================================================+
    // PRIVATE METHODS                                                  |
    //==================================================================+
    
    //------------------------------------------------------------------+
    // Begin Function Methods                                           |
    
    /**
     * Applies the Car operation to a list
     * This retreives the first element of a list
     * 
     * @param args - the list to apply the car operation
     * @return SchemeObject representation of the result
     * @throws EvaluatorException if the number of arguments is incorrect or if args is not a list
     */
    private SchemeObject applyCar(ConsCell args) throws EvaluatorException{
        if(args.length() != 1){
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FUNC_CAR);
        }
        if(!isList(args.getCar())){
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FUNC_CAR);
        }
        return ((ConsCell)args.getCar()).getCar().clone();
    }
    
    /**
     * Applies the Cdr operation to a list
     * This retrieves everything after the first element of a list
     * 
     * @param args - the list to apply the cdr operation
     * @return SchemeObject representation of the result
     * @throws EvaluatorException if the number of arguments is incorrect or if args is not a list
     */
    private SchemeObject applyCdr(ConsCell args) throws EvaluatorException{
        if(args.length() != 1){
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FUNC_CDR);
        }
        if(!isList(args.getCar())){
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FUNC_CDR);
        }
        SchemeObject cdr = ((ConsCell)args.getCar()).getCdr();
        if(cdr == null){
            return new EmptyListObject();
        }
        else{
            return cdr.clone();
        }
    }
    
    /**
     * Applies the Cons operation to a list
     * Constructs a new list with the first argument of args at the beginning of the list
     * and everything after the first element of args following the beginning of the list
     * 
     * @param args - list (also contains the argument to cons to the list)
     * @return constructed list with the first item of args at the front
     * @throws EvaluatorException if the number of arguments is incorrect or if the cdr if args is not a list (or emptylist)
     */
    private SchemeObject applyCons(ConsCell args) throws EvaluatorException{
        if(args.length() != 2){
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FUNC_CONS);
        }
        SchemeObject head = args.getCar();
        SchemeObject tail = ((ConsCell)args.getCdr()).getCar();
        if(isList(tail)){
            return new ConsCell(head.clone(),tail.clone());
        }
        else if(tail.getID() == EmptyListObject.EMPTY_LIST_ID){
            return new ConsCell(head.clone(),null);
        }
        else{
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FUNC_CONS);
        }
    }
    
    /**
     * Applies a function to a list of arguments
     * Push scope in the symbolTable to apply the function
     * 
     * @param func - the function to apply
     * @param args - the args to apply the function to
     * @return SchemeObject representation of the result of applying the function to args
     * @throws EvaluatorException if the number of arguments does not match the number of expected params. also see evaluateExpression
     */
    private SchemeObject applyFunction(Function func, ConsCell args) throws EvaluatorException{
        ConsCell params = func.getParams();
        if(params.length() != args.length()){ // arguments size must match params size
            throw new EvaluatorException(BAD_FUNC_ARGS_MESSAGE);
        }
        // function scope
        symbolTable.pushScope();
        while(params != null){
            symbolTable.defineLocal(((SymbolAtom)(params.getCar())).getValue(),args.getCar());
            params = (ConsCell)params.getCdr();
            args = (ConsCell)args.getCdr();
        }
        SchemeObject result = evaluateExpression(func.getBody());
        symbolTable.popScope();
        return result;
    }
    
    /**
     * Applies the isnull function an argument
     * Basically checks if the input was null () or empty list null '()
     * 
     * @param args - the argument to check for null
     * @return SchemeObject (BooleanAtom) that contains true if args was null, false if not
     * @throws EvaluatorException of the number of arguments is incorrect
     */
    private SchemeObject applyIsNull(ConsCell args) throws EvaluatorException{
        if(args.length() != 1){
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FUNC_NULL);
        }
        return new BooleanAtom(args.getCar().getID() == EmptyListObject.EMPTY_LIST_ID);
    }
    
    /**
     * Applies the lessthan function to two arguments
     * checks if the first argument is less than the second argument
     * 
     * @param args - the arguments to check for lessthan
     * @return SchemeObject (BooleanAtom) that contains true if the first arg is less than the second arg, false if not
     * @throws EvaluatorException if the number of arguments is incorrect. also see evaluateNum
     */
    private SchemeObject applyLessThan(ConsCell args) throws EvaluatorException{
        if(args.length() != 2){
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FUNC_LESSTHAN);
        }
        SchemeObject leftArg = args.getCar();
        SchemeObject rightArg = ((ConsCell)args.getCdr()).getCar();
        double leftNum = evaluateNum(leftArg,FUNC_LESSTHAN);
        double rightNum = evaluateNum(rightArg,FUNC_LESSTHAN);
        return new BooleanAtom(Math.abs(leftNum - rightNum) > THRESHOLD && leftNum < rightNum);
    }
    
    /**
     * Applies the plus function to a list of arguments
     * Basically sums the arguments in args
     * 
     * @param args - the numbers to sum
     * @return SchemeObject (IntegerAtom or RealAtom) that contains the sum of the numbers in args
     * @throws EvaluatorException if args contains at least one non-number
     */
    private SchemeObject applyPlus(ConsCell args) throws EvaluatorException{
        if(isAllIntegers(args)){ // do integer math
            return new IntegerAtom(sumIntegerList(args));
        }
        else{ // do double math
            return new RealAtom(sumRealList(args));
        }
    }
    
    // End Function Methods                                             |
    //------------------------------------------------------------------+
    // Begin Evaluate Special Forms                                     |
    
    /**
     * Implements the define form
     * Basically puts the args into the symbol table and matches it with the first arg, which is the key
     * This puts this entry into the global symbol table
     * 
     * @param args - the args to enter into the symbol table
     * @return null. Define only sets, does not return anything
     * @throws EvaluatorException if the number of arguments is incorrect, if the first arg is not a symbol, 
     *      or if the second arg is not a list. also see evaluateExpression
     */
    private SchemeObject evaluateDefineForm(ConsCell args) throws EvaluatorException{
        if(args.length() != 2){ // 2 args for a define statement
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FORM_DEFINE);
        }
        SchemeObject name = args.getCar();
        SchemeObject def = args.getCdr();
        String symbol = "";
        switch(name.getID()){
            case SymbolAtom.SYMBOL_ATOM_ID:{
                symbol = ((SymbolAtom)name).getValue();
                break;
            }
            default:{ // if the first arg aint a symbol
                throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_DEFINE);
            }
        }
        if(!isList(def)){ // if the second arg aint a list
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_DEFINE);
        }
        def = evaluateExpression(((ConsCell)def).getCar());
        symbolTable.defineGlobal(symbol,def);
        return null;
    }
    
    /**
     * Implements the if form
     * the first argument is the condition. if the condition evalutes to true, the 2nd argument gets evaluated. if false, the 3rd argument gets evaluated
     * 
     * @param args - the arguments to evaluate
     * @return SchemeObject representation of the result
     * @throws EvaluatorException if the number of arguments is incorrect or if the condition evaluates to null. also see evaluateExpression
     */
    private SchemeObject evaluateIfForm(ConsCell args) throws EvaluatorException{
        if(args.length() != 3){ // 3 args for an if statement
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FORM_IF);
        }
        SchemeObject condition = evaluateExpression(args.getCar());
        if(condition == null){
            throw new EvaluatorException(BAD_IF_MESSAGE + FORM_IF);
        }
        if(condition.getID() == BooleanAtom.BOOLEAN_ATOM_ID && !((BooleanAtom)condition).getValue()){
            return evaluateExpression(((ConsCell)((ConsCell)args.getCdr()).getCdr()).getCar());
        }
        else{
            return evaluateExpression(((ConsCell)args.getCdr()).getCar());
        }
    }
    
    /**
     * Implements the lambda form
     * the first argument is params. the second argument is a function body
     * 
     * @param args - the arguments to lambdaize
     * @return SchemeObject (Function) that contains the formal params and body of this function
     * @throws EvaluatorException if the number of arguments is incorrect, if the first arg is not a list, 
     *      if the second arg is not a list, or if the params contain a non-symbol
     */
    private SchemeObject evaluateLambdaForm(ConsCell args) throws EvaluatorException{
        if(args.length() != 2){ // 2 args for a lambda
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FORM_LAMBDA);
        }
        if(!isList(args.getCar())){ // first arg must be list
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LAMBDA);
        }
        ConsCell formalParams = (ConsCell)args.getCar();
        if(!isList(args.getCdr())){ // second arg must be a list
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LAMBDA);
        }
        SchemeObject body = ((ConsCell)args.getCdr()).getCar();
        if(!isAllSymbols(formalParams)){
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LAMBDA);
        }
        return new Function((ConsCell)formalParams.clone(),body.clone());
    }
    
    /**
     * Implements the let form
     * Let binds its first argument (list of symbols) the symbol table temporaily, then evalutes the second argument
     * 
     * @param args - the arguments to let
     * @return SchemeObject representation that contains the result
     * @throws EvaluatorException if the number of arguments is incorrect, if the first arg is not a list, 
     *      if the second arg is not a list, if the list of bindings contains a nonlist, 
     *      if the list of bindings contains a binding that is not size 2, or if the first argument of a binding is not a symbol.
     *      also see evaluateExpression
     */
    private SchemeObject evaluateLetForm(ConsCell args) throws EvaluatorException{
        if(args.length() != 2){ // 2 args for a let form
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FORM_LET);
        }
        SchemeObject body = args.getCdr();
        if(!isList(body)){ // if second arg aint a list
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LET);
        }
        body = ((ConsCell)body).getCar();
        if(!isList(args.getCar())){ // if first arg aint a list
            throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LET);
        }
        ConsCell bindings = (ConsCell)args.getCar();
        symbolTable.pushScope();
        int length = bindings.length();
        while(length > 0){
            SchemeObject binding = bindings.getCar();
            if(!isList(binding)){ // bindings must be lists
                throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LET);
            }
            ConsCell binder = (ConsCell)binding;
            if(binder.length() != 2){ // each binding must be size 2
                throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LET);
            }
            SchemeObject bindingSymbol = binder.getCar();
            binding = binder.getCdr();
            if(!isList(binding)){
                throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LET);
            }
            binder = (ConsCell)binding;
            switch(bindingSymbol.getID()){
                case SymbolAtom.SYMBOL_ATOM_ID:{
                    symbolTable.defineLocal(((SymbolAtom)bindingSymbol).getValue(),evaluateExpression(binder.getCar()));
                    break;
                }
                default:{ // first arg of bindings must be symbol
                    throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LET);
                }
            }
            length--;
            if(length > 0){
                if(!isList(bindings.getCdr())){ // bindings must be lists
                    throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + FORM_LET);
                }
                bindings = (ConsCell)bindings.getCdr();
            }
        }
        body = evaluateExpression(body);
        symbolTable.popScope();
        return body;
    }
    
    /**
     * Implements the Quote form
     * all this really does is return whatever is after the quote
     * 
     * @param args - arguments for ths quote
     * @return SchemeObject reprsentation of everything after the quote, unevaluated
     * @throws EvaluatorException if the number of arguments is incorrect
     */
    private SchemeObject evaluateQuoteForm(ConsCell args) throws EvaluatorException{
        if(args.length() != 1){ // only one argument expected
            throw new EvaluatorException(BAD_ARGS_NUM_MESSAGE + FORM_QUOTE);
        }
        return args.getCar();
    }
    
    // End Evaluate Special Forms                                       |
    //------------------------------------------------------------------+
    
    /**
     * Recursively builds a string representation of the result
     * 
     * @param result - the SchemeObject to build a string rep of
     * @param wasCar - true if the result could be the first element of a list, false if not
     * @return String representation of the result
     * @throws EvaluatorException if the result null or of unknown type
     */
    private String buildStringFromResult(SchemeObject result, boolean wasCar) throws EvaluatorException{
        if(result == null){
            return "";
        }
        if(debugMode){
            System.out.println("Found Result: " + result.getID());
        }
        switch(result.getID()){
            case SymbolAtom.SYMBOL_ATOM_ID:{
                return ((SymbolAtom)result).getValue();
            }
            case BooleanAtom.BOOLEAN_ATOM_ID:{
                if(((BooleanAtom)result).getValue()){
                    return "#t";
                }
                else{
                    return "#f";
                }
            }
            case IntegerAtom.INTEGER_ATOM_ID:{
                return "" + ((IntegerAtom)result).getValue();
            }
            case RealAtom.REAL_ATOM_ID:{
                return "" + ((RealAtom)result).getValue();
            }
            case EmptyListObject.EMPTY_LIST_ID:{
                return ((EmptyListObject)result).getValue();
            }
            case Function.FUNCTION_ID:{
                return ((Function)result).FUNCTION_VALUE;
            }
            case ConsCell.CONS_CELL_ID:{
                ConsCell res = (ConsCell)result;
                String temp = "";
                if(wasCar){
                    temp = "(";
                }
                temp = temp + buildStringFromResult(res.getCar(),true);
                if(res.getCdr() != null){
                    temp = temp + " " + buildStringFromResult(res.getCdr(),false);
                }
                if(wasCar){
                    temp = temp + ")";
                }
                return temp;
            }
            default:{
                throw new EvaluatorException(); // for now
            }
        }
    }
    
    /**
     * Recursively evalutes the expression
     * This method will lookup symbols in the symbolTable and pass lists to the evaluateList method
     * Atoms (and functions) evaluate to themselves
     * 
     * @param expression - the expression to evaluate
     * @return SchemeObject representation of the expression after evaluating
     * @throws EvaluatorException if expression was a symbol not defined in the symbolTable. also see evaluateList
     */
    private SchemeObject evaluateExpression(SchemeObject expression) throws EvaluatorException{
        if(expression == null){
            return null;
        }
        if(debugMode){
            System.out.println("Found expression: " + expression.getID());
        }
        switch(expression.getID()){
            case SymbolAtom.SYMBOL_ATOM_ID:{ 
                SchemeObject def = symbolTable.lookup(((SymbolAtom)expression).getValue());
                if(def == null){
                    throw new EvaluatorException(BAD_SYM_MESSAGE + expression.toString());
                }
                return def;
            }
            case ConsCell.CONS_CELL_ID:{
                return evaluateList((ConsCell)expression);
            }
            default:{
                return expression;
            }
        }
    }
    
    /**
     * Evalutes a list object
     * If the first argument is a symbol, then evaluate the rest of the arguments according to the symbol
     * Otherwise, test for anon lambda functions (stuff)
     * 
     * @param list - the list to evaluate
     * @return SchemeObject representation of the list after evaluating
     * @throws EvaluatorException if the cdr of list is not a list or not null, 
     *      if the first arg symbol does not match the predefined functions and is not in the symbolTable,
     *      or if the list is not an anon lambda. also see evaluateDefineForm, evaluateQuoteForm, evaluateLetForm,
     *      evaluateLambdaForm, evaluateIfForm, applyPlus, applyLessThan, applyCar, applyCdr, applyCons, applyIsNull,
     *      applyFunction
     */
    private SchemeObject evaluateList(ConsCell list) throws EvaluatorException{
        SchemeObject formOrFunction = list.getCar();
        ConsCell args;
        if(list.getCdr() == null){
            args = null;
        }
        else if(isList(list.getCdr())){
            args = (ConsCell)list.getCdr();
        }
        else{
            throw new EvaluatorException(BAD_LIST_ARGS_MESSAGE);
        }
        if(debugMode){
            System.out.println("Found something: " + formOrFunction.getID());
        }
        if(formOrFunction.getID() == SymbolAtom.SYMBOL_ATOM_ID){
            String name = ((SymbolAtom)formOrFunction).getValue();
            if(debugMode){
                System.out.println("Found symbol: " + name);
            }
            switch(name){ // check forms
                case FORM_DEFINE:{
                    return evaluateDefineForm(args);
                }
                case FORM_QUOTE:{
                    return evaluateQuoteForm(args);
                }
                case FORM_LET:{
                    return evaluateLetForm(args);
                }
                case FORM_LAMBDA:{
                    return evaluateLambdaForm(args);
                }
                case FORM_IF:{
                    return evaluateIfForm(args);
                }
                default:{
                    break; // this must be a function! (or nothing)
                }
            }
            ConsCell evaluatedArgs = evaluateListArgs(args);
            switch(name){ // check functions
                case FUNC_PLUS:{
                    return applyPlus(evaluatedArgs);
                }
                case FUNC_LESSTHAN:{
                    return applyLessThan(evaluatedArgs);
                }
                case FUNC_CAR:{
                    return applyCar(evaluatedArgs);
                }
                case FUNC_CDR:{
                    return applyCdr(evaluatedArgs);
                }
                case FUNC_CONS:{
                    return applyCons(evaluatedArgs);
                }
                case FUNC_NULL:{
                    return applyIsNull(evaluatedArgs);
                }
                default:{
                    break; // this must be in the symbol table! (or not)
                }
            }
            SchemeObject function = symbolTable.lookup(name);
            if(function != null && function.getID() == Function.FUNCTION_ID){
                return applyFunction((Function)function,evaluatedArgs);
            } 
            throw new EvaluatorException(BAD_FUNC_EXPR_MESSAGE);
        }
        else{ // I guess these are anon lambda expressions
            if(!isList(formOrFunction)){
                throw new EvaluatorException(BAD_EXPECTED_LAMBDA_MESSAGE);
            }
            SchemeObject lambda = evaluateExpression(formOrFunction);
            if(lambda == null || lambda.getID() != Function.FUNCTION_ID){
                throw new EvaluatorException(BAD_EXPECTED_LAMBDA_MESSAGE);
            }
            return applyFunction((Function)lambda,args);
        }
    }
    
    /**
     * Recursively evalutes the arguments in a list and puts the result in a new list
     * 
     * @param list - the list of arguments to evaluate
     * @return ConsCell list of the arguments in list after begin evaluated
     * @throws EvaluatorException if the cdr of list is not null and not a list. also see evaluateExpression
     */
    private ConsCell evaluateListArgs(ConsCell list) throws EvaluatorException{
        if(list == null){
            return null;
        }
        ConsCell evaluatedList = new ConsCell();
        evaluatedList.setCar(evaluateExpression(list.getCar()));
        SchemeObject cdr = list.getCdr();
        if(cdr != null && !isList(cdr)){
            throw new EvaluatorException(BAD_LIST_ARGS_MESSAGE);
        }
        evaluatedList.setCdr(evaluateListArgs((ConsCell)cdr));
        return evaluatedList;
    }
    
    /**
     * Evalutes the atom as a number, returning it as a double
     * 
     * @param atom - the number to evalute
     * @param function - the function that calls this function (for error messages)
     * @return double representation of the value of atom (even if atom is an integer)
     * @throws EvaluatorException if atom is not a valid number (IntegerAtom or RealAtom)
     */
    private double evaluateNum(SchemeObject atom, String function) throws EvaluatorException{
        switch(atom.getID()){
            case IntegerAtom.INTEGER_ATOM_ID:{
                return (double)((IntegerAtom)atom).getValue();
            }
            case RealAtom.REAL_ATOM_ID:{
                return ((RealAtom)atom).getValue();
            }
            default:{ // this is bad, we didnt get a number
                throw new EvaluatorException(BAD_ARGS_TYPE_MESSAGE + function);
            }
        }
    }
    
    /**
     * Recurively checks if the list is all IntegerAtoms (and thus, all integers)
     * 
     * @param list - the list of SchemeObjects to check
     * @return true if list only contains integers, false if not
     */
    private boolean isAllIntegers(ConsCell list){
        if(list == null){
            return true;
        }
        else if(list.getCar().getID() != IntegerAtom.INTEGER_ATOM_ID){
            return false;
        }
        else{
            return isAllIntegers((ConsCell)list.getCdr());
        }
    }
    
    /**
     * Recursively checks if the list is all symbols (SymbolAtoms)
     * 
     * @param list - the list of SchemeObjects to check
     * @return true if the list only contains symbols, false if not
     */
    private boolean isAllSymbols(ConsCell list){
        if(list == null){
            return true;
        }
        else if(list.getCar().getID() != SymbolAtom.SYMBOL_ATOM_ID){
            return false;
        }
        else{
            return isAllSymbols((ConsCell)list.getCdr());
        }
    }
    
    /**
     * Checks if obj is a list (ConsCell)
     * 
     * @param obj - the object to check if list
     * @return true if obj is a list, false if not
     */
    private boolean isList(SchemeObject obj){
        if(obj == null){
            return false;
        }
        switch(obj.getID()){
            case ConsCell.CONS_CELL_ID:{
                return true;
            }
            default:{
                return false;
            }
        }
    }
    
    /**
     * Initalizes and fills ArrayList of protected symbols
     * 
     * This is very tedious and is not done in a good way
     */
    private void initProtectedSymbols(){
        protectedSymbols = new ArrayList<String>();
        
        // string forms
        protectedSymbols.add(FORM_DEFINE);
        protectedSymbols.add(FORM_QUOTE);
        protectedSymbols.add(FORM_LET);
        protectedSymbols.add(FORM_LAMBDA);
        protectedSymbols.add(FORM_IF);
        
        // string functions
        protectedSymbols.add(FUNC_CAR);
        protectedSymbols.add(FUNC_CDR);
        protectedSymbols.add(FUNC_CONS);
        protectedSymbols.add(FUNC_LESSTHAN);
        protectedSymbols.add(FUNC_MULT);
        protectedSymbols.add(FUNC_NULL);
        protectedSymbols.add(FUNC_PLUS);
        protectedSymbols.add(FUNC_DIVI);
    }
    
    /**
     * Recursively sums the numbers in list, as integers
     * Assumes list only has integers
     * 
     * @param list - list of integers
     * @return sum of all the integers in list
     */
    private long sumIntegerList(ConsCell list){
        if(list == null){
            return 0;
        }
        return ((IntegerAtom)list.getCar()).getValue() + sumIntegerList((ConsCell)list.getCdr());
    }
    
    /**
     * Recursively sums the numbers in list, as reals
     * Automatically converts integers to reals
     * 
     * @param list - list of numbers to sum
     * @return sum of all the numbers in list
     * @throws EvaluatorException if list contains a non-number
     */
    private double sumRealList(ConsCell list) throws EvaluatorException{
        if(list == null){
            return 0.0;
        }
        return evaluateNum(list.getCar(),FUNC_PLUS) + sumRealList((ConsCell)list.getCdr());
    }
    
    //==================================================================+
    // PRIVATE ATTRIBUTES                                               |
    //==================================================================+
    
    private boolean debugMode;
    private int state;
    private SymbolTable symbolTable;
    private ArrayList<String> protectedSymbols;
    
    // constants
    private static final double THRESHOLD = 0.00000001; // for double comparisons
    
    // error messages
    private static final String BAD_ARGS_NUM_MESSAGE = "Incorrect number of arguments to ";
    private static final String BAD_ARGS_TYPE_MESSAGE = "Invalid argument types to ";
    private static final String BAD_EXPECTED_LAMBDA_MESSAGE = "Expected lambda expression";
    private static final String BAD_FUNC_EXPR_MESSAGE = "Expression does not start with a function";
    private static final String BAD_FUNC_ARGS_MESSAGE = "Expected arguments do not match given parameters";
    private static final String BAD_IF_MESSAGE = "Error evaluating condition to ";
    private static final String BAD_LIST_ARGS_MESSAGE = "Expression has badly formed argument list";
    private static final String BAD_SYM_CHOICE = "%s is a protected keyword";
    private static final String BAD_SYM_MESSAGE = "Symbol not found: ";
    
    // messages
    private static final String RESULT_LABEL = "Result Expression:";
    
    // string forms
    private static final String FORM_DEFINE = "define";
    private static final String FORM_QUOTE = "quote";
    private static final String FORM_LET = "let";
    private static final String FORM_LAMBDA = "lambda";
    private static final String FORM_IF = "if";
    
    // string functions
    private static final String FUNC_PLUS = "plus";
    private static final String FUNC_LESSTHAN = "lessthan";
    private static final String FUNC_CAR = "car";
    private static final String FUNC_CDR = "cdr";
    private static final String FUNC_CONS = "cons";
    private static final String FUNC_NULL = "isnull";
    private static final String FUNC_MULT = "mult";
    private static final String FUNC_SUBT = "subt";
    private static final String FUNC_DIVI = "divi";
    
}
