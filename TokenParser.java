/**
 * Parser for parsing ArrayList of tokens and turning it into syntax tree
 * 
 * @author Andre Allan Ponce
 * Assignment 7
 */

import java.util.List;

public class TokenParser{
	
	public TokenParser(String joke){}
	
	//==================================================================+
	// PUBLIC METHODS												  	|
	//==================================================================+
	
	public SchemeObject getTree(){
		return tree;
	}
		
	public void init(boolean debugMode){
		stringCreationLayer = 0;
		//tree = new SyntaxTree(debugMode);
		state = STATE_READY;
		this.debugMode = debugMode;
	}
	
	public void parse(List<Token> tokens) throws ParserException{
		tree = parseExpression(tokens);
	}
	
	public String toString(){
		return "\n" 
			+ generateTree() + "\n" 
			+ generateList() + "\n\n"
			+ generateExpr() + "\n";
	}
	
	//==================================================================+
	// PRIVATE METHODS												  	|
	//==================================================================+
	
	/**
	 * Create spaces for the tree based on the depth we are in
	 * @return string of spaces
	 */
	private String createSpaces(){
		String space = "";
		for(int i = 0; i < stringCreationLayer; i++){
			space = space + "  ";
		}
		return space;
	}
	
	/**
	 * Builds a string representation of the tree in tree form
	 * @param o - a node of the tree to print out
	 * @return string representation of node o
	 */
	private String createString(SchemeObject o){
		String temp = "";
		String spaces = createSpaces();
		if(o == null){
			return spaces + NULL_STRING + "\n";
		}
		switch(o.getID()){
			case ConsCell.CONS_CELL_ID:{
				stringCreationLayer++;
				ConsCell cell = (ConsCell)o;
				temp = temp + spaces + ConsCell.CONS_CAR_MESSAGE +"\n"
					+ createString(cell.getCar())
					+ spaces + ConsCell.CONS_CDR_MESSAGE + "\n"
					+ createString(cell.getCdr());
				stringCreationLayer--;
				break;
			}
			default:{
				temp = temp + spaces + o.toString()+"\n";
			}
		}
		return temp;
	}
	
	/**
	 * Create string reprsentation of the tree as an expression
	 * @param o - a node of the tree to print out
	 * @return String representation of node o
	 */
	private String createStringExpr(SchemeObject o){
		String temp = "";
		switch(o.getID()){
			case ConsCell.CONS_CELL_ID:{
				ConsCell cell = (ConsCell)o;
				boolean isList = cell.getCar().getID() == ConsCell.CONS_CELL_ID;
				if(isList){
					temp = temp + "(";
				}
				temp = temp + createStringExpr(cell.getCar());
				if(isList){
					temp = temp + ")";
				}
				if(cell.getCdr() != null){
					temp = temp + " " + createStringExpr(cell.getCdr());
				}
				break;
			}
			case SymbolAtom.SYMBOL_ATOM_ID:{
				temp = ((SymbolAtom)o).getValue();
				break;
			}
			case BooleanAtom.BOOLEAN_ATOM_ID:{
				temp = ""+((BooleanAtom)o).getValue();
				break;
			}
			case IntegerAtom.INTEGER_ATOM_ID:{
				temp = ""+((IntegerAtom)o).getValue();
				break;
			}
			case RealAtom.REAL_ATOM_ID:{
				temp = ""+((RealAtom)o).getValue();
				break;
			}
			case EmptyListObject.EMPTY_LIST_ID:{
				temp = ""+((EmptyListObject)o).getValue();
				break;
			}
			default:{
				temp = NULL_STRING;
				break;
			}
		}
		return temp;
	}
	
	/**
	 * creates a string representation of the tree as a list
	 * @param o - the node to print out
	 * @return String representation of node o
	 */
	private String createStringList(SchemeObject o){
		String temp = "";
		switch(o.getID()){
			case ConsCell.CONS_CELL_ID:{
				ConsCell cell = (ConsCell)o;
				boolean isList = cell.getCar().getID() == ConsCell.CONS_CELL_ID;
				if(isList){
					temp = temp + "(";
				}
				temp = temp + createStringList(cell.getCar());
				if(isList){
					temp = temp + ")";
				}
				if(cell.getCdr() != null){
					temp = temp + " " + createStringList(cell.getCdr());
				}
				break;
			}
			default:{
				temp = o.toString();
				break;
			}
		}
		return temp;
	}
	
