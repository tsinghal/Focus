package dreamteam.focus.server;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;

/**
 * Created by bowie on 10/15/17.
 */
public class DatabaseConnectorTest {
    private Context databaseContext;
    private DatabaseConnector db;

    private Profile profile1;
    private Profile profile2;
    private Profile profile3;

    private ProfileInSchedule pis1;
    private ProfileInSchedule pis2;
    private ProfileInSchedule pis3;

    private Schedule schedule1;
    private Schedule schedule2;
    private Schedule schedule3;

    private ArrayList<String> appBlacklist1;
    private ArrayList<String> appBlacklist2;
    private ArrayList<String> appBlacklist3;

    private ArrayList<Repeat_Enum> enum1;
    private ArrayList<Repeat_Enum> enum2;
    private ArrayList<Repeat_Enum> enum3;

    private ArrayList<ProfileInSchedule> cal1;
    private ArrayList<ProfileInSchedule> cal2;
    private ArrayList<ProfileInSchedule> cal3;

    private static long TIME_0800 = 946713600000L;
    private static long TIME_1000 = 946720800000L;
    private static long TIME_1200 = 946728000000L;
    private static long TIME_1400 = 946735200000L;
    private static long TIME_1600 = 946742400000L;
    private static long TIME_1800 = 946749600000L;
    private static long TIME_2000 = 946756800000L;

    @Before
    public void setUp() throws Exception {
        databaseContext = InstrumentationRegistry.getContext();
        db = new DatabaseConnector(databaseContext);
        populateFields();
    }

    private void populateFields() {
        appBlacklist1 = new ArrayList<>();
        appBlacklist1.add("appBlacklist1.1");
        appBlacklist1.add("appBlacklist1.2");
        appBlacklist1.add("appBlacklist1.3");

        appBlacklist2 = new ArrayList<>();
        appBlacklist2.add("appBlacklist2.1");
        appBlacklist2.add("appBlacklist2.2");
        appBlacklist2.add("appBlacklist2.3");
        appBlacklist2.add("appBlacklist2.4");

        appBlacklist3 = new ArrayList<>();
        appBlacklist3.add("appBlacklist3.1");
        appBlacklist3.add("appBlacklist3.2");
        appBlacklist3.add("appBlacklist3.3");
        appBlacklist3.add("appBlacklist3.4");
        appBlacklist3.add("appBlacklist3.5");

        enum1 = new ArrayList<>();
        enum1.add(Repeat_Enum.MONDAY);
        enum1.add(Repeat_Enum.WEDNESDAY);
        enum1.add(Repeat_Enum.FRIDAY);
        enum1.add(Repeat_Enum.SUNDAY);

        enum2 = new ArrayList<>();
        enum2.add(Repeat_Enum.TUESDAY);
        enum2.add(Repeat_Enum.WEDNESDAY);
        enum2.add(Repeat_Enum.THURSDAY);

        enum3 = new ArrayList<>();
        enum3.add(Repeat_Enum.MONDAY);
        enum3.add(Repeat_Enum.SATURDAY);
        enum3.add(Repeat_Enum.SUNDAY);

        cal1 = new ArrayList<>();
        cal1.add(pis1);

        cal2 = new ArrayList<>();
        cal2.add(pis1);
        cal2.add(pis2);

        cal3 = new ArrayList<>();
        cal3.add(pis1);
        cal3.add(pis2);
        cal3.add(pis3);


        profile1 = new Profile("profile1", appBlacklist1);
        profile2 = new Profile("profile2", appBlacklist2);
        profile3 = new Profile("profile3", appBlacklist3);

        pis1 = new ProfileInSchedule(profile1,
                new Date(TIME_1800), new Date(TIME_2000), enum1);
        pis2 = new ProfileInSchedule(profile2,
                new Date(TIME_0800), new Date(TIME_1200), enum2);
        pis3 = new ProfileInSchedule(profile3,
                new Date(TIME_1600), new Date(TIME_2000), enum3);

        schedule1 = new Schedule("schedule1", cal1);
        schedule2 = new Schedule("schedule2", cal2);
        schedule3 = new Schedule("schedule3", cal3);

        try {
            db.addSchedule(schedule1);
            db.addSchedule(schedule2);
            db.addSchedule(schedule3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("[FAILED] Adding schedule to database.");
        }
    }

    public static void main(String[] args) {

    }

    @After
    public void tearDown() throws Exception {
        System.out.println(" -- END OF TEST -- ");
    }

    @Test
    public void updateProfileInSchedule() throws Exception {

    }

    @Test
    public void removeProfileFromSchedule() throws Exception {

    }

    @Test
    public void getSchedules() throws Exception {
        ArrayList<Schedule> expected = new ArrayList<>();
        expected.add(schedule1);
        expected.add(schedule2);
        expected.add(schedule3);
        ArrayList<Schedule> schedule = db.getSchedules();
        Assert.assertEquals(expected, schedule);
    }

    @Test
    public void addBlockedNotification() throws Exception {

    }

    @Test
    public void getNotificationsCountForApp() throws Exception {

    }

    @Test
    public void getDatabaseVersion() throws Exception {

    }

}