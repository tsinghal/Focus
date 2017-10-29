package dreamteam.focus.server;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.TimePicker;

import junit.framework.Assert;

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
import dreamteam.focus.client.Profiles.ProfilesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by bowie on 10/21/17.
 * Complete documentation in testing doc.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class BackgroundServiceTest {
    private static final String WHATSAPP = "com.whatsapp";
    private static final String FOCUS = "dreamteam.focus";
    private static final String FACEBOOK_MESSENGER = "com.facebook.orca";
    private static final String BAD_SCHEDULE_NAME = "dfsjdofiewoiheegeg";
    private static final String GOOD_SCHEDULE_NAME = "test123";
    private static final String ANONYMOUS_SCHEDULE = "AnonymousSchedule";
    private static final String NOTIFICATION_TITLE = "User Alert";
    private static final long DELAY_MILLIS = 250;

    private Profile profile1, profile2, profile3;
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
        appBlacklist1.add("com.whatsapp");
        appBlacklist1.add("com.facebook.katana");
        ArrayList<String> appBlacklist2 = new ArrayList<>();
        appBlacklist2.add("com.facebook.orca");
        ArrayList<String> appBlacklist3 = new ArrayList<>();
        appBlacklist3.add("com.facebook.orca");
        appBlacklist3.add("com.whatsapp");

        ArrayList<Repeat_Enum> enum1 = new ArrayList<>();
        enum1.add(Repeat_Enum.MONDAY);
        enum1.add(Repeat_Enum.WEDNESDAY);
        enum1.add(Repeat_Enum.FRIDAY);
        enum1.add(Repeat_Enum.SUNDAY);
        ArrayList<Repeat_Enum> enum2 = new ArrayList<>();
        enum2.add(Repeat_Enum.TUESDAY);
        enum2.add(Repeat_Enum.WEDNESDAY);
        enum2.add(Repeat_Enum.THURSDAY);
        ArrayList<Repeat_Enum> enum3 = new ArrayList<>();
        enum3.add(Repeat_Enum.MONDAY);
        enum3.add(Repeat_Enum.SATURDAY);
        enum3.add(Repeat_Enum.SUNDAY);

        profile1 = new Profile("profile1", appBlacklist1);
        profile2 = new Profile("profile2", appBlacklist2);
        profile3 = new Profile("profile3", appBlacklist3);

        db.createProfile(profile1);
        db.createProfile(profile2);
        db.createProfile(profile3);


        ProfileInSchedule pis1 = new ProfileInSchedule(profile1,
                new Date(Constants.TIME_1000), new Date(Constants.TIME_1200), enum1);
        ProfileInSchedule pis2 = new ProfileInSchedule(profile2,
                new Date(Constants.TIME_0000), new Date(Constants.TIME_0400), enum2);
        ProfileInSchedule pis3 = new ProfileInSchedule(profile3,
                new Date(Constants.TIME_0800), new Date(Constants.TIME_1200), enum3);

        ArrayList<ProfileInSchedule> cal1 = new ArrayList<>();
        cal1.add(pis1);
        ArrayList<ProfileInSchedule> cal2 = new ArrayList<>();
        cal2.add(pis1);
        cal2.add(pis2);
        ArrayList<ProfileInSchedule> cal3 = new ArrayList<>();
        cal3.add(pis1);
        cal3.add(pis2);
        cal3.add(pis3);

        Schedule schedule1 = new Schedule("schedule1", cal1);
        Schedule schedule2 = new Schedule("schedule2", cal2);
        Schedule schedule3 = new Schedule("schedule3", cal3);
        Schedule goodSchedule = new Schedule(GOOD_SCHEDULE_NAME, cal3);


        db.addSchedule(schedule1);
        db.addSchedule(schedule2);
        db.addSchedule(schedule3);
        db.addSchedule(goodSchedule);

        //Intents.init();
    }

    @Test
    public void instantProfileActivate() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonProfiles), withText("Profiles"), isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(DELAY_MILLIS);
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
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction time = onView(withClassName(Matchers.equalTo(TimePicker.class.getName())));
        time.perform(setTime());

        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.buttonSetTime), withText("Set Time"), isDisplayed()));
        appCompatButton4.perform(click());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //testing notification using UIAutomator

        String NOTIFICATION_TEXT = "Profile : profile1 is now active";

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), 7000);
        UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
        UiObject2 text = device.findObject(By.text(NOTIFICATION_TEXT));
        assertEquals(NOTIFICATION_TITLE, title.getText());
        assertEquals(NOTIFICATION_TEXT, text.getText());
        device.pressHome();
    }

    @Test
    public void instantProfileDeactivate() {

        //activate a profile, so as to check behavior when user deactivates is
        //db.activateProfile(pis2);

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonProfiles), withText("Profiles"), isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleProfileStatus), withText("OFF"),
                        withParent(childAtPosition(
                                withId(R.id.listViewProfiles),
                                1)),
                        isDisplayed()));
        toggleButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction time = onView(withClassName(Matchers.equalTo(TimePicker.class.getName())));
        time.perform(setTime());


        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.buttonSetTime), withText("Set Time"), isDisplayed()));
        appCompatButton4.perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        toggleButton = onView(
                allOf(withId(R.id.toggleProfileStatus), withText("ON"),
                        withParent(childAtPosition(
                                withId(R.id.listViewProfiles),
                                1)),
                        isDisplayed()));
        toggleButton.perform(click());


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //testing notification

        String NOTIFICATION_TEXT = "Your profile is now inactive.";

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), 10000);
        UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
        UiObject2 text = device.findObject(By.text(NOTIFICATION_TEXT));
        assertEquals(NOTIFICATION_TITLE, title.getText());
        assertEquals(NOTIFICATION_TEXT, text.getText());
        device.pressHome();
    }

    @Test
    public void instantProfileScheduledDeactivate() {

        //activate a profile, so as to check behavior when user deactivates is
        //db.activateProfile(pis2);

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonProfiles), withText("Profiles"), isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleProfileStatus), withText("OFF"),
                        withParent(childAtPosition(
                                withId(R.id.listViewProfiles),
                                1)),
                        isDisplayed()));
        toggleButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction time = onView(withClassName(Matchers.equalTo(TimePicker.class.getName())));
        time.perform(setTime());


        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.buttonSetTime), withText("Set Time"), isDisplayed()));
        appCompatButton4.perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        toggleButton = onView(
                allOf(withId(R.id.toggleProfileStatus), withText("ON"),
                        withParent(childAtPosition(
                                withId(R.id.listViewProfiles),
                                1)),
                        isDisplayed()));
        toggleButton.perform(click());


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //testing notification

        String NOTIFICATION_TEXT = "Your profile is now inactive.";

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), 10000);
        UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
        UiObject2 text = device.findObject(By.text(NOTIFICATION_TEXT));
        assertEquals(NOTIFICATION_TITLE, title.getText());
        assertEquals(NOTIFICATION_TEXT, text.getText());
        device.pressHome();
    }

    @Test
    public void activeProfileDeleted() {

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.buttonProfiles), withText("Profiles"), isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(DELAY_MILLIS);
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
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction time = onView(withClassName(Matchers.equalTo(TimePicker.class.getName())));
        time.perform(setTime());


        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.buttonSetTime), withText("Set Time"), isDisplayed()));
        appCompatButton4.perform(click());

        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewProfileName), withText("profile1"),
                        withParent(childAtPosition(
                                withId(R.id.listViewProfiles),
                                0)),
                        isDisplayed()));
        textView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        appCompatButton4 = onView(
                allOf(withId(R.id.buttonDiscardChanges), withText("Delete Profile"), isDisplayed()));
        appCompatButton4.perform(click());


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //testing notification

        String NOTIFICATION_TEXT = "Your profile is now inactive.";

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), 10000);
        UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
        UiObject2 text = device.findObject(By.text(NOTIFICATION_TEXT));
        assertEquals(NOTIFICATION_TITLE, title.getText());
        assertEquals(NOTIFICATION_TEXT, text.getText());
        device.pressHome();
    }

    @Test
    public void openBlockedApp() {

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
                                1)),
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkAppOpen(FACEBOOK_MESSENGER);       //messenger shouldn't open since it is blocked

    }

    @Test
    public void intersectingProfiles(){
        try {
            activateInstantProfile(profile1,10,1);
            activateInstantProfile(profile3, 10, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        checkAppOpen(FACEBOOK_MESSENGER);       //messenger shouldn't open since it is blocked by two active profiles

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //reopen our app

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context context = InstrumentationRegistry.getInstrumentation().getContext(); //gets the context based on the instrumentation
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(FOCUS);  //sets the intent to start your app
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);  //clear out any previous task, i.e., make sure it starts on the initial screen
        context.startActivity(intent);  //starts the app
        device.wait(Until.hasObject(By.pkg(FOCUS)), 3000);


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         appCompatButton = onView(
                allOf(withId(R.id.buttonProfiles), withText("Profiles"), isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleProfileStatus), withText("ON"),
                        withParent(childAtPosition(
                                withId(R.id.listViewProfiles),
                                0)),
                        isDisplayed()));
        toggleButton.perform(click());


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkAppOpen(FACEBOOK_MESSENGER);       //messenger shouldn't open since it is blocked  by profile3

    }

    //helper function
    void checkAppOpen(String packageName){
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        Context context = InstrumentationRegistry.getInstrumentation().getContext(); //gets the context based on the instrumentation
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);  //sets the intent to start your app
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);  //clear out any previous task, i.e., make sure it starts on the initial screen
        context.startActivity(intent);  //starts the app
        device.wait(Until.hasObject(By.pkg(packageName)), 3000);

        //checks if current display has messenger
        UiObject title = device.findObject(new UiSelector().packageName(packageName));
        assertFalse(title.exists());
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

    private static ViewAction setTime() {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp =  (TimePicker) view;
                int hour=tp.getHour();
                int min=tp.getMinute();
                if(min + 11 > 59){
                    tp.setHour(hour + 1);
                    tp.setMinute(min);}
                else
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

    /**
     * Activates instant profile for a specified time frame and a certain time until deactivation.
     *
     * @param profile   profile to be activated for length minutes.
     * @param length    length of instant profile activation, in minutes
     * @param timeUntil time until profile deactivation, in minutes
     */
    private void activateInstantProfile(Profile profile, int length, int timeUntil) throws Exception {
        if (length < timeUntil) throw new Exception("length < timeUntil");
        db.activateProfile(new ProfileInSchedule(profile,
                new Date(new Date().getTime() - (length - timeUntil) * 60 * 1000),
                new Date(new Date().getTime() + timeUntil * 60 * 1000)));
    }

    @After
    public void tearDown() throws Exception {
        db.clear();
        //Intents.release();
    }
}