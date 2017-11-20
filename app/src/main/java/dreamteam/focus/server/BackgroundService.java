package dreamteam.focus.server;

/*
  ____             _                                   _  _____                 _
 |  _ \           | |                                 | |/ ____|               (_)
 | |_) | __ _  ___| | ____ _ _ __ ___  _   _ _ __   __| | (___   ___ _ ____   ___  ___ ___
 |  _ < / _` |/ __| |/ / _` | '__/ _ \| | | | '_ \ / _` |\___ \ / _ \ '__\ \ / / |/ __/ _ \
 | |_) | (_| | (__|   < (_| | | | (_) | |_| | | | | (_| |____) |  __/ |   \ V /| | (_|  __/
 |____/ \__,_|\___|_|\_\__, |_|  \___/ \__,_|_| |_|\__,_|_____/ \___|_|_   \_/ |_|\___\___|
 | |           |  _ \   __/ |      (_)                      | | |__   __|      | |
 | |__  _   _  | |_) | |___/      ___  ___    __ _ _ __   __| |    | |_   _ ___| |__   __ _ _ __
 | '_ \| | | | |  _ < / _ \ \ /\ / / |/ _ \  / _` | '_ \ / _` |    | | | | / __| '_ \ / _` | '__|
 | |_) | |_| | | |_) | (_) \ V  V /| |  __/ | (_| | | | | (_| |    | | |_| \__ \ | | | (_| | |
 |_.__/ \__, | |____/ \___/ \_/\_/ |_|\___|  \__,_|_| |_|\__,_|    |_|\__,_|___/_| |_|\__,_|_|
         __/ |
        |___/
 */

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;

/**
 * src: https://stackoverflow.com/questions/41425986
 */

public class BackgroundService extends NotificationListenerService {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String ACTION_STATUS_BROADCAST =
            "com.example.notifyservice.NotificationService_Status";
    private static final String TAG = "BackgroundService";
    private static final String FOCUS_PACKAGE_NAME =
            "dreamteam.focus";

    /*
     this list is unchanged in program, contains package names of apps that throw multiple
     notifications for a single notification given to user.
    */
    private static final List<String> erraticNotificationApps =
            Arrays.asList("com.whatsapp", "com.google.android.gm");

    private static final int SCHEDULE_TIMEOUT_SEC = 3;
    private static final int BLOCKING_TIMEOUT_SEC = 1;
    private static final double WINDOW_SIZE = 0.5;

    private static final String ANONYMOUS_SCHEDULE = "AnonymousSchedule";

    private static final int NOTIFICATION_ID_GENERIC = 0;
    private static final int NOTIFICATION_ID_SUPPRESS_NOTIFICATION = 1;
    private static final int NOTIFICATION_ID_PROFILE_CHANGE = 2;
    private static final int NOTIFICATION_ID_UNSEEN_NOTIFICATIONS = 3;
    private static final int NOTIFICATION_ID_ANONYMOUS_SCHEDULE_ACTIVE = 11;
    private static final int NOTIFICATION_ID_ANONYMOUS_SCHEDULE_INACTIVE = 12;
    private static final int NOTIFICATION_ID_ANONYMOUS_SCHEDULE_DELETED = 13;

    /**
     * src: https://www.redcort.com/us-federal-bank-holidays/
     */
    private ArrayList<String> publicHolidays;

    private Runnable scheduleThread = null;
    private Runnable blockingThread = null;
    private DatabaseConnector db = null;
    private ArrayList<Schedule> schedules;
    private ArrayList<ProfileInSchedule> anonymousPIS;
    private int anonymousPISOldSize;
    private int databaseVersion = -1;
    private HashSet<String> blockedApps;
    private NLServiceReceiver nlServiceReceiver;

