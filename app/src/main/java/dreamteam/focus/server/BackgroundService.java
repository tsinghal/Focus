package dreamteam.focus.server;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.MainActivity;

/*
    src: https://stackoverflow.com/questions/41425986/call-a-notification-listener-inside-a-background-service-in-android-studio
 */

public class BackgroundService extends NotificationListenerService {
    public static final String ACTION_STATUS_BROADCAST = "com.example.notifyservice.NotificationService_Status";
    private static final String TAG = "BackgroundService";
    private static final int SCHEDULE_TIMEOUT_SEC = 60;
    private static final int BLOCKING_TIMEOUT_SEC = 1;
    private static final String[] BLOCK_APP_WHITELIST = {
            "com.htc.launcher",
            "dreamteam.focus",
            "com.google.android.apps.nexuslauncher",
            "com.android.systemui"
    };

    private Runnable scheduleThread = null;
    private Runnable blockingThread = null;
    private final DatabaseConnector databaseConnector;
    private ArrayList<Schedule> schedules;
    private long databaseVersion;
    private HashSet<String> blockedApps;
    private NLServiceReceiver nlServiceReceiver;
    NotificationManager mNotifyMgr;
    Notification.Builder mBuilder;

    private int nAdded = 0; // Number of notifications added (since the service started)
    private int nRemoved = 0; // Number of notifications removed (since the service started)

    public BackgroundService() {
        databaseConnector = new DatabaseConnector();
        schedules = new ArrayList<>();
        if (checkForUpdates()) updateFromServer();
        blockedApps = new HashSet<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    /**
     * src= https://stackoverflow.com/questions/28292682/using-an-sqlite-database-from-a-service-in-android
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
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

        String packageName = getNameFromSBN(sbn);
        //Intent i = new  Intent("notification received");
        //i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName() + "\n");
        //i.putExtra("notification_event", packageName);
        //sendBroadcast(i);

        if (packageName.equals("com.whatsapp")) {

            // Sets an ID for the notification
            int mNotificationId = 1;
            sendNotification(mNotificationId, "Notification blocked by Focus!");

            cancelNotification(sbn.getKey());
        }

        if (packageName.equals("dreamteam.focus")) {
            if (sbn.getId() == 1)                    //cancel only that nofication which is used to block notifications of other apps
                cancelNotification(sbn.getKey());
        }
        nAdded++;
        broadcastStatus();
    }

    //send out notification to user from Focus
    public void sendNotification(int id, String message) {

        // Gets an instance of the NotificationManager service
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_name_notify)
                .setContentTitle("Focus")
                .setContentText(message);

        mBuilder.setContentIntent(resultPendingIntent);     //defines where the user will be directed if notification is clicked

        // Builds the notification and issues it.
        mNotifyMgr.notify(id, mBuilder.build());
        Log.d("onNotificationPosted", "notification thrown!");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
//        Intent i = new  Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
//        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");
//        sendBroadcast(i);

        nRemoved++;
        broadcastStatus();
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
        String packageName = sbn.getPackageName();
        Log.d(TAG, "getNameFromSBN" + packageName);
        return packageName;
    }

    private void broadcastStatus() {
        Log.i(TAG, "Broadcasting status added(" + nAdded + ")/removed(" + nRemoved + ")");
        Intent i1 = new Intent(ACTION_STATUS_BROADCAST);
        i1.putExtra("serviceMessage", "Added: " + nAdded + " | Removed: " + nRemoved);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i1);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        nlServiceReceiver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.notifyservice.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlServiceReceiver, filter);
        Log.i("onCreate", "NotificationService created!");
    }

    @Override
    public void onDestroy() {
        // TODO check handle thread deletions?
        super.onDestroy();
        unregisterReceiver(nlServiceReceiver);
        Log.i("NotificationService", "NotificationService destroyed.");
    }


    public void tick() {
        final String TAG = "BackgroundService.update";
        if (checkForUpdates()) updateFromServer();
        long millis = new Date().getTime();
        HashSet<String> oldBlockedApps = new HashSet<>();
        for (String app : blockedApps) {
            oldBlockedApps.add(app);
        }
        blockedApps.clear();
        for (Schedule schedule : schedules) {
            Log.i(TAG, schedule.getName());
            if (schedule.isActive()) {
                for (ProfileInSchedule pis : schedule.getCalendar()) {
                    if (pis.getStartTime().getTime() - SCHEDULE_TIMEOUT_SEC * 1000 <= millis &&
                            millis <= pis.getStartTime().getTime() + SCHEDULE_TIMEOUT_SEC * 1000) {
                        // TODO: 10/14/17 Notify user profile activated

                    } else if (pis.getEndTime().getTime() - SCHEDULE_TIMEOUT_SEC * 1000 <= millis &&
                            millis <= pis.getEndTime().getTime() + SCHEDULE_TIMEOUT_SEC * 1000) {
                        // TODO: 10/14/17 Notify user profile deactivated

                    }

                    // reconstruct blockedApps
                    if (pis.getProfile().isActive()) {
                        for (String app : pis.getProfile().getApps()) {
                            blockedApps.add(app);
                        }
                    }
                }
            }
            // TODO: set Schedule to active/inactive, update database

            // compare old and new list, call appropriate function as necessary
            if (!blockedApps.equals(oldBlockedApps)) {
                if (oldBlockedApps.removeAll(blockedApps)) { // returns true of something is removed
                    for (String app : oldBlockedApps) {
                        // TODO: 10/14/17 get notifications from database!
                        // TODO: 10/14/17 notify user
                    }
                }
            }

        }
        /* Pseudo code:
            read database
            get schedule objects from db
            get systemTime
            list appsToBlock
            list appsToUnblock
            loop through Schedules
            for each Schedule:
              get Map of ProfileInSchedule
              for each ProfileInSchedule:
                  if ProfileInSchedule.getStartTime() == systemTime:
                      get profile object
                      get apps from profile
                      push all apps to block onto appsToBlock
                  else if ProfileInSchedule.getEndTime() == systemTime:
                      get profile object
                      get apps from profile
                      push all apps to unblock onto appsToUnblock
              set Schedule to active/inactive, update database
            write to appBlock module: appsToBlock, appsToUnblock
            write to notificationBlock module: appsToBlock, appsToUnblock
         */

    }

