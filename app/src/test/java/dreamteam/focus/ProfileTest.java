package dreamteam.focus;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by bowie on 10/21/17.
 * Complete documentation in testing doc.
 */
@SuppressWarnings("unused")
public class ProfileTest {
    private ArrayList<String> blacklist1;
    private Profile profile;

    @Before
    public void setUp() throws Exception {
        blacklist1 = new ArrayList<>();
        blacklist1.add("blacklist1.1");
        blacklist1.add("blacklist1.2");
        blacklist1.add("blacklist1.3");
        blacklist1.add("blacklist1.4");

        profile = new Profile("profile", blacklist1);
    }

    @Test
    public void setName() throws Exception {
        profile.setName("\"); profile = null; //");
        assertEquals("\"); profile = null; //", profile.getName());
        profile.setName("profile");
        assertEquals("profile", profile.getName());
    }

    @Test
    public void getApps() throws Exception {
        for (String app : blacklist1) {
            assertTrue(profile.getApps().contains(app));
        }
    }

    @Test
    public void setActive() throws Exception {
        profile.setActive(false);
        assertFalse(profile.isActive());
        profile.setActive(true);
        assertTrue(profile.isActive());
        profile.setActive(false);
        assertFalse(profile.isActive());
    }
}