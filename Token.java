/**
 * Tokens abstract class
 * 
 * @author Andre allan Ponce
 * Assignment 7
 * 2015-04-15:
 * + added TokenIDs
 * = changed from interface to abstract class
 */
 
 public abstract class Token{
     
     /**
      * debug token ID
      */
     public static final int TOKEN_ID = 0;
     
     /**
      * each Token has a TokenID, defined in each token
      * @return tokenID
      */
     public int getID(){
         return TOKEN_ID;
     }
      
     /**
      * retreives the tokenType of this token
      * @return TokenType of this token
      */
     public abstract TokenType getType();
     
     /**
      * converts the TokenType and value into a one-liner String
      * @return String -> TokenType:\t\t\tvalue
      */
     public abstract String toString();
}
