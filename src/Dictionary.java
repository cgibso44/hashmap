
//Interface your dictionary has to implement


public interface Dictionary {
   
    // Inserts the entry with the specified key and value to the dictionary. 
    public void insert(String k, Pair v);
    
    // Removes the entry with the specified key from the dictionary. Throws 
    // DictionaryException if no entry with key in the dictionary          
    public Entry remove(String k) throws DictionaryException;

    // If entry with this key exists in the dictionary, returns this entry
    // otherwise returns null
    public Entry find(String k);  
    
    public int size();
}

