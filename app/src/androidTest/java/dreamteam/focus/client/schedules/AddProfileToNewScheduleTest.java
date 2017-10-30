package dreamteam.focus.client.schedules;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TimePicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.client.MainActivity;
import dreamteam.focus.server.DatabaseConnector;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by aarav on 10/27/17.
 */

@RunWith(AndroidJUnit4.class)
public class AddProfileToNewScheduleTest {
    private DatabaseConnector db;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception{
        db = new DatabaseConnector(InstrumentationRegistry.getTargetContext());
        db.clear();

        ArrayList<String> s = new ArrayList<>();
        s.add("Email");
        Profile p = new Profile("Driving", s);
        Profile q = new Profile("Class", s);
        Profile r = new Profile("zzzz", s);

        db.createProfile(p);
        db.createProfile(q);
        db.createProfile(r);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.buttonSchedules), withText("Schedules"), isDisplayed()));
        appCompatButton4.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.buttonAddSchedule), withText("+"), isDisplayed()));
        appCompatButton5.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.textEditScheduleName3));
        appCompatEditText2.perform(scrollTo(), replaceText("Test"), ViewActions.closeSoftKeyboard());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.buttonAddProfileToSchedule3), withText("+")));
        appCompatButton6.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ViewAction setTime(final int hour, final int min) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp =  (TimePicker) view;
                tp.setHour(hour);
                tp.setMinute(min);
            }
            @Override
            public String getDescription() {
                return "Set the passed time into the TimePicker";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }
        };
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
    public void testIfDayOfTheWeekSelected(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Starttime=onView(withId(R.id.timePickerStartTime));
        Starttime.perform(scrollTo(), setTime(13, 00));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Endtime=onView(withId(R.id.timePickerEndTime));
        Endtime.perform(scrollTo(), setTime(13, 20));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkBoxProfile2),
                        withParent(childAtPosition(
                                withId(R.id.ListViewProfilesToAdd),
                                0))));
        checkBox.perform(scrollTo(), click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonAddProfile), withText("Add Profile")));
        appCompatButton.perform(scrollTo(), click());

        Espresso.onView(withText("Select At-least One Day")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.buttonDiscardProfile), withText("Discard")));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonDiscardSchedule3), withText("Discard Changes")));
        appCompatButton7.perform(scrollTo(), click());
    }

    @Test
    public void testIfEndTimeisBeforeStartTime(){
        ViewInteraction switch_ = onView(
                allOf(withId(R.id.switch1), withText("Monday")));
        switch_.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Starttime=onView(withId(R.id.timePickerStartTime));
        Starttime.perform(scrollTo(), setTime(15, 00));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Endtime=onView(withId(R.id.timePickerEndTime));
        Endtime.perform(scrollTo(), setTime(13, 00));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkBoxProfile2),
                        withParent(childAtPosition(
                                withId(R.id.ListViewProfilesToAdd),
                                0))));
        checkBox.perform(scrollTo(), click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonAddProfile), withText("Add Profile")));
        appCompatButton.perform(scrollTo(), click());

        Espresso.onView(withText("Exceeding the ten hour limit")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.buttonDiscardProfile), withText("Discard")));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonDiscardSchedule3), withText("Discard Changes")));
        appCompatButton7.perform(scrollTo(), click());

    }

    @Test
    public void testIfEndTimeIsLessThan10Minutes(){
        ViewInteraction switch_ = onView(
                allOf(withId(R.id.switch2), withText("Tuesday")));
        switch_.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Starttime=onView(withId(R.id.timePickerStartTime));
        Starttime.perform(scrollTo(), setTime(13, 00));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Endtime=onView(withId(R.id.timePickerEndTime));
        Endtime.perform(scrollTo(), setTime(13, 9));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkBoxProfile2),
                        withParent(childAtPosition(
                                withId(R.id.ListViewProfilesToAdd),
                                0))));
        checkBox.perform(scrollTo(), click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonAddProfile), withText("Add Profile")));
        appCompatButton.perform(scrollTo(), click());

        Espresso.onView(withText("Exceeding the 10 minute-10 hour limit")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.buttonDiscardProfile), withText("Discard")));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonDiscardSchedule3), withText("Discard Changes")));
        appCompatButton7.perform(scrollTo(), click());

    }

    @Test
    public void testIfEndTimeIsMoreThan10Hours(){
        ViewInteraction switch_ = onView(
                allOf(withId(R.id.switch3), withText("Wednesday")));
        switch_.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Starttime=onView(withId(R.id.timePickerStartTime));
        Starttime.perform(scrollTo(), setTime(12, 15));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Endtime=onView(withId(R.id.timePickerEndTime));
        Endtime.perform(scrollTo(), setTime(23, 49));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkBoxProfile2),
                        withParent(childAtPosition(
                                withId(R.id.ListViewProfilesToAdd),
                                0))));
        checkBox.perform(scrollTo(), click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonAddProfile), withText("Add Profile")));
        appCompatButton.perform(scrollTo(), click());

        Espresso.onView(withText("Exceeding the 10 minute-10 hour limit")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.buttonDiscardProfile), withText("Discard")));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonDiscardSchedule3), withText("Discard Changes")));
        appCompatButton7.perform(scrollTo(), click());

    }

    @Test
    public void testOneProfileItemSelected(){
        ViewInteraction switch_ = onView(
                allOf(withId(R.id.switch3), withText("Wednesday")));
        switch_.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Starttime=onView(withId(R.id.timePickerStartTime));
        Starttime.perform(scrollTo(), setTime(12, 15));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Endtime=onView(withId(R.id.timePickerEndTime));
        Endtime.perform(scrollTo(), setTime(14, 25));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkBoxProfile2),
                        withParent(childAtPosition(
                                withId(R.id.ListViewProfilesToAdd),
                                0))));
        checkBox.perform(scrollTo());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonAddProfile), withText("Add Profile")));
        appCompatButton.perform(scrollTo(), click());

        Espresso.onView(withText("Select At-least One Profile")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.buttonDiscardProfile), withText("Discard")));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonDiscardSchedule3), withText("Discard Changes")));
        appCompatButton7.perform(scrollTo(), click());

    }

    @Test
    public void testProfileItemCreatedInSchedule() {

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.switch2), withText("Tuesday")));
        switch_.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Starttime = onView(withId(R.id.timePickerStartTime));
        Starttime.perform(scrollTo(), setTime(12, 15));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Endtime = onView(withId(R.id.timePickerEndTime));
        Endtime.perform(scrollTo(), setTime(14, 25));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkBoxProfile2),
                        withParent(childAtPosition(
                                withId(R.id.ListViewProfilesToAdd),
                                2))));
        checkBox.perform(scrollTo(), click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonAddProfile), withText("Add Profile")));
        appCompatButton.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewProfileScheduleName), withText("zzzz"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.listViewTuesday3),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("zzzz")));

        ViewInteraction button = onView(
                allOf(withId(R.id.toggleScheduleProfileStatus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.listViewTuesday3),
                                        0),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonDiscardSchedule3), withText("Discard Changes")));
        appCompatButton7.perform(scrollTo(), click());

    }

    @Test
    public void testProfileItemNotCreatedInSchedule() {

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.switch1), withText("Monday")));
        switch_.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Starttime = onView(withId(R.id.timePickerStartTime));
        Starttime.perform(scrollTo(), setTime(12, 15));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction Endtime = onView(withId(R.id.timePickerEndTime));
        Endtime.perform(scrollTo(), setTime(14, 25));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkBoxProfile2),
                        withParent(childAtPosition(
                                withId(R.id.ListViewProfilesToAdd),
                                0))));
        checkBox.perform(scrollTo(), click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.buttonDiscardProfile), withText("Discard")));
        appCompatButton6.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(
                allOf(withId(R.id.textViewProfileScheduleName), withText("Driving"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.listViewMonday3),
                                        0),
                                1),
                        isDisplayed())).check(doesNotExist());

        onView(
                allOf(withId(R.id.toggleScheduleProfileStatus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.listViewMonday3),
                                        0),
                                2),
                        isDisplayed())).check(doesNotExist());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonDiscardSchedule3), withText("Discard Changes")));
        appCompatButton7.perform(scrollTo(), click());
    }
}
