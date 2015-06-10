/*
 * CheatDetect.java
 *
 *
 */


import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author Cale Gibson
 */
public class CheatDetect {
               
	// Input: file name, start and end positions in the file
	// Output: file contents between start and end positions, inclusively, stored as a String
    private static String getStringfromFile(String name,int start,int end) throws java.io.IOException
     {
        
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(name));
        
        int ch;
        int  position = 0;
    
        while ( position != start )
        {
            ch = in.read();
            position++;
            if (ch == -1) throw new IOException("Invalid position range");
         }
        
        StringBuffer buf = new StringBuffer();
        
        while ( position <= end )
        {
            ch = in.read();
            position++;
            
            
            if (ch == -1) throw new IOException("Invalid position range");
            buf.append( (char) ch);  
        }

        in.close();
        return buf.toString();
    }

	public static void main(String[] args) throws java.io.IOException
	{
        //Check that argument count is correct
        if(args.length != 4)
        {
            System.out.println("Argument count incorrect");
            System.out.println("[USAGE] CheatDetect keywords file1 file2 N [USAGE]");
            System.out.println("Where N is number of matching variables");
            System.exit(0);
        }

        //Get the args and add them to appropriate variables
        String javaKeyWordsArg = args[0];
        String fileOneArg = args[1];
        String fileTwoArg = args[2];
        int matchingLimit = (new Integer(args[3])).intValue();

        //Read in the java keywords
        FileTokenRead javaKeyWords = new FileTokenRead(javaKeyWordsArg);

        //init the StringHashCode and HashDictionary()
        StringHashCode sH = new StringHashCode();
        HashDictionary hashDictionaryJavaKeywords = new HashDictionary(sH, (float)0.5);

        //Get the iterator to javaKeyWords object
        Iterator javaKeyWordsIterator = javaKeyWords.getIterator();

        //insert java keywords into the hash dictionary
        while(javaKeyWordsIterator.hasNext())
        {
            Object keyword = javaKeyWordsIterator.next();
            hashDictionaryJavaKeywords.insert(((Token)keyword).Value(), null);
        }

        /**
         * File One parsing and dictionary adding
         */
        //Read in java code from file 1
        FileTokenRead javaFileOne = new FileTokenRead(fileOneArg);
        //Create our fileone hash dictionary
        HashDictionary hashDictionaryJavaFileOne = new HashDictionary(sH, (float)0.5);
        Iterator javaFileOneIterator = javaFileOne.getIterator();

        //Iterate through the tokens from java file 1
        while(javaFileOneIterator.hasNext())
        {
            //Get the Object
            Object keyword = javaFileOneIterator.next();
            String value = ((Token)keyword).Value();

            //Get the start and end position to make a Pair object
            int startPos = ((Token)keyword).startPosition();
            int endPos = ((Token)keyword).endPosition();

            //Make the Pair object
            Pair p = new Pair(startPos, endPos);
            //If we find a match, than its a java keyword
            if(null != hashDictionaryJavaKeywords.find(value))
            {
                hashDictionaryJavaFileOne.insert(value, p);
            }
            //if not, its a user defined variable
            else
            {
                hashDictionaryJavaFileOne.insert("#", p);
            }
        }

        /**
         * File Two parsing and dictionary adding
         */

        //Read in java code from file 2
        FileTokenRead javaFilTwo = new FileTokenRead(fileTwoArg);

        //Create our filetwo hash dictionary
        HashDictionary hashDictionaryJavaFileTwo = new HashDictionary(sH, (float)0.5);
        Iterator javaFileTwoIterator = javaFilTwo.getIterator();

        //Iterate through the tokens from java file 2
        while(javaFileTwoIterator.hasNext())
        {
            //Get the Object
            Object keyword = javaFileTwoIterator.next();
            String value = ((Token)keyword).Value();

            //Get the start and end position to make a Pair object
            int startPos = ((Token)keyword).startPosition();
            int endPos = ((Token)keyword).endPosition();

            //Make the pair object
            Pair p = new Pair(startPos, endPos);
            //If we find a match, than its a java keyword
            if(null != hashDictionaryJavaKeywords.find(value))
            {
                hashDictionaryJavaFileTwo.insert(value, p);
            }
            //if not, its a user defined variable
            else
            {
                hashDictionaryJavaFileTwo.insert("#", p);
            }
        }

        //A hash dictionary to hold the possible subsequences of size n
        HashDictionary codeDictionary = new HashDictionary(sH, (float)0.5);

        //Variables needed to keep track of building code string
        int start = 0;
        int prevStart = 0;
        int previousStartIndex = 0;
        int end = 0;
        String codeString = "";
        int limitCount = 0;
        int indexCount = 0;

        //Loop through all entries
        for(int i = 0; i < hashDictionaryJavaFileOne.getHashTable().length; i++)
        {
            //if that entry is not null, loop through the linkedlist
            if(hashDictionaryJavaFileOne.getHashTable()[i] != null)
            {
                //Looping through the linkedlist
                for(int k = 0; k < hashDictionaryJavaFileOne.getHashTable()[i].size(); k++)
                {
                    //Get the entry, and if its start is equal to the last end, we know to build the code
                    Entry e = hashDictionaryJavaFileOne.getHashTable()[i].get(k);
                    if(e.Value().Start() == start)
                    {
                        //keeping track of start and end indexs
                        if(indexCount == 0)
                            prevStart = start;
                        if(indexCount == 1)
                            previousStartIndex = e.Value().Start();
                        indexCount++;
                        codeString += e.Key();
                        start = e.Value().End();
                        start++;
                        limitCount++;
                        //If we reach the match limit, break
                        if(limitCount == matchingLimit)
                        {
                            end = e.Value().End();
                            //reset the loop counts
                            i = 0;
                            k=0;
                            break;
                        }
                        //reset the loop counts
                        i = 0;
                        k=0;
                        break;
                    }
                }
                //Since we reached the match limit, add it to the dictionary
                if(limitCount == matchingLimit)
                {
                    Pair p = new Pair(prevStart, end);
                    codeDictionary.insert(codeString, p);
                    indexCount = 0;
                    limitCount = 0;
                    codeString = "";
                    start = previousStartIndex;
                }
            }
            //check to see if we are at the end, and if so, go back to the beginning and continue building the code to
            //insert into dictionary
            if(i == hashDictionaryJavaFileOne.getHashTable().length -1)
            {
                i = 0;
                start++;
                //if our start reaches 5000, break out, since for this project, code compared wont be longer than 5000 characters (assumed)
                if(start > 5000)
                    break;
            }
        }
        /**
         * Check file 2 against dictionary d in order to find a subsequence
         */

        //Variables needed to keep track of building code string
        start = 0;
        prevStart = 0;
        previousStartIndex = 0;
        end = 0;
        codeString = "";
        limitCount = 0;
        indexCount = 0;
        Pair pairFileTwo = new Pair(1,1);
        Pair pairFileOne = new Pair(1,1);
        boolean foundMatch = false;
        //Loop through all entries
        for(int i = 0; i < hashDictionaryJavaFileTwo.getHashTable().length; i++)
        {
            //if a match has been found, break out of the loop
            if(foundMatch)
                break;
            //if that entry is not null, loop through the linkedlist
            if(hashDictionaryJavaFileTwo.getHashTable()[i] != null)
            {
                //Looping through the linkedlist
                for(int k = 0; k < hashDictionaryJavaFileTwo.getHashTable()[i].size(); k++)
                {
                    //Get the entry, and if its start is equal to the last end, we know to build the code
                    Entry e = hashDictionaryJavaFileTwo.getHashTable()[i].get(k);
                    if(e.Value().Start() == start)
                    {
                        if(indexCount == 0)
                            prevStart = start;
                        if(indexCount == 1)
                            previousStartIndex = e.Value().Start();
                        indexCount++;
                        codeString += e.Key();
                        start = e.Value().End();
                        start++;
                        limitCount++;
                        //If we reach the match limit, break
                        if(limitCount == matchingLimit)
                        {
                            end = e.Value().End();
                            //reset the loop counts
                            i = 0;
                            k=0;
                            break;
                        }
                        //reset the loop counters
                        i = 0;
                        k=0;
                        break;
                    }
                }
                //If we reach the match limit, check to see if the code is already in the dictionary
                if(limitCount == matchingLimit)
                {
                    //if the code is in the dictionary, we know we have a match and can break.
                    if(codeDictionary.find(codeString)!= null)
                    {
                        Entry e = codeDictionary.find(codeString);
                        pairFileTwo = new Pair(prevStart, end);
                        pairFileOne = new Pair(e.Value().Start(), e.Value().End());
                        foundMatch = true;
                        break;
                    }
                    Pair p = new Pair(prevStart, end);
                    codeDictionary.insert(codeString, p);
                    indexCount = 0;
                    limitCount = 0;
                    codeString = "";
                    start = previousStartIndex;
                }
            }
            //check to see if we are at the end, and if so, go back to the beginning and continue building the code
            if(i == hashDictionaryJavaFileTwo.getHashTable().length -1)
            {
                //if a match was found, break out
                if(foundMatch)
                    break;
                i = 0;
                start++;
                if(start > 5000)
                    break;
            }
        }

        //If matches are found, display them to the screen
        if(foundMatch)
        {
            //Get the part of code that is a match and add it to a String variable
            String file1OutputString = getStringfromFile(fileOneArg, pairFileOne.Start(), pairFileOne.End());
            String file2OutputString = getStringfromFile(fileTwoArg, pairFileTwo.Start(), pairFileTwo.End());

            System.out.println("********* MATCHES FOUND *********\n");
            System.out.println("Match found in file1:");
            System.out.println(file1OutputString);
            System.out.println("\nMatch found in file2:");
            System.out.println(file2OutputString);
            System.out.println("\n*********************************\n");
        }
        //If no matches were found, display that to the screen
        else
            System.out.println("No matches found");

	}       
}
