

import java.util.*;

/**
 * Created by Cale Gibson (͡° ͜ʖ͡°)
 * on 21/01/15.
 */

//TODO check the probing stuff
public class HashDictionary implements Dictionary {

    /**
     *
     * @param limit prime number limit
     * @return a list of primes
     */
    private static ArrayList<Integer> primeSieveOfEratosthenes(int limit)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();

        boolean [] isComposite = new boolean [limit + 1];
        isComposite[1] = true;

        // Mark all composite numbers
        for (int i = 2; i <= limit; i++) {
            if (!isComposite[i]) {
                // 'i' is a prime number
                list.add(i);
                int multiple = 2;
                while (i * multiple <= limit) {
                    isComposite [i * multiple] = true;
                    multiple++;
                }
            }
        }
        return list;
    }

    //Class to house the array bucket and to keep track of items in the array bucket
    private class BucketOrder{

        //class variables
        private int _key;
        private int _pos;
        private String _value;
        private int _bucketIndex;

        //Getters
        public int getKey() {
            return _key;
        }

        public int getPos() {
            return _pos;
        }

        public String getValue() {
            return _value;
        }

        //Constructor
        private BucketOrder(int k, int p, int bucketIndex, String v)
        {
            _key = k;
            _pos = p;
            _bucketIndex = bucketIndex;
            _value = v;
        }
    }

    //Getter for hashtable so we can loop through items outside of this class
    public LinkedList<Entry>[] getHashTable() {
        return _hashTable;
    }

    //Class variables
    private int _bucketCount = 0;
    private LinkedList<Entry>[] _hashTable;
    private BucketOrder[] _arrayBucketOrder;
    private StringHashCode _stringHashCode;
    private float _inputLoadFactor;
    private long _probes = 0;
    private long _operations = 0;
    private int _size = 0;
    private ArrayList<Integer> _primes;

    /**
     * Blank constructor
     * @throws DictionaryException
     */
    public HashDictionary() throws DictionaryException
    {
        throw new DictionaryException("Ah ah ah, you didn't say the magic word!");
    }

    /**
     *
     * @param inputCode input code for hashing
     * @param inputLoadFactor the load factor used to determine if rehashing is needed
     */
    public HashDictionary(HashCode inputCode, float inputLoadFactor)
    {
        _stringHashCode = (StringHashCode)inputCode;
        _inputLoadFactor = inputLoadFactor;
        _hashTable = new LinkedList[7];
        _inputLoadFactor = inputLoadFactor;
        _arrayBucketOrder = new BucketOrder[15000];
        _primes = primeSieveOfEratosthenes(25000);
    }

    /**
     *
     * @return average number of probs preformed by hash dictionary
     */
    public long averNumProbes()
    {
        return _probes/_operations;
    }

    /**
     * rehash all entries when size increases
     */
    private void reHash()
    {
        LinkedList<Entry>[] tempHashTable;
        _arrayBucketOrder = new BucketOrder[15000];
        _bucketCount = 0;
        int nextSize = 0;
        for( int i = 0; i < _primes.size(); i++)
        {
            nextSize = _primes.get(i);
            if(nextSize > _hashTable.length * 2)
                break;
        }
        tempHashTable = new LinkedList[nextSize];
        for(int i = 0; i < _hashTable.length - 1; i++)
        {
            _probes++;
            if(null != _hashTable[i])
            {

                for(int j = 0; j < _hashTable[i].size(); j++)
                {
                    _probes++;
                    Entry tempEntry = _hashTable[i].get(j);
                    int key = _stringHashCode.giveCode(tempEntry.Key());
                    key = Math.abs(key % (tempHashTable.length - 1));

                    if(tempHashTable[key] == null)
                        tempHashTable[key] = new LinkedList<Entry>();

                    if(tempHashTable[key].size() >= 1)
                    {
                        BucketOrder b = new BucketOrder(key, tempHashTable[key].size()-1,_bucketCount, tempEntry.Key());
                        _arrayBucketOrder[_bucketCount] = b;
                        _bucketCount++;
                        _probes ++;
                        _operations++;
                    }
                    tempHashTable[key].add(tempEntry);
                    _probes++;
                    _operations++;
                    //_size++;
                }
            }
        }
        _hashTable = Arrays.copyOf(tempHashTable, nextSize);
    }

    /**
     *
     * @param k key to insert
     * @param v pair object to insert
     */
    @Override
    public void insert(String k, Pair v) {

        int arrayL = _hashTable.length;
        float something = (float)_size / (float)arrayL;
        if(something > _inputLoadFactor)
        {
            reHash();
        }

        try{
            int key = _stringHashCode.giveCode(k);
            key = Math.abs(key % (_hashTable.length - 1));

            Entry e = new Entry(k,v);
            if(_hashTable[key] == null)
                _hashTable[key] = new LinkedList<Entry>();

            if(_hashTable[key].size() >= 1)
            {
                int t = _hashTable[key].size();
                BucketOrder b = new BucketOrder(key, _hashTable[key].size()-1,_bucketCount, k);
                _arrayBucketOrder[_bucketCount] = b;
                _bucketCount++;
                _probes++;
            }
            _hashTable[key].add(e);
            _probes++;
            _operations++;
            _size++;
        }catch (Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     *
     * @param k key to remove
     * @return the removed entry object
     * @throws DictionaryException
     */
    @Override
    public Entry remove(String k) throws DictionaryException {

        int index=0;
        int hashKey = _stringHashCode.giveCode(k);
        hashKey = Math.abs(hashKey % (_hashTable.length - 1));
        for(int i = 0; i < _hashTable.length; i++)
        {
            _probes++;
            if(hashKey == i && _hashTable[hashKey] != null)
            {
                if(_hashTable[hashKey].size() > 1)
                {
                    for(Entry ent : _hashTable[hashKey])
                    {
                        _probes++;
                        if(ent.Key().equals(k))
                        {
                            Entry e = _hashTable[hashKey].remove(index);
                            _size--;
                            _operations++;
                            return e;
                        }
                        index++;
                    }
                }
                else
                {
                    _probes++;
                     Entry e = _hashTable[hashKey].remove(0);
                     _size--;
                     return e;
                }

            }
        }
        throw new DictionaryException("No Such Element");
    }

    /**
     *
     * @param k key to find
     * @return Entry object with key k
     */
    @Override
    public Entry find(String k) {
        int hashKey = _stringHashCode.giveCode(k);
        hashKey = Math.abs(hashKey % (_hashTable.length - 1));
        for(int i = 0; i < _hashTable.length; i++)
        {
            _probes++;
            if(hashKey == i && _hashTable[hashKey] != null)
            {
                if(_hashTable[hashKey].size() > 1 )
                {

                    for( BucketOrder b : _arrayBucketOrder)
                    {
                        _probes++;
                        if(null == b)
                            break;
                        if(b.getValue().equals(k))
                        {
                            Entry e = _hashTable[hashKey].get(b.getPos());
                            _operations++;
                            return e;
                        }
                    }
                    //Since the entry has a size greater than 1 and was the first entry in
                    Entry e = _hashTable[hashKey].get(0);
                    _probes++;
                    _operations++;
                    return e;
                }
                else if(_hashTable[hashKey].size() == 1 )
                {
                    Entry e = _hashTable[hashKey].get(0);
                    if(!e.Key().equals(k))
                    {
                        return null;
                    }
                    _probes++;
                    _operations++;
                    return e;
                }
            }
        }
        return null;
    }

    /**
     *
     * @return size of hashtable
     */
    @Override
    public int size() {
        return _size;
    }
}
