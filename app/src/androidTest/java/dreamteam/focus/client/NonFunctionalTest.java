package dreamteam.focus.client;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.Profiles.CreateProfileActivity;
import dreamteam.focus.client.Profiles.ProfilesActivity;
import dreamteam.focus.client.Schedules.AddScheduleActivity;
import dreamteam.focus.client.Schedules.SchedulesActivity;
import dreamteam.focus.server.DatabaseConnector;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by bowie on 10/23/17.
 * Tests MainActivity.java
 * NOTICE: The app must be granted usage access and notification access prior to running.
 */

@RunWith(AndroidJUnit4.class)
@SuppressWarnings("unused")
@MediumTest // @link https://testing.googleblog.com/2010/12/test-sizes.html
public class NonFunctionalTest {

    private Profile profile1;
    private DatabaseConnector db;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        db = new DatabaseConnector(InstrumentationRegistry.getTargetContext());
        db.clear();

        ArrayList<String> appBlacklist1 = new ArrayList<>();
        appBlacklist1.add("com.facebook.orca");

        ArrayList<Repeat_Enum> enum1 = new ArrayList<>();
        enum1.add(Repeat_Enum.MONDAY);

        profile1 = new Profile("profile0", appBlacklist1);

        db.createProfile(profile1);

        ProfileInSchedule pis1 = new ProfileInSchedule(profile1,
                new Date(946713600000L), new Date(946720800000L), enum1);

        ArrayList<ProfileInSchedule> cal1 = new ArrayList<>();
        cal1.add(pis1);


        for(int i=0; i<20;i++){
            Schedule schedule = new Schedule("schedule"+i,cal1);
            db.addSchedule(schedule);
        }

        for(int i=1; i<=20; i++){
            Profile profile = new Profile("profile"+i, appBlacklist1);
            db.createProfile(profile);
        }
    }


    @Test
    public void profilesCountCheck() throws Exception {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intents.init();
        onView(withId(R.id.buttonProfiles)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction appCompatButton2 = onView(allOf(withId(R.id.buttonAddProfile), withText("+"), isDisplayed()));
        appCompatButton2.perform(click());


        intended(not(hasComponent(CreateProfileActivity.class.getName())));
        Intents.release();

    }

    @Test
    public void schedulesCountButton() throws Exception {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intents.init();
        onView(withId(R.id.buttonSchedules)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton2 = onView(allOf(withId(R.id.buttonAddSchedule), withText("+"), isDisplayed()));
        appCompatButton2.perform(click());


        intended(not(hasComponent(AddScheduleActivity.class.getName())));
        Intents.release();

    }
}