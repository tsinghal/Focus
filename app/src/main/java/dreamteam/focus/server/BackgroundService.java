package dreamteam.focus.server;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.ContentValues.TAG;

public class BackgroundService extends Service {
    private Runnable scheduleThread = null;
    private Runnable blockingThread = null;
    private static final String tag = "BackgroundService";
    private final int SCHEDULE_TIMEOUT = 60;
    private final int BLOCKING_TIMEOUT = 2;
    private DatabaseConnector DBConnector;
    private NotificationService notificationService;

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
        final Handler mHandler = new Handler();
        if (scheduleThread == null) {
            Log.i(tag, "BackgroundService created");
            scheduleThread = new Runnable() {
                @Override
                public void run() {
                    // TODO: round to next minute-2sec
                    mHandler.postDelayed(scheduleThread, SCHEDULE_TIMEOUT * 1000);
                    Log.i(tag, "scheduleThread!");
//                    printForegroundTask();
//                    notificationService.dismissNotification("com.facebook.orca");
                }
            };
            blockingThread = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(blockingThread, BLOCKING_TIMEOUT * 1000);
                    Log.i(tag, "blockingThread");
                }
            }

            mHandler.postDelayed(scheduleThread, SCHEDULE_TIMEOUT * 1000);
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
     * to dismiss notifications:
     * instantiate NotificationService, call dismissNotification
     */
    public class NotificationService extends NotificationListenerService {
        // TODO
        public NotificationService() {
            super();
        }

        public void dismissNotification(String app) {
            Log.i("dismissNotification", app);
            cancelNotification(app);
        }

        public void sendNotification(String msg) {
            /**
             * req 8.1, 8.2,
             * 8.3 side: "you have unread notifications", user tap, redirect to notificationViewActivity
             */
        }
    }

    /**
     * src = https://stackoverflow.com/questions/19604097/killbackgroundprocesses-no-working
     *
     * @param app: string of app to be killed
     */
    public void killApp(String app) {
        Log.i("killApp", app);
        // some in-built exceptions to the kill app function
        if (app.equals("com.htc.launcher") || app.equals("dreamteam.focus")) return;

        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        // go back to main screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(startMain);

        // kill process
        am.killBackgroundProcesses(app);
        Toast.makeText(getBaseContext(),
                "Killed : " + app,
                Toast.LENGTH_LONG
        ).show();
    }

    /**
     * src = https://stackoverflow.com/questions/2166961/determining-the-current-foreground-application-from-a-background-task-or-service
     * check which app comes to foreground,
     * TODO: need to define a way so that this is fast and there is no delay(doesnt rely on original runnable),
     * Todo: and also make killing app faster so that app main screen doesn't show up.
     */
    public void printForegroundTask() {
        String currentApp = "NULL";

        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!mySortedMap.isEmpty()) {
                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        }
        Log.i(TAG, "Current app on the foreground is: " + currentApp);
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