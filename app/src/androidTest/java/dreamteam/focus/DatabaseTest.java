package dreamteam.focus;

import android.content.Context;
import android.database.SQLException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import dreamteam.focus.server.DatabaseConnector;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    Context appContext = InstrumentationRegistry.getTargetContext();
    DatabaseConnector db = new DatabaseConnector(appContext);

    ArrayList<String> blockedApps1, blockedApps2, blockedApps3;
    Profile profile1, profile2, profile3;
    Schedule schedule1, schedule2;
    ArrayList<Repeat_Enum> re1, re2, re3;
    ProfileInSchedule pis1, pis2, pis3;

    private void populateDatabase() {
        blockedApps1 = new ArrayList<>();
        blockedApps2 = new ArrayList<>();
        blockedApps3 = new ArrayList<>();

        blockedApps2.add("com.google.android.youtube");
        blockedApps2.add("com.google.android.ext.services");

        blockedApps3.add("com.google.android.youtube");
        blockedApps3.add("com.example.android.livecubes");

        profile1 = new Profile("Empty Profile", blockedApps1);
        profile2 = new Profile("Youtube + Services Profile", blockedApps2);
        profile3 = new Profile("Youtube + Livecubes Profile", blockedApps3);

        schedule1 = new Schedule("Schedule 1");
        schedule2 = new Schedule("Schedule 2");

        re1 = new ArrayList<Repeat_Enum>();
        re1.add(Repeat_Enum.MONDAY);

        re2 = new ArrayList<Repeat_Enum>();
        re2.add(Repeat_Enum.TUESDAY);

        re3 = new ArrayList<Repeat_Enum>();
        re3.add(Repeat_Enum.WEDNESDAY);

        try {
            pis1 = new ProfileInSchedule(profile1, db.getDate("1970-01-01T21:31:00Z"), db.getDate("1970-01-01T22:31:00Z"), re1);
            pis2 = new ProfileInSchedule(profile2, db.getDate("1970-01-01T22:31:00Z"), db.getDate("1970-01-01T23:31:00Z"), re2);
            pis3 = new ProfileInSchedule(profile3, db.getDate("1970-01-01T18:31:00Z"), db.getDate("1970-01-01T19:31:00Z"), re3);
        } catch (ParseException e) {
            Log.d("Populate Error", e.getLocalizedMessage());
        }

        // Uncomment the following to create in database
//        db.createProfile(profile1);
//        db.createProfile(profile2);
//        db.createProfile(profile3);
//
//        db.addSchedule(schedule1);
//        db.addSchedule(schedule2);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        assertEquals("dreamteam.focus", appContext.getPackageName());
    }

    @Test
    public void getProfiles_isEmpty() throws Exception {
        db.clear();
        populateDatabase();
        assertEquals(db.getProfiles().size(), 0);
    }

    @Test
    public void getProfiles_addProfile() throws Exception {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);

        ArrayList<Profile> profiles = db.getProfiles();

        assertEquals(profiles.size(), 1);
        assertEquals(profiles.get(0).getName(), profile1.getName());
        assertEquals(profiles.get(0).getApps(), profile1.getApps());
        assertEquals(profiles.get(0).isActive(), profile1.isActive());
    }

    @Test
    public void getProfiles_removeProfile() throws Exception {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);
        assertEquals(db.getProfiles().size(), 1);

        db.removeProfile(profile1.getName());
        assertEquals(db.getProfiles().size(), 0);
    }

    @Test
    public void getProfiles_activateProfile() throws Exception {
        db.clear();
        populateDatabase();

        db.createProfile(profile2);
        assertEquals(db.getProfiles().get(0).isActive(), false);

        pis2 = new ProfileInSchedule(profile2, db.getDate("1970-01-01T21:31:00Z"), db.getDate("1970-01-01T22:31:00Z"));
        db.activateProfile(pis2);
        assertEquals(db.getProfiles().get(0).isActive(), true);
    }

    @Test
    public void getProfiles_deactivateProfile() throws Exception {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);
        assertEquals(db.getProfiles().get(0).isActive(), false);

        pis1 = new ProfileInSchedule(profile1, db.getDate("1970-01-01T21:31:00Z"), db.getDate("1970-01-01T22:31:00Z"));
        db.activateProfile(pis1);
        assertEquals(db.getProfiles().get(0).isActive(), true);

        db.deactivateProfile(profile1);
        assertEquals(db.getProfiles().get(0).isActive(), false);
    }

    @Test
    public void getProfiles_updateProfileName() throws Exception {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);

        ArrayList<Profile> profiles = db.getProfiles();

        assertEquals(profiles.get(0).getName(), profile1.getName());

        String oldProfileName = profile1.getName();
        profile1.setName(profile1.getName() + " updated");

        db.updateProfile(oldProfileName, profile1);
        assertEquals(db.getProfiles().get(0).getName(), profile1.getName());
    }

    @Test
    public void getProfiles_updateProfileNameCheckSchedule() throws Exception {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);
        db.addSchedule(schedule1);
        db.addProfileInSchedule(pis1, schedule1.getName());

        String oldProfileName = profile1.getName();
        profile1.setName(profile1.getName() + " updated");

        db.updateProfile(oldProfileName, profile1);

        assertEquals(db.getSchedules().get(0).getCalendar().get(0).getProfile().getName(), profile1.getName());
    }

    @Test
    public void getProfiles_updateProfileBlockedApps() throws Exception {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);

        ArrayList<Profile> profiles = db.getProfiles();

        assertEquals(profiles.get(0).getApps(), profile1.getApps());

        db.updateProfile(profile1.getName(), profile2);

        profiles = db.getProfiles();

        assertEquals(profiles.get(0).getName(), profile2.getName());
        assertEquals(profiles.get(0).getApps(), profile2.getApps());
    }

    @Test
    public void getSchedules_isEmpty() throws Exception {
        db.clear();
        populateDatabase();
        assertEquals(db.getSchedules().size(), 0);
    }

    @Test
    public void getSchedules_addSchedule() throws Exception {
        db.clear();
        populateDatabase();

        db.addSchedule(schedule1);

        ArrayList<Schedule> schedules = db.getSchedules();

        assertEquals(schedules.size(), 1);
        assertEquals(schedules.get(0).getName(), schedule1.getName());
        assertEquals(schedules.get(0).getCalendar(), schedule1.getCalendar());
        assertEquals(schedules.get(0).isActive(), schedule1.isActive());
    }

    @Test
    public void getSchedules_removeSchedule() throws Exception {
        db.clear();
        populateDatabase();

        db.addSchedule(schedule1);

        db.removeSchedule(schedule1.getName());
        assertEquals(db.getSchedules().size(), 0);
    }

    @Test
    public void getSchedules_activateSchedule() throws Exception {
        db.clear();
        populateDatabase();

        db.addSchedule(schedule2);
        assertEquals(db.getSchedules().get(0).isActive(), false);

        db.activateSchedule(schedule2.getName());
        assertEquals(db.getSchedules().get(0).isActive(), true);
    }

    @Test
    public void getSchedules_deactivateSchedule() throws Exception {
        db.clear();
        populateDatabase();

        schedule2.setActive(true);
        db.addSchedule(schedule2);
        assertEquals(db.getSchedules().get(0).isActive(), true);

        db.deactivateSchedule(schedule2.getName());
        assertEquals(db.getSchedules().get(0).isActive(), false);
    }

    @Test
    public void getSchedules_updateScheduleName() throws Exception {
        db.clear();
        populateDatabase();

        db.addSchedule(schedule1);
        assertEquals(db.getSchedules().get(0).getName(), schedule1.getName());

        String oldScheduleName = schedule1.getName();
        schedule1.setName(oldScheduleName + " updated");

        db.updateScheduleName(oldScheduleName, schedule1.getName());
        assertEquals(db.getSchedules().get(0).getName(), schedule1.getName());
    }

    @Test
    public void getSchedules_addProfileInSchedule() {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);
        db.addSchedule(schedule1);
        db.addProfileInSchedule(pis1, schedule1.getName());
        try {
            assertEquals(db.getSchedules().get(0).getCalendar().size(), 1);
            assertEquals(db.getSchedules().get(0).getCalendar().get(0).getProfile().getName(), profile1.getName());
        } catch (ParseException e) {
            fail();
        }

    }

    @Test
    public void getSchedules_removeProfileInSchedule() {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);
        db.addSchedule(schedule1);
        db.addProfileInSchedule(pis1, schedule1.getName());
        db.removeProfileFromSchedule(pis1, schedule1.getName(), pis1.repeatsOn().get(0));
        try {
            assertEquals(db.getSchedules().get(0).getCalendar().size(), 0);
        } catch (ParseException e) {
            fail();
        }
    }

    @Test
    public void hasSchedule() throws Exception {
        db.clear();
        populateDatabase();

        db.addSchedule(schedule1);

        assertEquals(db.hasSchedule(schedule1.getName()), true);
    }

    @Test
    public void getNotificationCount_addBlockedNotification() throws Exception {
        db.clear();
        populateDatabase();

        assertEquals(db.getNotificationsCountForApp("Facebook").intValue(), 0);

        db.addBlockedNotification("Facebook");
        assertEquals(db.getNotificationsCountForApp("Facebook").intValue(), 1);

        //Note: call to getNotificationsCountForApp sets the count to zero
        assertEquals(db.getNotificationsCountForApp("Facebook").intValue(), 0);

        db.addBlockedNotification("Whatsapp");
        db.addBlockedNotification("Whatsapp");
        assertEquals(db.getNotificationsCountForApp("Whatsapp").intValue(), 2);
    }

    @Test
    public void getProfiles_removeProfileFromAllSchedules() throws Exception {
        db.clear();
        populateDatabase();

        db.createProfile(profile1);
        db.addSchedule(schedule1);
        db.addProfileInSchedule(pis1, schedule1.getName());

        ProfileInSchedule pis2 = new ProfileInSchedule(profile1, db.getDate("1970-01-01T21:31:00Z"), db.getDate("1970-01-01T22:31:00Z"), re2);
        db.addProfileInSchedule(pis2, schedule1.getName());
        assertEquals(db.getSchedules().get(0).getCalendar().size(), 2);

        db.removeProfile(profile1.getName());
        assertEquals(db.getSchedules().get(0).getCalendar().size(), 0);
    }
}
