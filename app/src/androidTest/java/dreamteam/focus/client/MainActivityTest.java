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
 * NOTICE: The app must be granted usage access and notification access prior to running.
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

    /**
     * src = https://stackoverflow.com/questions/39376856
     *
     * @throws Exception if assertion fails
     */
//    @Test
//    public void usageAccessDialogTrue() throws Exception {
//        onView(withText(R.string.usage_access))
//                .inRoot(isDialog())
//                .check(matches(isDisplayed()));
//        onView(withText(R.string.yes)).perform(click());
//        intended(allOf(
//                hasAction(Settings.ACTION_USAGE_ACCESS_SETTINGS),
//                toPackage("com.android.settings")));
//    }
//
//    @Test
//    public void usageAccessDialogFalse() throws Exception {
//        onView(withText(R.string.usage_access))
//                .inRoot(isDialog())
//                .check(matches(isDisplayed()));
//        onView(withText(R.string.no)).perform(click());
//    }
//
//    @Test
//    public void notificationAccessDialogTrue() throws Exception {
//        onView(withText(R.string.notification_service))
//                .inRoot(isDialog())
//                .check(matches(isDisplayed()));
//        onView(withText(R.string.yes)).perform(click());
//        intended(allOf(
//                hasAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
//                toPackage("com.android.settings")));
//    }
//
//    @Test
//    public void notificationAccessDialogFalse() throws Exception {
//        onView(withText(R.string.notification_service))
//                .inRoot(isDialog())
//                .check(matches(isDisplayed()));
//        onView(withText(R.string.no)).perform(click());
//    }

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