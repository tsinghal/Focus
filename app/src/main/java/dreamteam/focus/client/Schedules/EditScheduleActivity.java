package dreamteam.focus.client.Schedules;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.ListUtils;
import dreamteam.focus.client.MainActivity;
import dreamteam.focus.client.Adaptors.AdapterCalendarRemove;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/13/17.
 */

public class EditScheduleActivity extends AppCompatActivity {

    private Button addSchedule, discard, delete;
    private EditText nameText;
    private Button addProfileButton;
    public static ArrayList<ProfileInSchedule> profileArray;
    public static ArrayList<Integer> positionArray;
    public static ArrayList<ProfileInSchedule> pisArray;
    public static ArrayList<ProfileInSchedule> profileInScheduleArray;
    AdapterCalendarRemove mon,tue,wed,thu,fri,sat,sun;
    private EditText name;
    private DatabaseConnector db;
    public static String scheduleName;
    private ArrayList<ProfileInSchedule> mondaySchedules, tuesdaySchedules, wednesdaySchedules, thursdaySchedules,
            fridaySchedules, saturdaySchedules, sundaySchedules;
    private ListView monday,tuesday, wednesday, thursday, friday, saturday, sunday;
    public Schedule CurrentSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_edit_schedule);
        pisArray=new ArrayList<ProfileInSchedule>();
        positionArray=new ArrayList<Integer>();
        profileInScheduleArray=new ArrayList<ProfileInSchedule>();

        db = new DatabaseConnector(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            scheduleName = bundle.getString("Schedule Name");
        }


        try {
            for(int i = 0; i< MainActivity.db.getSchedules().size(); i++)
            {
                if(MainActivity.db.getSchedules().get(i).getName().equals(scheduleName))
                {
                    CurrentSchedule=MainActivity.db.getSchedules().get(i);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        addProfileButton = (Button) findViewById(R.id.buttonAddProfileToSchedule);

        addSchedule = (Button) findViewById(R.id.buttonAddSchedule);
        discard = (Button) findViewById(R.id.buttonDiscardSchedule);
        delete = (Button)findViewById(R.id.buttonDeleteChanges2);
        name = (EditText) findViewById(R.id.textEditScheduleName);

        name.setText(scheduleName);

        addProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddProfileToScheduleActivity.class);
                i.putExtra("EditScheduleActivity:ScheduleName", scheduleName);
                startActivity(i);
            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });

        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileInSchedule pis;
                Profile p;
                int pos;

                for(int i=0;i<profileInScheduleArray.size();i++)
                {
                    Log.d("TAG2:", profileInScheduleArray.get(i).repeatsOn().toString());
                    db.addProfileInSchedule(profileInScheduleArray.get(i), scheduleName);
                }
                
                for(int i=0;i<pisArray.size();i++)
                {
                    pis=pisArray.get(i);
                    pos=positionArray.get(i);
                    Log.d("TAG2", String.valueOf(db.removeProfileFromSchedule(pis,scheduleName, pis.repeatsOn().get(0))));
                }


                String newName = name.getText().toString();

                if(!newName.equals(scheduleName)){
                    if(!db.hasSchedule(newName)){
                        db.updateScheduleName(scheduleName, newName);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Schedule Name Already Exisits!",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    finish();
                }

                //ADD TO DATABASE HERE
//                String names = name.getText().toString();
//                scheduleName;
//
//                Schedule sch = new Schedule(names, false);
//                db.updateSchedule()
//                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try {
                    db.removeSchedule(scheduleName);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });


        try {
            profileArray = (ArrayList<ProfileInSchedule>) db.getProfilesInSchedule(scheduleName);
            Log.e("error","In EditScheduleActivity.java -> getting profileArray of: "+scheduleName+ " FOUND: "+profileArray.size());
        } catch (ParseException e) {
            e.getMessage();
        }

        updateList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateList();
    }

    public void updateList()
    {
        mondaySchedules = new ArrayList<ProfileInSchedule>();
        tuesdaySchedules = new ArrayList<ProfileInSchedule>();
        wednesdaySchedules = new ArrayList<ProfileInSchedule>();
        thursdaySchedules = new ArrayList<ProfileInSchedule>();
        fridaySchedules = new ArrayList<ProfileInSchedule>();
        saturdaySchedules = new ArrayList<ProfileInSchedule>();
        sundaySchedules = new ArrayList<ProfileInSchedule>();


        for (int i = 0; i < profileArray.size(); i++) {
            //ArrayList<Repeat_Enum> r = profileArray.get(i).repeatsOn();
            Log.e("error","Profile Enum Value: "+ profileArray.get(i).repeatsOn().toString());
            if(profileArray.get(i).repeatsOn().contains(Repeat_Enum.MONDAY)){
                mondaySchedules.add(profileArray.get(i));
            }
            if(profileArray.get(i).repeatsOn().contains(Repeat_Enum.TUESDAY)){
                tuesdaySchedules.add(profileArray.get(i));
            }
            if(profileArray.get(i).repeatsOn().contains(Repeat_Enum.WEDNESDAY)){
                wednesdaySchedules.add(profileArray.get(i));
            }
            if(profileArray.get(i).repeatsOn().contains(Repeat_Enum.THURSDAY)){
                thursdaySchedules.add(profileArray.get(i));
            }
            if(profileArray.get(i).repeatsOn().contains(Repeat_Enum.FRIDAY)){
                fridaySchedules.add(profileArray.get(i));
            }
            if(profileArray.get(i).repeatsOn().contains(Repeat_Enum.SATURDAY)){
                saturdaySchedules.add(profileArray.get(i));
            }
            if(profileArray.get(i).repeatsOn().contains(Repeat_Enum.SUNDAY)){
                sundaySchedules.add(profileArray.get(i));
            }

        }

        monday = (ListView) findViewById(R.id.listViewMonday);
        mon = new AdapterCalendarRemove(getApplicationContext(), mondaySchedules);
        monday.setAdapter(mon);


        tuesday = (ListView) findViewById(R.id.listViewTuesday);
        tue = new AdapterCalendarRemove(getApplicationContext(), tuesdaySchedules);
        tuesday.setAdapter(tue);

        wednesday = (ListView) findViewById(R.id.listViewWednesday);
        wed = new AdapterCalendarRemove(getApplicationContext(), wednesdaySchedules);
        wednesday.setAdapter(wed);

        thursday = (ListView) findViewById(R.id.listViewThursday);
        thu = new AdapterCalendarRemove(getApplicationContext(), thursdaySchedules);
        thursday.setAdapter(thu);

        friday = (ListView) findViewById(R.id.listViewFriday);
        fri = new AdapterCalendarRemove(getApplicationContext(), fridaySchedules);
        friday.setAdapter(fri);

        saturday = (ListView) findViewById(R.id.listViewSaturday);
        sat = new AdapterCalendarRemove(getApplicationContext(), saturdaySchedules);
        saturday.setAdapter(sat);

        sunday = (ListView) findViewById(R.id.listViewSunday);
        sun = new AdapterCalendarRemove(getApplicationContext(), sundaySchedules);
        sunday.setAdapter(sun);

        ListUtils.setDynamicHeight(monday);
        ListUtils.setDynamicHeight(tuesday);
        ListUtils.setDynamicHeight(wednesday);
        ListUtils.setDynamicHeight(thursday);
        ListUtils.setDynamicHeight(friday);
        ListUtils.setDynamicHeight(saturday);
        ListUtils.setDynamicHeight(sunday);

    }

    public boolean getState()
    {
        return CurrentSchedule.isActive();
    }
}
