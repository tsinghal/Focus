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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.client.ArrangeAppsByName;
import dreamteam.focus.client.MainActivity;
import dreamteam.focus.client.adaptors.AdapterProfiles;
import dreamteam.focus.server.DatabaseConnector;

import static dreamteam.focus.client.profiles.EditProfileActivity.IntentNameString;
import static dreamteam.focus.client.profiles.EditProfileActivity.map;

/**
 * Created by shatrujeet lawa on 10/8/2017.
 */

public class ProfilesActivity extends AppCompatActivity {

    private Button addNewProfile;
    private Button sortProfile;

    ArrayList<Profile> profileArray;
    ArrayList<Profile> pArray;
    AdapterProfiles profileArrayAdapter;
    ListView profileListView;
    int profileLimit = 20;


    //code added for Delete multiple - Tushar

    AlertDialog.Builder alertdialogbuilder;
    String[] AlertDialogItems;
    boolean[] Selectedtruefalse;
    private Button delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        //add profileList from the database

        final DatabaseConnector db = new DatabaseConnector(getApplicationContext());

        //Sort the Profiles according to frequency
        profileArray = new ArrayList<Profile>();
        profileArray = MainActivity.db.getProfiles();

//        HashMap<Profile,Integer> mapping=new HashMap<Profile ,Integer>(); //for displaying profile according to frequency
//
//        for(int i=0;i<pArray.size();i++)
//        {
//          mapping.put(pArray.get(i),MainActivity.db.getProfileFrequency(pArray.get(i).getName()));
//        }
//        Map sortedMap = sortByValue(mapping);
//        Iterator it = sortedMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            profileArray.add((Profile)pair.getKey());
//            it.remove(); // avoids a ConcurrentModificationException
//        }


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

        sortProfile=(Button)findViewById(R.id.buttonSortProfiles);
        sortProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortProfiles();
            }
        });


        delete = (Button) findViewById(R.id.buttonClear);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertdialogbuilder = new AlertDialog.Builder(ProfilesActivity.this);


                int i=-1;
                AlertDialogItems = new String[profileArray.size()];
                for(Profile p: profileArray) {
                    AlertDialogItems[++i] = p.getName();
                }

                Selectedtruefalse = new boolean[profileArray.size()];
                for(int j=0; j<profileArray.size(); j++) {
                    Selectedtruefalse[j] = false;
                }

                alertdialogbuilder.setMultiChoiceItems(AlertDialogItems, Selectedtruefalse, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                });

                alertdialogbuilder.setCancelable(false);

                alertdialogbuilder.setTitle("Delete multiple Profiles");

                alertdialogbuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        int a = 0;
                                        while(a < Selectedtruefalse.length)
                                        {
                                            boolean value = Selectedtruefalse[a];

                                            if(value){
                                                MainActivity.db.removeProfile(profileArray.get(a).getName());
                                            }
                                            a++;
                                        }
                                        updateList();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilesActivity.this);
                        builder.setMessage("Are you sure you want to delete selected profiles?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

                    }
                });
                alertdialogbuilder.setNeutralButton("Delete All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilesActivity.this);
                        builder.setMessage("Are you sure you want to delete all profiles?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                    }
                });

                alertdialogbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });


                AlertDialog dialog = alertdialogbuilder.create();

                dialog.show();


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

        int i=-1;
        AlertDialogItems = new String[profileArray.size()];
        for(Profile p: profileArray) {
            AlertDialogItems[++i] = p.getName();
        }

        Selectedtruefalse = new boolean[profileArray.size()];
        for(int j=0; j<profileArray.size(); j++) {
            Selectedtruefalse[j] = false;
        }
    }

    public void sortProfiles() {

        //src: https://stackoverflow.com/questions/34381536/sort-a-hashmap-by-the-integer-value-desc

        Map<String, Integer> map = new HashMap<>();
        profileArray=new ArrayList<Profile>();

        pArray=new ArrayList<Profile>();
        pArray=MainActivity.db.getProfiles();
        for(int i=0;i<pArray.size();i++)
        {
            map.put(pArray.get(i).getName(),MainActivity.db.getProfileFrequency(pArray.get(i).getName()));
        }

        Object[] a = map.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return -((Map.Entry<String, Integer>) o1).getValue().compareTo(
                        ((Map.Entry<String, Integer>) o2).getValue());
            }
        });
        for (Object e : a) {
           profileArray.add(MainActivity.db.getProfileByName(((Map.Entry<String, Integer>) e).getKey()));
        }

        profileArrayAdapter = new AdapterProfiles(getApplicationContext(), profileArray);
        profileListView = (ListView) findViewById(R.id.listViewProfiles);
        profileListView.setAdapter(profileArrayAdapter);

        int i=-1;
        AlertDialogItems = new String[profileArray.size()];
        for(Profile p: profileArray) {
            AlertDialogItems[++i] = p.getName();
        }

        Selectedtruefalse = new boolean[profileArray.size()];
        for(int j=0; j<profileArray.size(); j++) {
            Selectedtruefalse[j] = false;
        }
    }

}

