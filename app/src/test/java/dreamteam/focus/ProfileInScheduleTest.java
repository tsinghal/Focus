package dreamteam.focus;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

/**
 * Created by bowie on 10/21/17.
 * Completed documentation in testing doc.
 */
public class ProfileInScheduleTest {
    private Profile profile1;
    private Profile profile2;
    private ProfileInSchedule pis;

    private static final class Constants {
        // GMT -8
        private static long TIME_0000 = 946713600000L;
        private static long TIME_0200 = 946720800000L;
        private static long TIME_0400 = 946728000000L;
        private static long TIME_0600 = 946735200000L;
        private static long TIME_0800 = 946742400000L;
        private static long TIME_1000 = 946749600000L;
        private static long TIME_1200 = 946756800000L;
    }

    private ArrayList<Repeat_Enum> repeat;

    @Before
    public void setUp() throws Exception {
        ArrayList<String> blacklist1 = new ArrayList<>();
        blacklist1.add("blacklist1.1");
        blacklist1.add("blacklist1.2");
        blacklist1.add("blacklist1.3");
        blacklist1.add("blacklist1.4");

        repeat = new ArrayList<>();
        repeat.add(Repeat_Enum.MONDAY);
        repeat.add(Repeat_Enum.TUESDAY);
        repeat.add(Repeat_Enum.SUNDAY);


        profile1 = new Profile("profile", blacklist1);
        profile2 = new Profile("profile2", blacklist1);
        pis = new ProfileInSchedule(
                profile1,
                new Date(Constants.TIME_0000),
                new Date(Constants.TIME_0400),
                repeat
        );
    }

    @Test
    public void setProfile() throws Exception {
        assertNotSame(profile1, profile2);
        assertEquals(profile1, pis.getProfile());
        pis.setProfile(profile2);
        assertEquals(profile2, pis.getProfile());
        pis.setProfile(profile1);
        assertEquals(profile1, pis.getProfile());
    }

    @Test
    public void setStartTime() throws Exception {
        assertEquals(new Date(Constants.TIME_0000), pis.getStartTime());
        pis.setStartTime(new Date(Constants.TIME_0200));
        assertEquals(new Date(Constants.TIME_0200), pis.getStartTime());
        pis.setStartTime(new Date(Constants.TIME_0000));
        assertEquals(new Date(Constants.TIME_0000), pis.getStartTime());
    }

    @Test
    public void setEndTime() throws Exception {
        assertEquals(new Date(Constants.TIME_0400), pis.getEndTime());
        pis.setEndTime(new Date(Constants.TIME_0600));
        assertEquals(new Date(Constants.TIME_0600), pis.getEndTime());
        pis.setEndTime(new Date(Constants.TIME_0200));
        assertEquals(new Date(Constants.TIME_0200), pis.getEndTime());
    }

    @Test
    public void repeatsOn() throws Exception {
        for (Repeat_Enum day : repeat) {
            assertTrue(pis.repeatsOn().contains(day));
        }
    }
}