	/**
	 * Finds the index of the end of a quote expression
	 * Index 0 is considered the beginning of the quote expression "'"
	 * The end index is the first encountered atom, close paren on the first encountered empty list, or close paren on the first encountered ( expression ) 
	 * 
	 * @param tokens - list of tokens
	 * @return index of the end of quote expression
	 * @throws ParserException if an umatched close paren is found; see findMatchingParen
	 */
	private int findEndQuote(List<Token> tokens) throws ParserException{
		int i = 0;
		boolean foundEndAtom = false;
		while(i < tokens.size() && !foundEndAtom){
			if(tokens.get(i).getID() == PunctuationToken.PUNCTUATION_TOKEN_ID){
				PunctuationToken tempToken = (PunctuationToken)tokens.get(i);
				if(tempToken.getValue() == PunctuationToken.PUNCTUATION_TOKEN_OPEN){
					i += findMatchingParen(tokens.subList(i+1,tokens.size()));
					foundEndAtom = true;
				}
				else if(tempToken.getValue() == PunctuationToken.PUNCTUATION_TOKEN_CLOSED){
					state = STATE_ERROR;
					throw new ParserException(BAD_LIST_MESSAGE);
				}
			}
			else{
				foundEndAtom = true;
			}
			++i;
		}
		if(!foundEndAtom){
			state = STATE_ERROR;
			throw new ParserException(SINGLE_QUOTE_MESSAGE);
		}
		return i;
	}
	
	/**
	 * Finds the index of the matching paren of a paren expression
	 * Index 0 is considered the beginning of the paren expression '('
	 * The end index is the matching close-paren
	 * 
	 * @param tokens - list of tokens
	 * @return index of the matching close-paren
	 * @throws ParserException if no matching close-paren is found
	 */
	private int findMatchingParen(List<Token> tokens) throws ParserException{
		int parenCounter = 1;
		int i = 0;
		while(parenCounter > 0 && i < tokens.size()){
			if(tokens.get(i).getID() == PunctuationToken.PUNCTUATION_TOKEN_ID){
				if(((PunctuationToken)tokens.get(i)).getValue() == PunctuationToken.PUNCTUATION_TOKEN_OPEN){
					parenCounter++;
				}
				else if(((PunctuationToken)tokens.get(i)).getValue() == PunctuationToken.PUNCTUATION_TOKEN_CLOSED){
					parenCounter--;
				}
			}
			++i;
		}
		if(parenCounter != 0){
			state = STATE_ERROR;
			throw new ParserException(BAD_LIST_MESSAGE);
		}
		return i;
	}
	
	/**
	 * Generates parse tree in expression form
	 * @return string representation of parse tree in expression form
	 */
	private String generateExpr(){
		String result = "";
		result = LABEL_EXPR + "\n";
		boolean isList = tree.getID() == ConsCell.CONS_CELL_ID;
		if(isList){
			result = result + "(";
		}
		result = result + createStringExpr(tree);
		if(isList){
			result = result + ")";
		}
		return result;
	}
	
	/**
	 * Generates parse tree in list form
	 * @return string representation of parse tree in list form
	 */
	private String generateList(){
		String result = "";
		result = LABEL_LIST + "\n";
		boolean isList = tree.getID() == ConsCell.CONS_CELL_ID;
		if(isList){
			result = result + "(";
		}
		result = result + createStringList(tree);
		if(isList){
			result = result + ")";
		}
		return result;
	}
	
	/**
	 * Generates parse tree in tree form
	 * @return string representation of parse tree in tree form
	 */
	private String generateTree(){
		String result = "";
		result = LABEL_TREE + "\n"
			+ createString(tree);
		return result;
	}
	
	/**
	 * Parses the incoming token and returns its atom equivalent
	 * 
	 * @param t - token to parse
	 * @return atom equivalent of t
	 * @throws ParserException if the incoming token has no atom equivalent
	 */
	private SchemeObject parseAtom(Token t) throws ParserException{
		if(debugMode){
			System.out.println(t.toString());
		}
		SchemeObject o;
		switch(t.getID()){
			case SymbolToken.SYMBOL_TOKEN_ID:{
				o = new SymbolAtom(((SymbolToken)t).getValue());
				break;
			}
			case BooleanToken.BOOLEAN_TOKEN_ID:{
				o = new BooleanAtom(((BooleanToken)t).getValue());
				break;
			}
			case IntegerToken.INTEGER_TOKEN_ID:{
				o = new IntegerAtom(((IntegerToken)t).getValue());
				break;
			}
			case RealToken.REAL_TOKEN_ID:{
				o = new RealAtom(((RealToken)t).getValue());
				break;
			}
			default:{
				state = STATE_ERROR;
				throw new ParserException(BAD_ATOM_MESSAGE + t.toString());
			}
		}
		return o;
	}
	
