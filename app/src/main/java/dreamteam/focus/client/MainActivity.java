package dreamteam.focus.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import dreamteam.focus.R;
import dreamteam.focus.server.DatabaseConnector;

public class MainActivity extends AppCompatActivity {
    private Button schedulesButton;
    private Button profilesButton;


    public static DatabaseConnector db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new DatabaseConnector(getApplicationContext());

        schedulesButton=(Button)findViewById(R.id.buttonSchedules);
        profilesButton=(Button)findViewById(R.id.buttonProfiles);

        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Profiles.class);
                startActivity(i);

            }
        });

    }






}
