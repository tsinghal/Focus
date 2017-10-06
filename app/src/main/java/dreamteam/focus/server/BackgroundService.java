package dreamteam.focus.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends Service {
    public Runnable mRunnable = null;
    private static final String tag = "BackgroundService";
    private int seconds = 40;
    private DatabaseConnector DBConnector;

    public BackgroundService() {
        DBConnector = new DatabaseConnector();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /*
     * src:
     * https://stackoverflow.com/questions/28292682/using-an-sqlite-database-from-a-service-in-android
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Handler mHandler = new Handler();
        if (mRunnable == null) {
            Log.i(tag, "BackgroundService created");
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(mRunnable, seconds * 1000);
                    Log.i(tag, "false!");
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
    }
}
