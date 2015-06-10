

/**
 * Created by Cale Gibson (͡° ͜ʖ͡°)
 * on 21/01/15.
 */
public class Entry {
    String  _key;
    Pair    _value;

    /**
     *
     * @param key Value to act as a key for the entry
     * @param value The Pair object to associate with the key
     */
    public Entry(String key, Pair value )
    {
        _key = key;
        _value = value;
    }

    /**
     *
     * @return Returns the key
     */
    public String Key()
    {
        return _key;
    }

    /**
     *
     * @return Returns the Pair object
     */
    public Pair Value()
    {
        return _value;
    }
}
