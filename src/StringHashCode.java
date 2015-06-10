
/**
 * Created by Cale Gibson (͡° ͜ʖ͡°)
 * on 21/01/15.
 */
public class StringHashCode implements HashCode {
    @Override
    public int giveCode(Object o) {
        int hashcode = 2;
        int prime = 233;

        for(int i=0; i < ((String) o).length(); i++)
        {
            hashcode = prime * hashcode + ((String) o).charAt(i);
        }
        return hashcode;
    }
}
