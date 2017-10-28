package dreamteam.focus.server;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.MainActivity;

/**
 * Created by bowie on 10/21/17.
 * Complete documentation in testing doc.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class BackgroundServiceTest {
    private static final String WHATSAPP = "com.whatsapp";
    private static final String FACEBOOK_MESSENGER = "com.facebook.orca";
    private static final String BAD_SCHEDULE_NAME = "dfsjdofiewoiheegeg";
    private static final String GOOD_SCHEDULE_NAME = "test123";
    private static final String ANONYMOUS_SCHEDULE = "AnonymousSchedule";
    private Profile profile1;
    private Profile profile2;
    private Profile profile3;

    private ProfileInSchedule pis1;
    private ProfileInSchedule pis2;
    private ProfileInSchedule pis3;

    private Schedule schedule1;
    private Schedule schedule2;
    private Schedule schedule3;
    private Schedule anonymousSchedule;
    private Schedule goodSchedule;

    private ArrayList<String> appBlacklist1;
    private ArrayList<String> appBlacklist2;
    private ArrayList<String> appBlacklist3;

    private ArrayList<Repeat_Enum> enum1;
    private ArrayList<Repeat_Enum> enum2;
    private ArrayList<Repeat_Enum> enum3;

    private ArrayList<ProfileInSchedule> cal1;
    private ArrayList<ProfileInSchedule> cal2;
    private ArrayList<ProfileInSchedule> cal3;

    private DatabaseConnector db;

    private int version = -1;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        db = new DatabaseConnector(InstrumentationRegistry.getTargetContext());
        db.clear();

        appBlacklist1 = new ArrayList<>();
        appBlacklist1.add("com.facebook.orca");
        appBlacklist1.add("com.whatsapp");
        appBlacklist1.add("com.facebook.katana");
        appBlacklist2 = new ArrayList<>();
        appBlacklist2.add("com.facebook.orca");
        appBlacklist3 = new ArrayList<>();
        appBlacklist3.add("com.facebook.orca");
        appBlacklist3.add("com.whatsapp");

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

        profile1 = new Profile("profile1", appBlacklist1);
        profile2 = new Profile("profile2", appBlacklist2);
        profile3 = new Profile("profile3", appBlacklist3);

        pis1 = new ProfileInSchedule(profile1,
                new Date(Constants.TIME_1000), new Date(Constants.TIME_1200), enum1);
        pis2 = new ProfileInSchedule(profile2,
                new Date(Constants.TIME_0000), new Date(Constants.TIME_0400), enum2);
        pis3 = new ProfileInSchedule(profile3,
                new Date(Constants.TIME_0800), new Date(Constants.TIME_1200), enum3);

        cal1 = new ArrayList<>();
        cal1.add(pis1);
        cal2 = new ArrayList<>();
        cal2.add(pis1);
        cal2.add(pis2);
        cal3 = new ArrayList<>();
        cal3.add(pis1);
        cal3.add(pis2);
        cal3.add(pis3);

        schedule1 = new Schedule("schedule1", cal1);
        schedule2 = new Schedule("schedule2", cal2);
        schedule3 = new Schedule("schedule3", cal3);
        anonymousSchedule = new Schedule(ANONYMOUS_SCHEDULE);
        goodSchedule = new Schedule(GOOD_SCHEDULE_NAME, cal3);


        db.addSchedule(schedule1);
        db.addSchedule(schedule2);
        db.addSchedule(goodSchedule);


        version = db.getDatabaseVersion();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onStartCommand() throws Exception {

    }

    @Test
    public void onNotificationPosted() throws Exception {

    }

    @Test
    public void onNotificationRemoved() throws Exception {

    }

    @Test
    public void tick() throws Exception {

    }
}