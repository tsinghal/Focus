package dreamteam.focus.server;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.widget.Toast;

public class BackgroundService extends Service {
    public Runnable mRunnable = null;
    private static final String tag = "BackgroundService";
    private int seconds = 5;
    private DatabaseConnector DBConnector;
    private NotificationService notificationService;

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
        if (mRunnable == null) {
            Log.i(tag, "BackgroundService created");
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    // TODO: round to next minute
                    mHandler.postDelayed(mRunnable, seconds * 1000);
                    Log.i(tag, "Ping!");
                }
            };
            mHandler.postDelayed(mRunnable, seconds * 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public void tick() {
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


    /**
     * to dismiss notifications:
     * instantiate NotificationService, call dismissNotification
     */
    private class NotificationService extends NotificationListenerService {
        public NotificationService() {
            super();
        }

        public void dismissNotification(String app) {
            Log.i("dismissNotification", app);
            cancelNotification(app);
        }
    }

    /**
     * src= https://stackoverflow.com/questions/19604097/killbackgroundprocesses-no-working
     *
     * @param app: string of app to be killed
     */
    public void killApp(String app) {
        Log.i("killApp", app);
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

}


