package dreamteam.focus.client.Schedules;

import android.content.ClipData;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
//import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import dreamteam.focus.R;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.MainActivity;
import dreamteam.focus.server.DatabaseConnector;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Predicates.instanceOf;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by aarav on 10/26/17.
 */
@RunWith(AndroidJUnit4.class)
public class SchedulesActivityTest {

    private DatabaseConnector db;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        Intents.init();
        db = new DatabaseConnector(InstrumentationRegistry.getTargetContext());
        //db.clear();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonSchedules), withText("Schedules"), isDisplayed()));
        appCompatButton.perform(click());
    }

    private void populateDB (){

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.buttonAddSchedule), withText("+"), isDisplayed()));
        appCompatButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                withId(R.id.textEditScheduleName3));
        appCompatEditText.perform(scrollTo(), replaceText("Midterm"), closeSoftKeyboard());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.buttonAddSchedule3), withText("Create Schedule")));
        appCompatButton3.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.buttonAddSchedule), withText("+"), isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.textEditScheduleName3));
        appCompatEditText2.perform(scrollTo(), replaceText("School"), closeSoftKeyboard());


        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.buttonAddSchedule3), withText("Create Schedule")));
        appCompatButton5.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }



    @Test
    public void testCreateScheduleButton() throws Exception {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.buttonAddSchedule), withText("+"), isDisplayed()));
        appCompatButton2.perform(click());

        intended(hasComponent(AddScheduleActivity.class.getName()));
        pressBack();

    }

    @Test
    public void testListViewItems() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        this.populateDB();

        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewScheduleName), withText("Midterm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ScheduleNames),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Midterm")));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textViewScheduleName), withText("School"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ScheduleNames),
                                        1),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("School")));

        db.clear();
    }

    @Test
    public void testAllToggleButtonOFF() {

        this.populateDB();

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleScheduleStatus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ScheduleNames),
                                        0),
                                1),
                        isDisplayed()));
        toggleButton.check(matches(isNotChecked()));

        ViewInteraction toggleButton2 = onView(
                allOf(withId(R.id.toggleScheduleStatus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ScheduleNames),
                                        1),
                                1),
                        isDisplayed()));
        toggleButton2.check(matches(isNotChecked()));

        db.clear();
    }

    @Test
    public void testAllToggleButtonON() {

        this.populateDB();

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleScheduleStatus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ScheduleNames),
                                        0),
                                1),
                        isDisplayed()));
        toggleButton.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        toggleButton.check(matches(isChecked()));

        ViewInteraction toggleButton2 = onView(
                allOf(withId(R.id.toggleScheduleStatus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ScheduleNames),
                                        1),
                                1),
                        isDisplayed()));
        toggleButton2.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        toggleButton2.check(matches(isChecked()));

        db.clear();
    }

    @Test
    public void testOneToggleButtonOFF() {

        this.populateDB();

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleScheduleStatus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ScheduleNames),
                                        0),
                                1),
                        isDisplayed()));
        //It is off by Default
        toggleButton.check(matches(isNotChecked()));
        //Toggle it to switch it ON
        toggleButton.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check if it is on
        toggleButton.check(matches(isChecked()));
        //Now switch it off again for the test
        toggleButton.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        toggleButton.check(matches(isNotChecked()));

        db.clear();
    }

    @Test
    public void testOneToggleButtonON() {

        this.populateDB();

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleScheduleStatus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ScheduleNames),
                                        0),
                                1),
                        isDisplayed()));
        //It is off by Default
        toggleButton.check(matches(isNotChecked()));
        //toggle to switch it on
        toggleButton.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check if it is turned on indeed
        toggleButton.check(matches(isChecked()));

        db.clear();
    }

    @Test
    public void testListViewItemIsClickable() {

        this.populateDB();

        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewScheduleName), withText("Midterm"),
                        withParent(childAtPosition(
                                withId(R.id.ScheduleNames),
                                0)),
                        isDisplayed()));
        textView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textViewCreate), withText("Edit Schedule"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction editText = onView(
                allOf(withId(R.id.textEditScheduleName), withText("Midterm"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                2),
                        isDisplayed()));
        editText.check(matches(withText("Midterm")));

        pressBack();

        db.clear();
    }


    @Test
    public void testAndoridBackButton() {
        //Getting the view and pressing the Android back button
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.pressBack();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intended(hasComponent(MainActivity.class.getName()));
    }



    @After
    public void tearDown() throws Exception {
        Intents.release();
    }



}