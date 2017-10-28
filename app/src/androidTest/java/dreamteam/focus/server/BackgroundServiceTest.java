package dreamteam.focus.server;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TimePicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;

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
    private static final String NOTIFICATION_TITLE = "User Alert";
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

        db.createProfile(profile1);
        db.createProfile(profile2);
        db.createProfile(profile3);


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

    @Test
    public void instantProfileActivate() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonProfiles), withText("Profiles"), isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleProfileStatus), withText("OFF"),
                        withParent(childAtPosition(
                                withId(R.id.listViewProfiles),
                                0)),
                        isDisplayed()));
        toggleButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction time=onView(withClassName(Matchers.equalTo(TimePicker.class.getName())));
        time.perform(setTime());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.buttonSetTime), withText("Set Time"), isDisplayed()));
        appCompatButton4.perform(click());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //testing notification

        String NOTIFICATION_TEXT = "Profile : profile1 is now active";

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), 7000);
        UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
        UiObject2 text = device.findObject(By.text(NOTIFICATION_TEXT));
        assertEquals(NOTIFICATION_TITLE, title.getText());
        assertEquals(NOTIFICATION_TEXT, text.getText());


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

    public static ViewAction setTime() {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp =  (TimePicker) view;
                int hour=tp.getHour();
                int min=tp.getMinute();
                tp.setHour(hour);
                tp.setMinute(min+11);
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