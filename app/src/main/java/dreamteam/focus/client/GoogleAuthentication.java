package dreamteam.focus.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dreamteam.focus.R;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 11/8/17.
 */

public class GoogleAuthentication extends AppCompatActivity {
    public static DatabaseConnector db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_google);
    }
}
