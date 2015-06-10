/* This program is for reading Tokens form input file                                                      */
/* Each Token consists  of a String value and an integer position                                          */
/* position specifies the beginning postition of the Token in the input file                               */
/* First open input file for reading: in  = new BufferedInputStream(new FileInputStream(String fileName)); */
/* Next create an object of class FileTokenRead: FileTokenRead readTokens = new FileTokenRead(in);         */
/* To read a token, first check that there is a next token in file: if ( readTokens.hasNextToken() )       */
/* Finally, to read a token, use:  Token nextToken = readToken.nextToken();                                */ 


import java.io.File;
import java.io.*;
import java.util.LinkedList;
import java.util.Iterator;

public class FileTokenRead{
    //private BufferedInputStream in;
    //private int leftoverChar;         // holds  character already read from file but not given to the user yet
    //private boolean endOfFile;        // true if reached the end of the file
    //private int position;             // holds current position in the file
    //private int tokenLength;          // holds length of the token last read from file
    
    private LinkedList<Token> tokens;
    
    // constructor which starts reading with the beginning of the file
    public FileTokenRead(String name) throws java.io.IOException
    {
        tokens = new LinkedList<Token>();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(name));

        // now parse the input file and put all tokens into the linked list
        
        int ch = in.read();
        int position = 0;
  
        
        //System.out.println();
        //System.out.println();
        //System.out.println();
        
        boolean  endOfFile = false;
        
        while ( ch != -1 )
        {
            while ( isWhiteSpace((char) ch) ){
                ch = in.read();
                position++;
                
                if ( ch == -1 ){
                    endOfFile = true;
                    break;
                }
            }
            
            if ( ! endOfFile )
            {
                StringBuffer buf = new StringBuffer();
                buf.append( (char) ch);  
            
                if ( !isLetter((char) ch ) ){  // token is a single non-letter character 
                    ch = in.read();
                    position++;
                }   
                else {
                    ch = in.read();
                    position++;
                
                    while ( isLetterNumberSpecialChar_((char) ch) )
                    {  // token starts with a letter and consits of consequitive letters and numbers
                        buf.append((char) ch);
             
                        ch = in.read();
                        position++;
                    }
                }
            
                tokens.addLast(new Token(buf.toString(),position-buf.length(),position-1));            
                //System.out.print(buf.toString());
                //System.out.print("#");
            }
        }
        
        in.close();        
    }

    public Iterator<Token> getIterator(){
        return(tokens.listIterator());
    }
    
            
    // this function checks if the input character is a letter
    private boolean isLetter( char in ){
        char nextChar = Character.toLowerCase((char) in);
        if ( (nextChar >= 'a' && nextChar <= 'z')  )
            return (true);
        else return(false);
    }

    private boolean isWhiteSpace( char in ){
        char nextChar = Character.toLowerCase((char) in);
        if ( (nextChar >= 33 && nextChar <= 126)  )
            return (false);
        else return(true);
    }

    // this function checks if the input character is a letter or a number
    private boolean isLetterNumberSpecialChar_( char in ){
        char nextChar = Character.toLowerCase((char) in);
        if ( (nextChar >= 'a' && nextChar <= 'z') || (nextChar >= '0' && (nextChar <= '9')) || nextChar == '_')
            return (true);
        else return(false);
    }
    

}





