package dreamteam.focus.client.Schedules;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

import dreamteam.focus.R;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.Adaptors.AdapterSchedules;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/13/17.
 */

public class SchedulesActivity extends AppCompatActivity {

    private Button addNewSchedule;
    public static DatabaseConnector db;
    private TextView name;
    public ListView lvNames;

//    private BroadcastReceiver MyReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.i("SchedulesActivity", "Broadcast Recieved: "+intent.getStringExtra("scheduleMessage"));
//            String message = intent.getStringExtra("serviceMessage");
//            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//            updateList();
//        }
//    };


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
                    Intent i = new Intent(getApplicationContext(), AddScheduleActivity.class);
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        LocalBroadcastManager.getInstance(this).registerReceiver(MyReceiver, new IntentFilter("com.example.notifyservice.NotificationService_Status"));
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(MyReceiver);
//    }



}
