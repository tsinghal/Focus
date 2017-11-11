package dreamteam.focus.client.schedules;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.MainActivity;
import dreamteam.focus.client.adaptors.AdapterSchedules;
import dreamteam.focus.client.profiles.ProfilesActivity;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/13/17.
 */

public class SchedulesActivity extends AppCompatActivity {

    private Button addNewSchedule;
    public static DatabaseConnector db;
    private TextView name;
    public ListView lvNames;

    ArrayList<Schedule> scheduleArray;
    AdapterSchedules scheduleArrayAdapter;
    DialogInterface.OnClickListener dialogClickListener;


    //code added for Delete multiple - Tushar

    AlertDialog.Builder alertdialogbuilder;
    String[] AlertDialogItems;
    boolean[] Selectedtruefalse;
    private Button delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_schedules);


        db = new DatabaseConnector(this);
        /*
        Profile p = new Profile("zzz",new ArrayList<String>());
        db.createProfile(p);
        Profile d = new Profile("Driving",new ArrayList<String>());
        db.createProfile(d);
        Profile z = new Profile("Midterm",new ArrayList<String>());
        db.createProfile(z);
        */
        addNewSchedule = (Button) findViewById(R.id.buttonAddSchedule);
        name = (TextView) findViewById(R.id.textViewScheduleName);

        addNewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Schedule> schedulesInDb = null;
                try {
                    schedulesInDb = db.getSchedules();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (schedulesInDb.size() < 20) {
                    Intent i = new Intent(getApplicationContext(), AddScheduleActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "You Have Reached a 20 Schedule Max!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete = (Button) findViewById(R.id.buttonClear);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertdialogbuilder = new AlertDialog.Builder(SchedulesActivity.this);


                int i=-1;
                AlertDialogItems = new String[scheduleArray.size()];
                for(Schedule s: scheduleArray) {
                    AlertDialogItems[++i] = s.getName();
                }

                Selectedtruefalse = new boolean[scheduleArray.size()];
                for(int j=0; j<scheduleArray.size(); j++) {
                    Selectedtruefalse[j] = false;
                }

                alertdialogbuilder.setMultiChoiceItems(AlertDialogItems, Selectedtruefalse, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                });

                alertdialogbuilder.setCancelable(false);

                alertdialogbuilder.setTitle("Delete multiple Schedules");

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
                                                try {
                                                    MainActivity.db.removeSchedule(scheduleArray.get(a).getName());
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(SchedulesActivity.this);
                        builder.setMessage("Are you sure you want to delete selected schedules?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

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
                                        ArrayList<Schedule> temp = null;
                                        try {
                                            temp = db.getSchedules();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        for(int i=0; i<temp.size(); i++){
                                            try {
                                                MainActivity.db.removeSchedule(temp.get(i).getName());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        updateList();

                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(SchedulesActivity.this);
                        builder.setMessage("Are you sure you want to delete all schedules?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

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

        lvNames = (ListView) findViewById(R.id.ScheduleNames);

        updateList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateList();

    }

    public void updateList() {
        try {
            scheduleArray = db.getSchedules();
            for (int i = 0; i < scheduleArray.size(); i++) {
                Log.d("err", String.valueOf(scheduleArray.get(i).getCalendar().size()));
            }
        } catch (ParseException e) {
            Log.d("error", e.getMessage());
        }

        scheduleArrayAdapter = new AdapterSchedules(getApplicationContext(), scheduleArray);

        lvNames.setAdapter(scheduleArrayAdapter);

        int i=-1;
        AlertDialogItems = new String[scheduleArray.size()];
        for(Schedule s: scheduleArray) {
            AlertDialogItems[++i] = s.getName();
        }

        Selectedtruefalse = new boolean[scheduleArray.size()];
        for(int j=0; j<scheduleArray.size(); j++) {
            Selectedtruefalse[j] = false;
        }
    }

}
