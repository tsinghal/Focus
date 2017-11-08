package dreamteam.focus.server;

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
import dreamteam.focus.client.MainActivity;

/**
 * src: https://stackoverflow.com/questions/41425986
 */

public class BackgroundService extends NotificationListenerService {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String ACTION_STATUS_BROADCAST =
            "com.example.notifyservice.NotificationService_Status";
    private static final String TAG = "BackgroundService";
    private static final int SCHEDULE_TIMEOUT_SEC = 3;
    private static final int BLOCKING_TIMEOUT_SEC = 1;
    private static final double WINDOW_SIZE = 0.5;

    private static final String ANONYMOUS_SCHEDULE = "AnonymousSchedule";

//    private static final int NOTIFICATION_ID_GENERIC = 0;
    private static final int NOTIFICATION_ID_SUPPRESS_NOTIFICATION = 1;
    private static final int NOTIFICATION_ID_PROFILE_CHANGE = 2;
    private static final int NOTIFICATION_ID_UNSEEN_NOTIFICATIONS = 3;
    private static final int NOTIFICATION_ID_ANONYMOUS_SCHEDULE_ACTIVE = 11;
    private static final int NOTIFICATION_ID_ANONYMOUS_SCHEDULE_INACTIVE = 12;
    private static final int NOTIFICATION_ID_ANONYMOUS_SCHEDULE_DELETED = 13;

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
            Log.d(TAG, "scheduleThread created");
            scheduleThread = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(scheduleThread, SCHEDULE_TIMEOUT_SEC * 1000);
//                    Log.d(TAG, "scheduleThread");
                    if (needUpdate()) updateFromServer();
                    tick();
                }
            };
            mHandler.postDelayed(scheduleThread, SCHEDULE_TIMEOUT_SEC * 1000);
        }

        if (blockingThread == null) {
            Log.d(TAG, "blockingThread created");
            blockingThread = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(blockingThread, BLOCKING_TIMEOUT_SEC * 1000);
//                    Log.d(TAG, "blockingThread");
                    blockApps();
                }
            };

            mHandler.postDelayed(blockingThread, BLOCKING_TIMEOUT_SEC * 1000);
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

    @Override
    @SuppressWarnings("SpellCheckingInspection")
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("onNotificationPosted", "ID: " + sbn.getId() + " " + sbn.getNotification().tickerText +
                "\t" + sbn.getPackageName());

        String packageName = getNameFromSBN(sbn);

        // block each app's notifications in `blockedApps`
        for (String app : blockedApps) {
            if (packageName.equals(app)) {
                cancelNotification(sbn.getKey());
                db.addBlockedNotification(app); // tell database to add count to this app
                sendNotification(NOTIFICATION_ID_SUPPRESS_NOTIFICATION, "Notification blocked by Focus!");
            }
        }
        // cancel only that notification of Focus (used to dismiss heads-up notifications from other apps
        if (packageName.equals("dreamteam.focus") && sbn.getId() == NOTIFICATION_ID_SUPPRESS_NOTIFICATION) {
            cancelNotification(sbn.getKey());
        }
    }

    /**
     * Sends a notification from this application package to the Android System
     *
     * @param id      the id of this notification
     * @param message the message of this notification
     */
    private void sendNotification(int id, String message) {
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, MainActivity.class);

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("User Alert")
                .setContentText(message);

        // defines where the user will be directed if notification is clicked
        mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        // Builds the notification and issues it.
        mNotifyMgr.notify(id, mBuilder.build());
        Log.i("sendNotification", "A notification was thrown!");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        Log.i(TAG, "********** onNotificationRemoved");
//        Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
//        Intent i = new  Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
//        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");
//        sendBroadcast(i);

    }

    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.w("NotificationService", "Notification listener DISCONNECTED from the notification service! " +
                "\nScheduling a reconnect...");
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.w("NotificationService", "Notification listener connected with the notification service!");
    }

    private String getNameFromSBN(StatusBarNotification sbn) {
        return sbn.getPackageName();
    }

    private void broadcastStatus() {
//        Log.i(TAG, "Broadcasting status added(" + nAdded + ")/removed(" + nRemoved + ")");
        Intent i1 = new Intent(ACTION_STATUS_BROADCAST);
//        i1.putExtra("serviceMessage", "Added: " + nAdded + " | Removed: " + nRemoved);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i1);
        //TODO: profileMessage, scheduleMessage
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
                                    "Profile : " + pis.getProfile().getName() + " is now active");
                            addAppsToBlockedApps(pis.getProfile());
                            db.activateProfileInSchedule(pis, schedule.getName());
                        } else if ((endTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2) <= now &&
                                now <= (endTime + SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2)) {
                            sendNotification(generateNotificationID(NOTIFICATION_ID_PROFILE_CHANGE),
                                    "Profile : " + pis.getProfile().getName() + " is now inactive");
                            db.deactivateProfileInSchedule(pis, schedule.getName());
                        } else if ((startTime + SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE) <= now &&
                                now <= (endTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2)) {
                            addAppsToBlockedApps(pis.getProfile());
                        }
                    }
                }
            }
        }

        for (ProfileInSchedule pis : anonymousPIS) { // separate case for ANONYMOUS_SCHEDULE
            int startTime = getTimeInInt(pis.getStartTime());
            int endTime = getTimeInInt(pis.getEndTime());

            if ((startTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE) <= now && now <= (startTime + 60)) {
                if (anonymousPISOldSize < anonymousPIS.size()) {
//                    sendNotification(generateNotificationID(NOTIFICATION_ID_PROFILE_CHANGE), "Your profile is now instantly active.");
                    sendNotification(generateNotificationID(NOTIFICATION_ID_ANONYMOUS_SCHEDULE_ACTIVE),
                            "Profile : " + pis.getProfile().getName() + " is now active");
                    anonymousPISOldSize = anonymousPIS.size();
                }

            } else if ((endTime - SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2) <= now &&
                    now <= (endTime + SCHEDULE_TIMEOUT_SEC * WINDOW_SIZE * 2)) {
                sendNotification(generateNotificationID(NOTIFICATION_ID_ANONYMOUS_SCHEDULE_INACTIVE),
                        "Profile : " + pis.getProfile().getName() + " is now inactive");


                if (db.removeProfileFromSchedule(pis, ANONYMOUS_SCHEDULE)) {
                    // TODO: tell UI(do only after updating database)
                    Log.d(TAG, "to be implemented");
                }
                continue; // do not add this profile's apps to blockedApps
            }


            // add this Profile's apps to blockedApps
            addAppsToBlockedApps(pis.getProfile());
        }

//        if (anonymousPISOldSize > anonymousPIS.size()) {
//            sendNotification(
//                    generateNotificationID(NOTIFICATION_ID_ANONYMOUS_SCHEDULE_INACTIVE),
//                    "Your profile is now inactive"
//            );
//            anonymousPISOldSize = anonymousPIS.size();
//        }

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
                            "Profile : " + pis.getProfile().getName() + " is now inactive"
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
                                        getAppNameFromPackage(app));
                    }
                }
            }
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
}
