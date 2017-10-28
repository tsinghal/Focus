package dreamteam.focus.client;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import static org.junit.Assert.*;

/**
 * Created by Tushar on 10/27/17.
 */
public class ArrangeAppsByNameTest {

    private TreeMap<String, String> treemap;
    private HashMap<String, String> map;
    private ArrayList<String> expectedOrder;
    @Before
    public void setUp() throws Exception {
        treemap = new TreeMap<String, String>();
        map = new HashMap<String, String>();

        map.put("com.whatsapp", "Whatsapp");
        map.put("com.messenger", "Messenger");
        map.put("com.eurosport", "Eurosport");
        map.put("com.google", "Google");
        map.put("com.abc", "ABC");

        expectedOrder = new ArrayList<>();
        expectedOrder.add("ABC");
        expectedOrder.add("Eurosport");
        expectedOrder.add("Google");
        expectedOrder.add("Messenger");
        expectedOrder.add("Whatsapp");
    }

    @Test
    public void sortMapByValueTest() throws Exception {
        ArrangeAppsByName arrange = new ArrangeAppsByName();
        treemap = arrange.sortMapByValue(map);

        ArrayList<String> orderedKeys = new ArrayList<>();
        for (String key : treemap.keySet()) {
            orderedKeys.add(key);
        }

        for(int i=0 ; i< orderedKeys.size(); i++){
            assertEquals(expectedOrder.get(i), treemap.get(orderedKeys.get(i)));
        }
    }

}