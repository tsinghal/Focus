package dreamteam.focus.client.profiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
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
import dreamteam.focus.client.adaptors.AdapterProfiles;
import dreamteam.focus.server.DatabaseConnector;

import static dreamteam.focus.client.profiles.EditProfileActivity.IntentNameString;

/**
 * Created by shatrujeet lawa on 10/8/2017.
 */

public class ProfilesActivity extends AppCompatActivity {

    private Button addNewProfile;

    ArrayList<Profile> profileArray;
    AdapterProfiles profileArrayAdapter;
    ListView profileListView;
    int profileLimit = 20;
    DialogInterface.OnClickListener dialogClickListener;
    private Button clear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        //add profileList from the database

        final DatabaseConnector db = new DatabaseConnector(getApplicationContext());
        // updateList(); //every time list opens up,update Profile list
        profileArray = new ArrayList<Profile>();
        profileArray = MainActivity.db.getProfiles();
        profileArrayAdapter = new AdapterProfiles(getApplicationContext(), profileArray);
        profileListView = (ListView) findViewById(R.id.listViewProfiles);
        profileListView.setAdapter(profileArrayAdapter);


        addNewProfile = (Button) findViewById(R.id.buttonAddProfile);

        addNewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.db.getProfiles().size() < profileLimit) {
                    Intent i1 = new Intent("ProfileInstantActivate");
                    i1.putExtra("message", "profileName" );
                    sendBroadcast(i1);
                    Intent i = new Intent(getApplicationContext(), CreateProfileActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "You cant have more than 20 profiles", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        ArrayList<Profile> temp = db.getProfiles();
                        for(int i=0; i<temp.size(); i++){
                            MainActivity.db.removeProfile(temp.get(i).getName());
                        }
                        updateList();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        clear = (Button) findViewById(R.id.buttonClear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure you want to delete all profiles?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(getApplicationContext(),"OnRestart()",Toast.LENGTH_SHORT).show(); ;
        updateList();
    }


    public void updateList() {
        profileArray = new ArrayList<Profile>();
        profileArray = MainActivity.db.getProfiles();
        profileArrayAdapter = new AdapterProfiles(getApplicationContext(), profileArray);
        profileListView = (ListView) findViewById(R.id.listViewProfiles);
        profileListView.setAdapter(profileArrayAdapter);
    }
}
