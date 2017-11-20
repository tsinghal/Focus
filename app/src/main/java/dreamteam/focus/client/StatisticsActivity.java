package dreamteam.focus.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import dreamteam.focus.R;
import dreamteam.focus.client.profiles.ProfilesActivity;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 11/4/17.
 */

public class StatisticsActivity extends AppCompatActivity {
    public static DatabaseConnector db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_statistics);
    }
}