    public BackgroundService() {
        schedules = new ArrayList<>();
        blockedApps = new HashSet<>();
        anonymousPIS = new ArrayList<>();

        publicHolidays = new ArrayList<>(Arrays.asList(
                "2017-01-02", "2017-01-16", "2017-02-20", "2017-05-29", "2017-07-04", "2017-09-04",
                "2017-10-09", "2017-11-10", "2017-11-23", "2017-12-25",
                "2018-01-01", "2018-01-15", "2018-02-19", "2018-05-28", "2018-07-04", "2018-09-03",
                "2018-10-08", "2018-11-12", "2018-11-22", "2018-12-25"
        ));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    /**
     * @link https://stackoverflow.com/questions/28292682
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Handler mHandler = new Handler();
        if (scheduleThread == null) {
            scheduleThread = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(scheduleThread, SCHEDULE_TIMEOUT_SEC * 1000);
//                    Log.d(TAG, "scheduleThread");
                    if (needUpdate()) updateFromServer();
                    tick();
                }
            };
            Log.i(TAG, "scheduleThread created");
            if (publicHolidays.contains(getDateString(new Date()))) {
                Log.v("onStart.scheduleThread", "Thread is now asleep till end of day");
                sendNotification(NOTIFICATION_ID_GENERIC,
                        "Today is a public holiday. This app is disabled.",
                        FOCUS_PACKAGE_NAME);
                mHandler.postDelayed(scheduleThread, millisToEndOfDay());
            } else {
                mHandler.postDelayed(scheduleThread, SCHEDULE_TIMEOUT_SEC * 1000);
            }
        }

        if (blockingThread == null) {
            blockingThread = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(blockingThread, BLOCKING_TIMEOUT_SEC * 1000);
//                    Log.d(TAG, "blockingThread");
                    blockApps();
                    if (needUpdate()) updateFromServer();
                    fastTick(false);
                }
            };
            Log.i(TAG, "blockingThread created");
            if (publicHolidays.contains(getDateString(new Date()))) {
                Log.v("onStart.blockingThread", "Thread is now asleep till end of day");
                sendNotification(NOTIFICATION_ID_GENERIC,
                        "Today is a public holiday. This app is disabled.",
                        FOCUS_PACKAGE_NAME);
                mHandler.postDelayed(blockingThread, millisToEndOfDay());
            } else {
                mHandler.postDelayed(blockingThread, BLOCKING_TIMEOUT_SEC * 1000);
            }
        }
        if (intent != null) {
            if (intent.hasExtra("command")) {
                Log.i("NotificationService", "Started for command '" + intent.getStringExtra("command"));
                broadcastStatus();
            } else if (intent.hasExtra("id")) {
                int id = intent.getIntExtra("id", 0);
                String message = intent.getStringExtra("msg");
                Log.i("NotificationService", "Requested to start explicitly - id : " + id + " message : " + message);
            }
        }
        super.onStartCommand(intent, flags, startId);

        // NOTE: We return STICKY to prevent the automatic service termination
        return START_STICKY;
    }

    /**
     * src= https://stackoverflow.com/questions/11989555
     *
     * @return millis to 12:01am
     */
    private long millisToEndOfDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis() - System.currentTimeMillis() + 60000;
    }

    @Override
    @SuppressWarnings("SpellCheckingInspection")
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.v("onNotificationPosted",
                "ID: " + sbn.getId() +
                        "\n\tText: " + sbn.getNotification().tickerText +
                        "\n\tPackage: " + sbn.getPackageName());


        String packageName = getNameFromSBN(sbn);
        // cancel only that notification of Focus (used to dismiss heads-up notifications from other apps
        if (packageName.equals("dreamteam.focus") && sbn.getId() == NOTIFICATION_ID_SUPPRESS_NOTIFICATION) {
            cancelNotification(sbn.getKey());
            return;
        }

        if (sbn.getTag() == null && erraticNotificationApps.contains(packageName)) {
            cancelNotification(sbn.getKey());
            return;
        }

