package dreamteam.focus.client;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TimePicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;
import dreamteam.focus.server.DatabaseConnector;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditProfileInSchedulUpdatesPIS {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        DatabaseConnector db = new DatabaseConnector(InstrumentationRegistry.getTargetContext());
        db.clear();
        ArrayList<String> BlockedApps=new ArrayList<>();
        Profile p=new Profile("m",BlockedApps);
        db.createProfile(p);

        Date startT = new Date() ;
        Date endT = new Date();

        SimpleDateFormat d1 = new SimpleDateFormat("HH:mm");
        try {
            startT = d1.parse(10+":"+41);
            endT = d1.parse(10+":"+57);
            Log.e("TImeActivationPIS","ADDED PROFILE TO START TIME: "+ startT.toString() + " END TIME: " + endT.toString());
        } catch (ParseException qp) {
            qp.printStackTrace();

        }

        ArrayList<Repeat_Enum> rep =new ArrayList<Repeat_Enum>();
        rep.add(Repeat_Enum.MONDAY);
        ProfileInSchedule pis1=new ProfileInSchedule(p,startT,endT,rep);


        ArrayList<Repeat_Enum> rep2 =new ArrayList<Repeat_Enum>();
        rep2.add(Repeat_Enum.WEDNESDAY);
        ProfileInSchedule pis2=new ProfileInSchedule(p,startT,endT,rep2);

        ArrayList<ProfileInSchedule> pisArray=new ArrayList<ProfileInSchedule>();
        pisArray.add(pis1);
        pisArray.add(pis2);
        Schedule s=new Schedule("s",pisArray);
        db.addSchedule(s);

    }

    @Test
    public void editProfileInSchedulUpdatesPIS() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonSchedules), withText("Schedules"), isDisplayed()));
        appCompatButton.perform(click());


             try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewScheduleName), withText("s"),
                        withParent(childAtPosition(
                                withId(R.id.ScheduleNames),
                                0)),
                        isDisplayed()));
        textView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textViewProfileScheduleName), withText("m"),
                        withParent(childAtPosition(
                                withId(R.id.listViewMonday),
                                0)),
                        isDisplayed()));
        textView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatSpinner = onView(
                withId(R.id.spinnerPisDay));
        appCompatSpinner.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.text1), withText("TUESDAY"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.FrameLayout")))),
                                1),
                        isDisplayed()));
        appCompatTextView.perform(click());


        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction view1=onView(allOf(withId(R.id.timePickerPisStartTime)));
        view1.perform(setStartTime());

        ViewInteraction view2=onView(allOf(withId(R.id.timePickerPisEndTime)));
        view2.perform(setEndTime());


        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.buttonPisSave), withText("Update Schedule")));
        appCompatButton11.perform(scrollTo(), click());

        ViewInteraction view3 = onView(
                allOf(withId(R.id.textViewProfileScheduleName), withText("m"),
                        withParent(childAtPosition(
                                withId(R.id.listViewTuesday),
                                0)),
                        isDisplayed()));
        view3.perform(click());


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction view4 = onView(
                allOf(withId(android.R.id.text1), withText("TUESDAY"),
                        childAtPosition(
                                allOf(withId(R.id.spinnerPisDay),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                4)),
                                0),
                        isDisplayed()));
        view4.check(matches(withText("TUESDAY")));

        ViewInteraction view5=onView(allOf(withId(R.id.timePickerPisStartTime)));
        view5.check(matches(matchesTime(10,39)));

        ViewInteraction view6=onView(allOf(withId(R.id.timePickerPisEndTime)));
        view6.check(matches(matchesTime(10,55)));



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

    public static ViewAction setStartTime() {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp =  (TimePicker) view;
                tp.setHour(10);
                tp.setMinute(39);

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
    public static ViewAction setEndTime() {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp =  (TimePicker) view;
                tp.setHour(10);
                tp.setMinute(55);

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

    public static Matcher<View> matchesTime(final int hour, final int min) {
        return new BoundedMatcher<View, TimePicker>(TimePicker.class) {


            @Override
            public void describeTo(Description description) {
                description.appendText("matches Time:");
            }

            @Override
            protected boolean matchesSafely(TimePicker item) {
                return (hour == item.getHour() && min == item.getMinute() );
            }
        };
    }
}
