package dreamteam.focus.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import dreamteam.focus.R;

/**
 * Created by shatrujeet lawa on 10/8/2017.
 */

public class Profiles extends AppCompatActivity {

    private Button addNewProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        addNewProfile=(Button)findViewById(R.id.buttonAddProfile);

        addNewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),CreateProfile.class);
                startActivity(i);
            }
        });
    }
}
