package dreamteam.focus.client;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dreamteam.focus.R;
import dreamteam.focus.client.Profiles.ProfilesActivity;
import dreamteam.focus.client.Schedules.SchedulesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by bowie on 10/23/17.
 * Tests MainActivity.java
 */

@RunWith(AndroidJUnit4.class)
@SuppressWarnings("unused")
@MediumTest // @link https://testing.googleblog.com/2010/12/test-sizes.html
public class MainActivityTest {
    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    /**
     * A JUnit {@link Rule @Rule} to launch your activity under test.
     * <p>
     * Rules are interceptors which are executed for each test method and will run before
     * any of your setup code in the {@link Before @Before} method.
     * <p>
     * {@link ActivityTestRule} will create and launch of the activity for you and also expose
     * the activity under test. To get a reference to the activity you can use
     * the {@link ActivityTestRule#getActivity()} method.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void profileButton() throws Exception {
        onView(withId(R.id.buttonProfiles)).perform(click());
        intended(hasComponent(ProfilesActivity.class.getName()));
        Espresso.pressBack();
    }

    @Test
    public void scheduleButton() throws Exception {
        onView(withId(R.id.buttonSchedules)).perform(click());
        intended(hasComponent(SchedulesActivity.class.getName()));
        Espresso.pressBack();
    }
}