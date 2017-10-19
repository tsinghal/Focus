package dreamteam.focus.client;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Tushar on 10/18/17.
 */

public class ArrangeAppsByName {
    // src : https://www.programcreek.com/2013/03/java-sort-map-by-value/

    public TreeMap<String, String> sortMapByValue(HashMap<String, String> map){
        Comparator<String> comparator = new ValueComparator(map);
        TreeMap<String, String> result = new TreeMap<String, String>(comparator);
        result.putAll(map);
        return result;
    }

    // a comparator that compares Strings
    class ValueComparator implements Comparator<String>{

        HashMap<String, String> map = new HashMap<String, String>();

        public ValueComparator(HashMap<String, String> map){
            this.map.putAll(map);
        }

        @Override
        public int compare(String s1, String s2) {
            return map.get(s1).compareTo(map.get(s2));
        }
    }
}
