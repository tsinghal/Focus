package dreamteam.focus.client.Profiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.client.MainActivity;
import dreamteam.focus.client.Adaptors.AdapterProfiles;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by shatrujeet lawa on 10/8/2017.
 */

public class ProfilesActivity extends AppCompatActivity {

    private Button addNewProfile;
    ArrayList<Profile> profileArray;
    AdapterProfiles profileArrayAdapter;
    ListView profileListView;
    int profileLimit=20;


    private BroadcastReceiver MyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("ProfilesActivity", "Broadcast Recieved: "+intent.getStringExtra("profileMessage"));
            String message = intent.getStringExtra("serviceMessage");
            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
           updateList();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        //add profileList from the database

        DatabaseConnector db=new DatabaseConnector(getApplicationContext());
       // updateList(); //every time list opens up,update Profile list
        profileArray= new ArrayList<Profile>();
        profileArray= MainActivity.db.getProfiles();
        profileArrayAdapter=new AdapterProfiles(getApplicationContext(), profileArray);
        profileListView=(ListView)findViewById(R.id.listViewProfiles);
        profileListView.setAdapter(profileArrayAdapter);






        addNewProfile=(Button)findViewById(R.id.buttonAddProfile);

        addNewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.db.getProfiles().size()<=profileLimit)
                {
                    Intent i = new Intent(getApplicationContext(), CreateProfileActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You cant have more than 20 profiles",Toast.LENGTH_LONG);
                }
            }
        });
    }


    @Override
    protected void onRestart(){
        super.onRestart();
        //Toast.makeText(getApplicationContext(),"OnRestart()",Toast.LENGTH_SHORT).show(); ;
        updateList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(MyReceiver, new IntentFilter("com.example.notifyservice.NotificationService_Status"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(MyReceiver);
    }

    public void updateList() {
        profileArray= new ArrayList<Profile>();
        profileArray=MainActivity.db.getProfiles();
        profileArrayAdapter=new AdapterProfiles(getApplicationContext(), profileArray);
        profileListView=(ListView)findViewById(R.id.listViewProfiles);
        profileListView.setAdapter(profileArrayAdapter);
    }
}
