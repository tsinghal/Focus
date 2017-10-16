package dreamteam.focus.client;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.Schedule;
import dreamteam.focus.server.DatabaseConnector;

import java.sql.Array;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by aarav on 10/13/17.
 */

public class Schedules extends AppCompatActivity {

    private Button addNewSchedule;
    public static DatabaseConnector db;
    private TextView name;
    public ListView lvNames;

    ArrayList<Schedule> scheduleArray;
    AdapterSchedules scheduleArrayAdapter;

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
        addNewSchedule=(Button)findViewById(R.id.buttonAddSchedule);
        name = (TextView)findViewById(R.id.textViewScheduleName);

        addNewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Schedule> schedulesInDb = null;
                try {
                    schedulesInDb = db.getSchedules();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(schedulesInDb.size()<21){
                    Intent i = new Intent(getApplicationContext(), CreateSchedule.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "You Have Reached a 20 Schedule Max!",Toast.LENGTH_SHORT).show();
                }
            }
        });


        lvNames = (ListView) findViewById(R.id.ScheduleNames);

        updateList();




    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Toast.makeText(getApplicationContext(),"OnRestart()", Toast.LENGTH_SHORT).show();
        updateList();

    }

    public void updateList()
    {
        try {
            scheduleArray = db.getSchedules();



            for(int i=0; i<scheduleArray.size();i++) {
                Log.d("err", String.valueOf(scheduleArray.get(i).getCalendar().size()));
            }

        } catch(ParseException e) {
            Log.d("error", e.getMessage());
        }


        scheduleArrayAdapter=new AdapterSchedules(getApplicationContext(), scheduleArray);

        lvNames.setAdapter(scheduleArrayAdapter);
    }


}