	/**
	 * Parses incoming token list for an emptyList ()
	 * 
	 * @param tokens - list of tokens
	 * @return empty list object
	 * @throws ParserException if the incoming token list does not consist of () 
	 */
	private SchemeObject parseEmptyList(List<Token> tokens) throws ParserException{
		Token leftToken = tokens.get(0);
		Token rightToken = tokens.get(1);
		if(leftToken.getID() == PunctuationToken.PUNCTUATION_TOKEN_ID && rightToken.getID() == PunctuationToken.PUNCTUATION_TOKEN_ID){
			PunctuationToken leftTempToken = (PunctuationToken)leftToken;
			PunctuationToken rightTempToken = (PunctuationToken)rightToken;
			if(leftTempToken.getValue() == PunctuationToken.PUNCTUATION_TOKEN_OPEN && rightTempToken.getValue() == PunctuationToken.PUNCTUATION_TOKEN_CLOSED){
				return new EmptyListObject();
			}
		}
		state = STATE_ERROR;
		throw new ParserException(BAD_LIST_MESSAGE);
	}
	
	/**
	 * Recursivly parses an expression, given by the incoming list of tokens
	 * 
	 * @param tokens - list of tokens to parse as an expression
	 * @return the root of this expression as a syntax tree 
	 * @throws ParserException if there are extra tokens that cannot be parsed; see parsePunctuationToken, parseAtom, parseEmptyList
	 */
	private SchemeObject parseExpression(List<Token> tokens) throws ParserException{
		if(debugMode){
			System.out.print("current_state: "+state+"; ");
			System.out.println("SIZE: "+tokens.size()+";");
		}
		SchemeObject o = null;
		if(tokens.size() > 0){
			switch(state){
				case STATE_DONE:{
					state = STATE_ERROR;
					throw new ParserException(TOO_MANY_TOKENS_MESSAGE);
				}
			}
			Token currentToken = tokens.get(0);
			if(debugMode){
				System.out.println("ID: "+currentToken.getID());
			}
			switch(currentToken.getID()){
				case PunctuationToken.PUNCTUATION_TOKEN_ID:{
					PunctuationToken tempToken = (PunctuationToken)currentToken;
					o = parsePunctuationToken(tokens,tempToken);
					break;
				}
				default:{ // we found an atom
					switch(state){
						case STATE_READY:{
							if(tokens.size() > 1){
								state = STATE_ERROR;
								throw new ParserException(TOO_MANY_TOKENS_MESSAGE);
							}
							o = parseAtom(currentToken);
							state = STATE_ATOM;
							break;
						}
						default:{
							state = STATE_ATOM;
							SchemeObject car = parseAtom(currentToken);
							SchemeObject cdr;
							if(tokens.size() > 1){
								cdr = parseExpression(tokens.subList(1,tokens.size()));
							}
							else{
								cdr = null;
							}
							o = new ConsCell(car,cdr);
							break;
						}
					}
					break;
				}
			}
		}
		return o;
	}
	
	/**
	 * Parses an incoming punctuation token as well as the list of tokens for expressions and atoms
	 * 
	 * @param tokens - list of tokens
	 * @param t - incoming PunctuationToken
	 * @return root of the syntax tree of parsed expressions and atoms
	 * @throws ParserException if there are tokens that cannot be parsed or if there is an unclosed list; see parseExpression, findMatchingParen, parseQuoteToken
	 */
	private SchemeObject parsePunctuationToken(List<Token> tokens, PunctuationToken t) throws ParserException{
		SchemeObject o = null;
		if(t.getValue() == PunctuationToken.PUNCTUATION_TOKEN_OPEN){
			int i = findMatchingParen(tokens.subList(1,tokens.size()));
			switch(state){
				case STATE_READY:{
					if(i < tokens.size()-1){
						state = STATE_ERROR;
						throw new ParserException(TOO_MANY_TOKENS_MESSAGE);
					}
				}
			}
			if(i == 1){
				SchemeObject emptyList = parseEmptyList(tokens.subList(0,2));
				switch(state){
					case STATE_READY:{
						o = emptyList;
						break;
					}
					default:{
						if(tokens.size() > 2){
							o = new ConsCell(emptyList,parseExpression(tokens.subList(2,tokens.size())));
						}
						else{
							o = new ConsCell(emptyList,null);
						}
						break;
					}
				}

			}
			else{
				if(debugMode){
					System.out.println("found_expr_at["+1+","+i+"]");
				}
				SchemeObject car = null;
				SchemeObject cdr = null;
				switch(state){
					case STATE_ATOM:
					case STATE_LIST_EXPR:
					case STATE_QUOTE_EXPR:{
						SchemeObject preCar = parseExpression(tokens.subList(1,i));
						if(preCar.getID() == ConsCell.CONS_CELL_ID){
							car = preCar;
						}
						else{
							car = new ConsCell(preCar,null);
						}
						if(tokens.size()-1 > i){
							cdr = parseExpression(tokens.subList(i+1,tokens.size()));
						}
						else{
							cdr = null;
						}
						o = new ConsCell(car,cdr);
						break;
					}
					case STATE_LIST_READY:{
						car = parseExpression(tokens.subList(1,i));
						if(tokens.size()-1 > i){
							cdr = parseExpression(tokens.subList(i+1, tokens.size()));
						}
						else{
							cdr = null;
							state = STATE_ATOM;
						}
						o = new ConsCell(car,cdr);
						break;
					}
					case STATE_READY:{
						state = STATE_LIST_READY;
						car = parseExpression(tokens.subList(1,i));
						if(tokens.size()-1 > i){
							cdr = parseExpression(tokens.subList(i, tokens.size()));
						}
						else{
							cdr = null;
							state = STATE_DONE;
						}
						o = car;
						break;
					}
					default:{
						state = STATE_ERROR;
						throw new ParserException();
					}
				}
				
			}
		}
		else if(t.getValue() == SymbolAtom.SYMBOL_QUOTE_VALUE){
			o = parseQuoteToken(tokens,t);
		}
		else{
			state = STATE_ERROR;
			throw new ParserException(BAD_LIST_MESSAGE);
		}
		return o;
	}
	