    // TODO: function to check for instant profile activation -- how to know when to check

    /**
     * src = https://stackoverflow.com/questions/19604097/killbackgroundprocesses-no-working
     */
    public void blockApps() {

        String appInForeground = getForegroundTask();
        if (appInForeground == null)
            return;

//        for (String app : blockedApps) {}

        // some in-built exceptions to the kill app function
        for (String whitelistedApp : BLOCK_APP_WHITELIST) {
            if (appInForeground.equals(whitelistedApp)) return;
        }

        if (appInForeground.equals("com.whatsapp")) { // TODO: 10/14/17 this is for debug purposes
            Log.i(TAG, "blockApps(" + appInForeground + ")");
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


    /**
     * src = https://stackoverflow.com/questions/2166961/determining-the-current-foreground-application-from-a-background-task-or-service
     * check which app comes to foreground,
     */
    public String getForegroundTask() {
        String currentApp = null;
        if (isUsageAccessGranted()) {
            currentApp = "NULL";
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);

            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
            Log.i(TAG, "printForegroundTask: " + currentApp);
        }
        return currentApp;
    }

    /**
     * Checks local database version number against remote version number
     *
     * @return True if local data is up to date, false otherwise
     */
    private boolean checkForUpdates() {
        return databaseVersion >= databaseConnector.getDatabaseVersion();
    }

    /**
     * Pulls data from server.
     */
    private void updateFromServer() {
        Log.i(TAG, "getting update from server");
        schedules = databaseConnector.getSchedules();
        databaseVersion = databaseConnector.getDatabaseVersion();
        Log.i(TAG, "completed update");
    }

    /**
     * src = https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     *
     * @return True if enabled, false otherwise.
     */
    private boolean isNotificationServiceGranted() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * src = https://stackoverflow.com/questions/41054355/how-to-get-app-name-by-package-name-in-android
     *
     * @param packageName: string of package name from which app name is derived
     */
    public String getAppNameFromPackage(String packageName) {
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
     * src = https://stackoverflow.com/questions/38686632/how-to-get-usage-access-permission-programatically
     *
     * @return True if enabled, false otherwise.
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
    class NLServiceReceiver extends BroadcastReceiver {

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

// TODO: 10/14/17 Refactor and clean up code 
// TODO: 10/14/17 tell database about state changes
// TODO: 10/14/17 tell UI about shit 