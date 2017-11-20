package dreamteam.focus.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dreamteam.focus.R;
import dreamteam.focus.client.profiles.ProfilesActivity;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 11/4/17.
 */

public class StatisticsActivity extends AppCompatActivity {
    public static DatabaseConnector db;
    private TextView distractionHours,notificationsBlocked,appsBlocked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_statistics);

        distractionHours=(TextView)findViewById(R.id.textViewTotalDistractionFreeHours);
        notificationsBlocked=(TextView)findViewById(R.id.textViewTotalNotificationsBlocked);
        appsBlocked=(TextView)findViewById(R.id.textViewTotalAppInstancesBlocked);

        distractionHours.setText(MainActivity.db.getStatsNoDistractHours());
        notificationsBlocked.setText(MainActivity.db.getStatsBlockedNotifications());
        appsBlocked.setText(MainActivity.db.getStatsAppInstancesBlocked());

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
    }
}
