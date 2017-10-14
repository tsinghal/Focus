package dreamteam.focus.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import dreamteam.focus.R;

public class MainActivity extends AppCompatActivity {
    private Button schedulesButton;
    private Button profilesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        schedulesButton=(Button)findViewById(R.id.buttonSchedules);
        profilesButton=(Button)findViewById(R.id.buttonProfiles);

        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Profiles.class);
                startActivity(i);

            }
        });

        schedulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s=new Intent(getApplicationContext(),Schedules.class);
                startActivity(s);

            }
        });

    }






}
