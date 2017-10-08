package dreamteam.focus.server;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";

    private Runnable scheduleThread = null;
    private Runnable blockingThread = null;
    private static final String tag = "BackgroundService";
    private final int SCHEDULE_TIMEOUT = 60;
    private final int BLOCKING_TIMEOUT = 1;
    private DatabaseConnector DBConnector;
    private NotificationService notificationService;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    //global variable for blockApps so that overlap works

    public BackgroundService() {
        DBConnector = new DatabaseConnector();
        notificationService = new NotificationService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * src= https://stackoverflow.com/questions/28292682/using-an-sqlite-database-from-a-service-in-android
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String TAG = "onStartCommand";
        final Handler mHandler = new Handler();
        if (scheduleThread == null) {
            Log.d(TAG, "scheduleThread created");
            scheduleThread = new Runnable() {
                @Override
                public void run() {
                    // TODO: round to next minute-2sec
                    mHandler.postDelayed(scheduleThread, SCHEDULE_TIMEOUT * 1000);
//                    Log.d(tag, "scheduleThread");
                }
            };
            mHandler.postDelayed(scheduleThread, SCHEDULE_TIMEOUT * 1000);
        }

        if (blockingThread == null) {
            blockingThread = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(blockingThread, BLOCKING_TIMEOUT * 1000);
                   // Log.d(tag, "blockingThread");
                  //notificationService.dismissNotification("com.facebook.orca");
                    printForegroundTask();
                }
            };

            mHandler.postDelayed(blockingThread, BLOCKING_TIMEOUT * 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public void tick() {
        // TODO
        // read database
        // get schedule objects from db
        // get systemTime
        // list appsToBlock
        // list appsToUnblock
        // loop through Schedules
        //  for each Schedule:
        //      get Map of ProfileInSchedule
        //      for each ProfileInSchedule:
        //          if ProfileInSchedule.getStartTime() == systemTime:
        //              get profile object
        //              get apps from profile
        //              push all apps to block onto appsToBlock
        //          else if ProfileInSchedule.getEndTime() == systemTime:
        //              get profile object
        //              get apps from profile
        //              push all apps to unblock onto appsToUnblock
        //      set Schedule to active/inactive, update database
        // write to appBlock module: appsToBlock, appsToUnblock
        // write to notificationBlock module: appsToBlock, appsToUnblock
    }

    //function to check for instant profile activation -- how to know when to check

    public void blockApps() {

    }

    /**
     * src = https://stackoverflow.com/questions/19604097/killbackgroundprocesses-no-working
     *
     * @param app: string of app to be killed
     */
    public void killApp(String app) {
        final String TAG = "killApp";

        // some in-built exceptions to the kill app function
        if (app.equals("com.htc.launcher") || app.equals("dreamteam.focus") || app.equals("com.google.android.apps.nexuslauncher"))
            return;

        Log.i(TAG, app);
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        // go back to main screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(startMain);

        // kill process
        am.killBackgroundProcesses(app);
        Toast.makeText(getBaseContext(),
                "Focus! has blocked " + getAppNameFromPackage(app),
                Toast.LENGTH_LONG
        ).show();
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
        String appName = (String) (info != null ? manager.getApplicationLabel(info) : "this app");
        return appName;
    }

    /**
     * src = https://stackoverflow.com/questions/2166961/determining-the-current-foreground-application-from-a-background-task-or-service
     * check which app comes to foreground,
     * TODO: need to define a way so that this is fast and there is no delay(doesnt rely on original runnable),
     * Todo: and also make killing app faster so that app main screen doesn't show up.
     */
    public void printForegroundTask() {
        final String TAG = "printForegroundTask";
        String currentApp = "NULL";

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);

            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
            Log.i(TAG, currentApp);

        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        killApp(currentApp);
    }

    /**
     * to dismiss notifications:
     * instantiate NotificationService, call dismissNotification
     */

    public class NotificationService extends NotificationListenerService {
        private final String TAG = "NotificationService";

        public NotificationService() {
            super();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return super.onBind(intent);
        }

        @Override
        public void onNotificationPosted(StatusBarNotification sbn) {
            Log.d(TAG, "Posted");
            String app = getNameFromSBN(sbn);
            Intent intent = new Intent("com.github.chagall.notificationlistenerexample");
            intent.putExtra("app", app);
            sendBroadcast(intent);
        }

        @Override
        public void onNotificationRemoved(StatusBarNotification sbn) {
            Log.d(TAG, "Removed");
            String app = getNameFromSBN(sbn);
            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if (activeNotifications != null && activeNotifications.length > 0) {
                for (int i = 0; i < activeNotifications.length; i++) {
                    if (app.equals(getNameFromSBN(activeNotifications[i]))) {
                        Intent intent = new Intent("com.github.chagall.notificationlistenerexample");
                        intent.putExtra("app", app);
                        sendBroadcast(intent);
                        return;
                    }
                }
            }
        }

        private String getNameFromSBN(StatusBarNotification sbn) {
            String packageName = sbn.getPackageName();
            Log.d(TAG, packageName);
            return packageName;
        }

        public void dismissNotification(String app) {
            Log.d(TAG, "dismiss " + app);
            cancelNotification(app);
        }
    }


}


/**
 * TODO
 * - blockApp(String): Toast user the app is blocked
 * - unblockApp(String)
 * - check SQLite version number
 * -
 * -
 * -
 */