        // block each app's notifications in `blockedApps`
        for (String app : blockedApps) {
            if (packageName.equals(app)) {
                cancelNotification(sbn.getKey());
                db.addBlockedNotification(app); // tell database to add count to this app
                sendNotification(NOTIFICATION_ID_SUPPRESS_NOTIFICATION, "Notification blocked by Focus!", FOCUS_PACKAGE_NAME);
                db.addToStatsBlockedNotifications(1);
                Log.v(BackgroundService.class.getName(), "addToStatsBlockedNotifications, now = " + db.getStatsBlockedNotifications());
            }
        }

    }

    /**
     * Sends a notification from this application package to the Android System
     *
     * @param id          the id of this notification
     * @param message     the message of this notification
     * @param packageName the package name of the app that should open on clicking the notification
     */
    private void sendNotification(int id, String message, String packageName) {
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PackageManager pm = getApplicationContext().getPackageManager();
        Intent result = pm.getLaunchIntentForPackage(packageName);

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("User Alert")
                .setContentText(message);

        // defines where the user will be directed if notification is clicked
        mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, result, PendingIntent.FLAG_UPDATE_CURRENT));

        // Builds the notification and issues it.
        mNotifyMgr.notify(id, mBuilder.build());
        Log.i("sendNotification", "A notification was thrown!");
    }

    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.i("NotificationService", "Notification listener DISCONNECTED from the notification service! " +
                "\nScheduling a reconnect...");
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i("NotificationService", "Notification listener connected with the notification service!");
    }

    private String getNameFromSBN(StatusBarNotification sbn) {
        return sbn.getPackageName();
    }

    private void broadcastStatus() {
//        Log.i(TAG, "Broadcasting status added(" + nAdded + ")/removed(" + nRemoved + ")");
        Intent i1 = new Intent(ACTION_STATUS_BROADCAST);
//        i1.putExtra("serviceMessage", "Added: " + nAdded + " | Removed: " + nRemoved);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i1);
        //NOT A TO-DO ANYMORE! profileMessage, scheduleMessage
    }

    @Override
    @SuppressWarnings("SpellCheckingInspection")
    public void onCreate() {
        super.onCreate();
        nlServiceReceiver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.notifyservice.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlServiceReceiver, filter);
        Log.i("onCreate", "NotificationService created!");

        if (getApplicationContext() != null) {
            db = new DatabaseConnector(getApplicationContext());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlServiceReceiver);
        Log.i("NotificationService", "NotificationService destroyed.");
    }

    /**
     * Get the value of today in terms of Repeat_Enum
     *
     * @return today, in Repeat_Enum
     */
    @SuppressWarnings("SpellCheckingInspection")
    private Repeat_Enum todayInRepeatEnum() {
        String today = (String) DateFormat.format("EEEE", new Date());
        switch (today) {
            case "Monday":
                return Repeat_Enum.MONDAY;
            case "Tuesday":
                return Repeat_Enum.TUESDAY;
            case "Wednesday":
                return Repeat_Enum.WEDNESDAY;
            case "Thursday":
                return Repeat_Enum.THURSDAY;
            case "Friday":
                return Repeat_Enum.FRIDAY;
            case "Saturday":
                return Repeat_Enum.SATURDAY;
            case "Sunday":
                return Repeat_Enum.SUNDAY;
            default:
                return Repeat_Enum.NEVER;
        }
    }

    /**
     * This function is executed every SCHEDULE_TIMEOUT_SEC seconds.
     */
    private void tick() {
        final String TAG = "tick()";
        int now = getTimeInInt(new Date()); // get system time

        // Make a deep copy of `blockedApps`
        HashSet<String> oldBlockedApps = new HashSet<>();
        for (String app : blockedApps) {
            oldBlockedApps.add(app);
        }
        blockedApps.clear();

        // Reconstruct
        for (Schedule schedule : schedules) {
            if (schedule.isActive()) {
                Log.v(TAG + " normal schedule", schedule.getName());
                for (ProfileInSchedule pis : schedule.getCalendar()) {
                    if (pis.repeatsOn().contains(todayInRepeatEnum())) { // profile has to be repeated on current day
                        int startTime = getTimeInInt(pis.getStartTime());
                        int endTime = getTimeInInt(pis.getEndTime());

                        if ((startTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE) <= now &&
                                now <= (startTime + SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE)) {
                            sendNotification(generateNotificationID(NOTIFICATION_ID_PROFILE_CHANGE),
                                    "Profile : " + pis.getProfile().getName() + " is now active", FOCUS_PACKAGE_NAME);
                            addAppsToBlockedApps(pis.getProfile());
                            db.activateProfileInSchedule(pis, schedule.getName());
                        } else if ((endTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2) <= now &&
                                now <= (endTime + SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2)) {
                            sendNotification(generateNotificationID(NOTIFICATION_ID_PROFILE_CHANGE),
                                    "Profile : " + pis.getProfile().getName() + " is now inactive", FOCUS_PACKAGE_NAME);
                            db.deactivateProfileInSchedule(pis, schedule.getName());
                            db.addToStatsNoDistractHours((endTime - startTime) / 10000);
                            Log.v(BackgroundService.class.getName(), "addToStatsNoDistractHours, now = " + db.getStatsNoDistractHours());
                        } else if ((startTime + SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE) <= now &&
                                now <= (endTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2)) {
                            addAppsToBlockedApps(pis.getProfile());
                        }
                    }
                }
            }
        }

        fastTick(true);

        ArrayList<ProfileInSchedule> temp = null;
        try {
            temp = db.getAllDeletedProfileInSchedule();
        } catch (ParseException e) {
            Log.e(TAG, "failed to extract deleted PIS");
        }

        if (temp != null) {
            if (temp.size() > 0) {
                for (ProfileInSchedule pis : temp) {
                    sendNotification(
                            generateNotificationID(NOTIFICATION_ID_ANONYMOUS_SCHEDULE_DELETED),
                            "Profile : " + pis.getProfile().getName() + " is now inactive", FOCUS_PACKAGE_NAME
                    );
                }
            }
        }


        // compare old and new list, call appropriate function as necessary
        if (!blockedApps.equals(oldBlockedApps)) {
            if (oldBlockedApps.removeAll(blockedApps) || (blockedApps.size() == 0)) { // returns true of something is removed
                for (String app : oldBlockedApps) {
                    // notify user about missed notifications
                    int count = db.getNotificationsCountForApp(app);
                    if (count != 0) {
                        sendNotification(generateNotificationID(NOTIFICATION_ID_UNSEEN_NOTIFICATIONS),
                                "You have " + count + " unseen notifications from " +
                                        getAppNameFromPackage(app), app);
                    }
                }
            }
        }
    }


    private void fastTick(boolean call) {               //call will be true if called by tick, else false
        int now = getTimeInInt(new Date()); // get system time

        for (ProfileInSchedule pis : anonymousPIS) { // separate case for ANONYMOUS_SCHEDULE
            int startTime = getTimeInInt(pis.getStartTime());
            int endTime = getTimeInInt(pis.getEndTime());

            if ((startTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE) <= now && now <= (startTime + 60)) {
                if (anonymousPISOldSize < anonymousPIS.size()) {
                    if (!call)
                        sendNotification(generateNotificationID(NOTIFICATION_ID_ANONYMOUS_SCHEDULE_ACTIVE),
                                "Profile : " + pis.getProfile().getName() + " is now active", FOCUS_PACKAGE_NAME);
                    anonymousPISOldSize = anonymousPIS.size();
                }

            } else if ((endTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2) <= now &&
                    now <= (endTime + SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2)) {
                if (!call)
                    sendNotification(generateNotificationID(NOTIFICATION_ID_ANONYMOUS_SCHEDULE_INACTIVE),
                            "Profile : " + pis.getProfile().getName() + " is now inactive", FOCUS_PACKAGE_NAME);


                if (db.removeProfileFromSchedule(pis, ANONYMOUS_SCHEDULE)) {
                    Log.d(TAG, "to be implemented");
                }
                continue; // do not add this profile's apps to blockedApps
            }

            // add this Profile's apps to blockedApps
            addAppsToBlockedApps(pis.getProfile());
        }
    }

    /**
     * This function randomizes notification ID to prevent notifications from overwriting one another
     *
     * @param id the notification ID of specific message
     * @return notification id in the ten-thousands and thousands, last three digits randomized to avoid conflicts
     */
    private int generateNotificationID(int id) {
        return id * 1000 + (int) (Math.random() * 100);
    }

    /**
     * Adds apps specified in Profile into `blockedApps`.
     * This should only be called with an active profile.
     *
     * @param activeProfile an active profile by time or user override
     */
    private void addAppsToBlockedApps(dreamteam.focus.Profile activeProfile) {
        for (String app : activeProfile.getApps()) {
            blockedApps.add(app);
        }
    }

    /**
     * Helper function for tick()
     *
     * @param time Date
     * @return int, in the format signifying time in format: HHmmss
     * @link BackgroundService#tick tick()
     */
    @SuppressWarnings("SpellCheckingInspection")
    private int getTimeInInt(Date time) {
        return Integer.parseInt(new SimpleDateFormat("HH", Locale.US).format(time)) * 10000
                + Integer.parseInt(new SimpleDateFormat("mm", Locale.US).format(time)) * 100
                + Integer.parseInt(new SimpleDateFormat("ss", Locale.US).format(time));
    }

    /**
     * @link https://stackoverflow.com/questions/19604097
     */
    private void blockApps() {
        String appInForeground = getForegroundTask();
        if (appInForeground == null)
            return;

        for (String app : blockedApps) { // block each app in blockedApps
            if (appInForeground.equals(app)) {
                ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

                // go back to main screen
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(startMain);

                // kill process
                am.killBackgroundProcesses(appInForeground);
                Toast.makeText(getBaseContext(),
                        "Focus! has blocked " + getAppNameFromPackage(appInForeground),
                        Toast.LENGTH_SHORT
                ).show();
                db.addToStatsAppInstancesBlocked(1);
                Log.v(BackgroundService.class.getName(), "addToStatsAppInstancesBlocked, now = " + db.getStatsAppInstancesBlocked());
            }
        }
    }


    /**
     * @return package name of foreground app
     * @link https://stackoverflow.com/questions/2166961
     */
    private String getForegroundTask() {
        String currentApp = null;
        if (isUsageAccessGranted()) {
            currentApp = "NULL";
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList =
                    usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);

            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
            //Log.v(TAG, "printForegroundTask: " + currentApp);
        }
        return currentApp;
    }

    /**
     * Checks local database version number against remote version number
     *
     * @return True if local data is needs update, false otherwise
     */
    private boolean needUpdate() {
//        Log.v("BS.needUpdate()", databaseVersion + " < " + db.getDatabaseVersion());
        return databaseVersion < db.getDatabaseVersion();
    }

    /**
     * Pulls data from server.
     */
    private void updateFromServer() {
        final String TAG = "updateFromServer()";
//        Log.i(TAG, "getting update from server");
        try {
            schedules = db.getSchedules();
            anonymousPIS = db.getProfilesInSchedule(ANONYMOUS_SCHEDULE);
            databaseVersion = db.getDatabaseVersion(); // sync version with db
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error getting schedules from database.");
        }
        Log.i(TAG, "Completed update.");
    }

    /**
     * @param packageName: string of package name from which app name is derived
     * @link https://stackoverflow.com/questions/41054355
     */
    private String getAppNameFromPackage(String packageName) {
        PackageManager manager = getApplicationContext().getPackageManager();
        ApplicationInfo info;
        try {
            info = manager.getApplicationInfo(packageName, 0);
        } catch (final Exception e) {
            info = null;
        }
        return (String) (info != null ? manager.getApplicationLabel(info) : "this app");
    }

    /**
     * @return True if enabled, false otherwise.
     * @link https://stackoverflow.com/questions/38686632
     */
    private boolean isUsageAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Notification Broadcast Receiver
     */
    public class NLServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("broadcastreceived", "broadcastreceived");

            if (intent.getStringExtra("command").equals("list")) {
                Intent i1 = new Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event", "=====================");
                sendBroadcast(i1);
                int i = 1;
                for (StatusBarNotification sbn : BackgroundService.this.getActiveNotifications()) {
                    Intent i2 = new Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event", i + " " + sbn.getPackageName() + "\n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event", "===== Notification List ====");
                sendBroadcast(i3);
            }

        }

    }

    public String getDateString(java.util.Date d) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(d);
    }

}
