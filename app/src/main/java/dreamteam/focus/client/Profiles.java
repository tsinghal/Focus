package dreamteam.focus.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.sql.Array;
import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;

/**
 * Created by shatrujeet lawa on 10/8/2017.
 */

public class Profiles extends AppCompatActivity {

    private Button addNewProfile;
    ArrayList<Profile> profileArray;
    AdapterProfiles profileArrayAdapter;
    ListView profileListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        //add profileList from the database
        profileArray= new ArrayList<Profile>();
        Profile p=new Profile("Hello",null,null);
        profileArray.add(p);
        Profile q=new Profile("Hi Maete",null,null);
        profileArray.add(q);

        profileArrayAdapter=new AdapterProfiles(getApplicationContext(), profileArray);

        profileListView=(ListView)findViewById(R.id.listViewProfiles);
        profileListView.setAdapter(profileArrayAdapter);




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
