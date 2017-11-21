package dreamteam.focus.client;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import dreamteam.focus.R;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 11/4/17.
 */

public class StatisticsActivity extends AppCompatActivity {
    public static DatabaseConnector db;
    BarGraphSeries<DataPoint> series1, series2, series3;
    private TextView distractionHours,notificationsBlocked,appsBlocked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_statistics);

        distractionHours=(TextView)findViewById(R.id.textViewTotalDistractonFreeHours);
        notificationsBlocked=(TextView)findViewById(R.id.textViewTotalNotificationsBlocked);
        appsBlocked=(TextView)findViewById(R.id.textViewTotalAppInstancesBlocked);

        distractionHours.setText(MainActivity.db.getStatsNoDistractHours()+"");
        notificationsBlocked.setText(MainActivity.db.getStatsBlockedNotifications()+"");
        appsBlocked.setText(MainActivity.db.getStatsAppInstancesBlocked()+"");

        Button uploadButton = (Button) findViewById(R.id.buttonUploadStatistics);
        Button downloadButton = (Button) findViewById(R.id.buttonDownloadStatistics);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GoogleAuthentication.class));
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GoogleAuthentication.class));
            }
        });

        //updateGraphs();

//
//        graph1 = (GraphView) findViewById(R.id.graph1);
//        series1 = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0,MainActivity.db.getStatsAppInstancesBlocked() ),
//                new DataPoint(1,getSystemAppsCount() ),
//
//        });
//        graph1.addSeries(series1);
//
//// styling
//        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
//            @Override
//            public int get(DataPoint data) {
//                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
//            }
//        });
//
//        series1.setSpacing(50);
//
//// draw values on top
//        series1.setDrawValuesOnTop(true);
//        series1.setValuesOnTopColor(Color.RED);
//
//        graph1.getViewport().setMinX(0);
//        graph1.getViewport().setMaxX(1.2);
//        graph1.getViewport().setMinY(0);
//        graph1.getViewport().setMaxY(150);
//        graph1.getViewport().setYAxisBoundsManual(true);
//        graph1.getViewport().setXAxisBoundsManual(true);
//
//
//        graph2 = (GraphView) findViewById(R.id.graph2);
//        series2 = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, MainActivity.db.getStatsAppInstancesBlocked()),
//                new DataPoint(1, MainActivity.db.getStatsNoDistractHours()),
//
//        });
//        graph2.addSeries(series2);
//
//// styling
//        series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
//            @Override
//            public int get(DataPoint data) {
//                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
//            }
//        });
//
//        series2.setSpacing(50);
//
//// draw values on top
//        series2.setDrawValuesOnTop(true);
//        series2.setValuesOnTopColor(Color.RED);
//
//        graph2.getViewport().setMinX(0);
//        graph2.getViewport().setMaxX(1.2);
//        graph2.getViewport().setMinY(0);
//        graph2.getViewport().setMaxY(150);
//        graph2.getViewport().setYAxisBoundsManual(true);
//        graph2.getViewport().setXAxisBoundsManual(true);
//
//        graph3 = (GraphView) findViewById(R.id.graph3);
//        series3 = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, MainActivity.db.getStatsAppInstancesBlocked()),
//                new DataPoint(1, MainActivity.db.getStatsBlockedNotifications()),
//
//        });
//        graph3.addSeries(series2);
//
//// styling
//        series3.setValueDependentColor(new ValueDependentColor<DataPoint>() {
//            @Override
//            public int get(DataPoint data) {
//                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
//            }
//        });
//
//        series3.setSpacing(50);
//
//// draw values on top
//        series3.setDrawValuesOnTop(true);
//        series3.setValuesOnTopColor(Color.RED);
//
//        graph3.getViewport().setMinX(0);
//        graph3.getViewport().setMaxX(1.2);
//        graph3.getViewport().setMinY(0);
//        graph3.getViewport().setMaxY(150);
//        graph3.getViewport().setYAxisBoundsManual(true);
//        graph3.getViewport().setXAxisBoundsManual(true);
    }

    public int getSystemAppsCount() {
        PackageManager pm = getPackageManager();
        HashMap<String, String> map = new HashMap<String, String>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            String appName = getAppNameFromPackage(packageInfo.packageName);

            if (!appName.equals("this app") && !appName.contains(".") && !packageInfo.packageName.equals("com.htc.launcher") && !packageInfo.packageName.equals("dreamteam.focus") && !packageInfo.packageName.equals("com.google.android.apps.nexuslauncher") && !packageInfo.packageName.equals("com.android.systemui") && !packageInfo.packageName.equals("com.google.android.packageinstaller")) {

                map.put(packageInfo.packageName, appName);
            }
        }
        ArrangeAppsByName arrange = new ArrangeAppsByName();
        TreeMap<String, String> sortedMap = arrange.sortMapByValue(map);
        return sortedMap.size();

    }

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

    @Override
    protected void onRestart() {
        super.onRestart();
        distractionHours.setText(MainActivity.db.getStatsNoDistractHours() + "");
        notificationsBlocked.setText(MainActivity.db.getStatsBlockedNotifications() + "");
        appsBlocked.setText(MainActivity.db.getStatsAppInstancesBlocked() + "");
        updateGraphs();


    }

    @Override
    protected void onResume() {
        super.onResume();
        distractionHours.setText(MainActivity.db.getStatsNoDistractHours() + "");
        notificationsBlocked.setText(MainActivity.db.getStatsBlockedNotifications() + "");
        appsBlocked.setText(MainActivity.db.getStatsAppInstancesBlocked() + "");
        updateGraphs();
    }

    public void updateGraphs() {

        GraphView graph1, graph2, graph3;

        series1 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0,MainActivity.db.getStatsAppInstancesBlocked() ),
                new DataPoint(1,getSystemAppsCount() ),

        });


// styling
        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series1.setSpacing(50);

// draw values on top
        series1.setDrawValuesOnTop(true);
        series1.setValuesOnTopColor(Color.RED);

        series2 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, MainActivity.db.getStatsAppInstancesBlocked()),
                new DataPoint(1, MainActivity.db.getStatsNoDistractHours()),

        });

        series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series2.setSpacing(50);

// draw values on top
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.RED);

        series3 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, MainActivity.db.getStatsAppInstancesBlocked()),
                new DataPoint(1, MainActivity.db.getStatsBlockedNotifications()),

        });

        // styling
        series3.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series3.setSpacing(50);

// draw values on top
        series3.setDrawValuesOnTop(true);
        series3.setValuesOnTopColor(Color.RED);

        graph1 = (GraphView) findViewById(R.id.graph1);
        graph2 = (GraphView) findViewById(R.id.graph2);
        graph3 = (GraphView) findViewById(R.id.graph3);

        graph1.addSeries(series1);
        graph2.addSeries(series2);
        graph3.addSeries(series3);

        graph1.getViewport().setMinX(0);
        graph1.getViewport().setMaxX(1.2);
        graph1.getViewport().setMinY(0);
        graph1.getViewport().setMaxY(150);
        graph1.getViewport().setYAxisBoundsManual(true);
        graph1.getViewport().setXAxisBoundsManual(true);

        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(1.2);
        graph2.getViewport().setMinY(0);
        graph2.getViewport().setMaxY(150);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setXAxisBoundsManual(true);

        graph3.getViewport().setMinX(0);
        graph3.getViewport().setMaxX(1.2);
        graph3.getViewport().setMinY(0);
        graph3.getViewport().setMaxY(150);
        graph3.getViewport().setYAxisBoundsManual(true);
        graph3.getViewport().setXAxisBoundsManual(true);


    }
}
