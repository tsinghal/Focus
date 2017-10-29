package dreamteam.focus;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by bowie on 10/29/17.
 * Test suite for Schedule test
 */
public class ScheduleTest {
    private Schedule schedule1;
    private ArrayList<ProfileInSchedule> cal1;

    @Before
    public void setUp() throws Exception {
        ArrayList<String> appBlacklist1 = new ArrayList<>();
        appBlacklist1.add("com.facebook.orca");

        ArrayList<Repeat_Enum> enum1 = new ArrayList<>();
        enum1.add(Repeat_Enum.MONDAY);

        Profile profile1 = new Profile("profile0", appBlacklist1);


        ProfileInSchedule pis1 = new ProfileInSchedule(profile1,
                new Date(946713600000L), new Date(946720800000L), enum1);

        cal1 = new ArrayList<>();
        cal1.add(pis1);

        schedule1 = new Schedule("schedule1", cal1);
    }

    @Test
    public void getCalendar() throws Exception {
        assertEquals(cal1, schedule1.getCalendar());
    }

    @Test
    public void setName() throws Exception {
        assertEquals("schedule1", schedule1.getName());
        schedule1.setName("s");
        assertEquals("s", schedule1.getName());
    }

    @Test
    public void setActive() throws Exception {
        schedule1.setActive(false);
        assertFalse(schedule1.isActive());
        schedule1.setActive(true);
        assertTrue(schedule1.isActive());
        schedule1.setActive(false);
        assertFalse(schedule1.isActive());
    }

}