	/**
	 * Parses an incoming quote token and expression
	 * 
	 * @param tokens - list of tokens
	 * @param t - incoming quote token
	 * @return root of syntax tree of quote expression
	 * @throws ParserException if there are tokens that cannot be parsed or if a quote had no ending atom or expression; see findEndQuote, parseExpression
	 */
	private SchemeObject parseQuoteToken(List<Token> tokens, PunctuationToken t) throws ParserException{
		if(tokens.size() < 2){
			state = STATE_ERROR;
			throw new ParserException(SINGLE_QUOTE_MESSAGE);
		}
		int i;
		if(tokens.get(1).getID() == PunctuationToken.PUNCTUATION_TOKEN_ID){
			i = 1+findEndQuote(tokens.subList(1,tokens.size()));
		}
		else{
			i = 2;
		}
		if(debugMode){
			System.out.println("found_q_expr_at["+1+","+i+"]");
		}
		SchemeObject car = null;
		SchemeObject cdr = null;
		switch(state){
			case STATE_READY:{
				if(tokens.size() > i){
					state = STATE_ERROR;
					throw new ParserException(TOO_MANY_TOKENS_MESSAGE);
				}
				state = STATE_QUOTE_EXPR;
				car = new SymbolAtom(SymbolAtom.SYMBOL_QUOTE);
				cdr = parseExpression(tokens.subList(1,i));
				state = STATE_DONE;
				break;
			}
			case STATE_LIST_READY:{
				state = STATE_QUOTE_EXPR;
				car = new ConsCell(new SymbolAtom(SymbolAtom.SYMBOL_QUOTE),parseExpression(tokens.subList(1,i)));
				cdr = parseExpression(tokens.subList(i,tokens.size()));
				state = STATE_DONE;
				break;
			}
			case STATE_LIST_EXPR:
			case STATE_QUOTE_EXPR:
			case STATE_ATOM:{
				state = STATE_QUOTE_EXPR;
				car = new ConsCell(new SymbolAtom(SymbolAtom.SYMBOL_QUOTE),parseExpression(tokens.subList(1,i)));
				cdr = parseExpression(tokens.subList(i,tokens.size()));
				state = STATE_ATOM;
				break;
			}
			default:{
				state = STATE_ERROR;
				throw new ParserException();
			}
		}
		return new ConsCell(car,cdr);
	}
	
	//==================================================================+
	// PRIVATE ATTRIBUTES											  	|
	//==================================================================+
	
	private boolean debugMode;
	private SchemeObject tree;
	private int state;
	private int stringCreationLayer = 0;
	
	// states
	private static final int STATE_ERROR = -1;
	private static final int STATE_READY = 0;
	private static final int STATE_QUOTE_EXPR = 1;
	private static final int STATE_LIST_EXPR = 2;
	private static final int STATE_ATOM = 3;
	private static final int STATE_LIST_READY = 4;
	private static final int STATE_DONE = 6;
	
	// exception messages
	private static final String BAD_ATOM_MESSAGE = "Invalid Atom: ";
	private static final String BAD_LIST_MESSAGE = "Unclosed List";
	private static final String TOO_MANY_TOKENS_MESSAGE = "Unconsumed tokens at end of expression";
	private static final String SINGLE_QUOTE_MESSAGE = "Expected Atom, found \'";
	
	// parser labels for toString
	private static final String LABEL_TREE = "Parser Result in tree form:";
	private static final String LABEL_LIST = "Parser Result in list form:";
	private static final String LABEL_EXPR = "Parser Result as Scheme expression:";
	
	// more toString stuff
	private static final String NULL_STRING = "NULL";
}
