package dreamteam.focus.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.server.DatabaseConnector;

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

        DatabaseConnector db=new DatabaseConnector(getApplicationContext());
       // updateList(); //every time list opens up,update Profile list
        updateList();

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

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),"OnResume()",Toast.LENGTH_SHORT) ;
        updateList();
    }


    @Override
    protected void onRestart(){
        super.onRestart();
        Toast.makeText(getApplicationContext(),"OnRestart()",Toast.LENGTH_SHORT) ;
        updateList();
    }

    public void updateList() {
        profileArray= new ArrayList<Profile>();
        profileArray=MainActivity.db.getProfiles();
    }
}
