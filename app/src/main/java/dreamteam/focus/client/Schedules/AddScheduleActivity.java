package dreamteam.focus.client.Schedules;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.Adaptors.AdapterCalendarNewRemove;
import dreamteam.focus.client.ListUtils;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/13/17.
 */

public class AddScheduleActivity extends AppCompatActivity {

    private Button addSchedule, discard, delete;
    private EditText nameText;
    private Button addProfileButton;
    private ArrayList<Schedule> schedulesInDatabase;
    public static ArrayList<ProfileInSchedule> profileArray; //set current profilesInSchedule withoutchanging database
    public static ArrayList<Integer> positionArray;
    public static ArrayList<ProfileInSchedule> pisArray;
    public static ArrayList<ProfileInSchedule> profileInScheduleArray;
    AdapterCalendarNewRemove mon,tue,wed,thu,fri,sat,sun;
    private EditText name;
    private DatabaseConnector db;
    public static String scheduleName;
    private ArrayList<ProfileInSchedule> mondaySchedules, tuesdaySchedules, wednesdaySchedules, thursdaySchedules,
            fridaySchedules, saturdaySchedules, sundaySchedules;
    private ListView monday,tuesday, wednesday, thursday, friday, saturday, sunday;
    private Schedule s;
    private boolean check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createschedule);

        pisArray=new ArrayList<ProfileInSchedule>();  //when u add new PIS,it keeps track of it for dubmit button
        positionArray=new ArrayList<Integer>();  //when u add new PIS,it keeps track of it for submit button
        profileInScheduleArray=new ArrayList<ProfileInSchedule>(); //when u delete PIS from a schedule,it keeps track of it and deletes it on addSchedulre
        check = false;
        db = new DatabaseConnector(this);
        try {
            schedulesInDatabase = db.getSchedules();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        addProfileButton = (Button) findViewById(R.id.buttonAddProfileToSchedule3);

        addSchedule = (Button) findViewById(R.id.buttonAddSchedule3);
        discard = (Button) findViewById(R.id.buttonDiscardSchedule3);
        name = (EditText) findViewById(R.id.textEditScheduleName3);



        addProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //scheduleName = name.getText().toString();
//                if(name.getText().toString().matches("")){
//                    Toast.makeText(getApplicationContext(), "Please Enter A Name First", Toast.LENGTH_SHORT).show();
//                }
//                else {

                if(!check)
                {
                    String names = "TemporaryScheduleNameToCreate";
                    s = new Schedule(names);
                    try {
                        if (!db.hasSchedule(names)){
                            try {
                                //db.addSchedule(s);
                                check = true;
                                Intent i = new Intent(getApplicationContext(), AddProfileToNewSchedule.class);
                                //i.putExtra("AddScheduleActivity:ScheduleName", scheduleName);
                                startActivity(i);
                            }catch (android.database.SQLException e){
                                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Name, Name Already Exists!", Toast.LENGTH_SHORT).show();
                        }
                    } catch(SQLException e) {
                        Log.e("errorS", e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Intent i = new Intent(getApplicationContext(), AddProfileToNewSchedule.class);
                    i.putExtra("AddScheduleActivity:ScheduleName", scheduleName);
                    startActivity(i);
                }
            }
            // }
        });


        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.hasSchedule(s.getName()) && check){
                    try {
                        db.removeSchedule(s.getName());
                        finish();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
            }
        });

        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!check)
                {

                    if(name.getText().toString().matches("")){
                        Toast.makeText(getApplicationContext(), "Please Enter A Name First", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String names = name.getText().toString();
                        s = new Schedule(names);
                        try {
                            if (!db.hasSchedule(names)) {
                                try{
                                    db.addSchedule(s);
                                }catch (android.database.SQLException e){
                                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                                check = true;
                                //Intent i = new Intent(getApplicationContext(), SchedulesActivity.class);
                                finish();
                            } else {

                                Toast.makeText(getApplicationContext(), "Invalid Name, Name Already Exisits!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (SQLException e) {
                            Log.e("errorS", e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else {
                    if(name.getText().toString().matches("")){
                        Toast.makeText(getApplicationContext(), "Please Enter A Name First", Toast.LENGTH_SHORT).show();
                    } else {
                        String names = name.getText().toString();
                        if(db.hasSchedule(names)){
                            Toast.makeText(getApplicationContext(), "Please Enter A Unique Name", Toast.LENGTH_SHORT).show();
                        } else {
                            if (s.getName().equals(names)) {
                                db.addSchedule(s);
                            } else {
                                s.setName(names);
                                db.addSchedule(s);
                            }
                            finish();
                        }
                    }
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

        monday = (ListView) findViewById(R.id.listViewMonday3);
        mon = new AdapterCalendarNewRemove(getApplicationContext(), mondaySchedules);
        monday.setAdapter(mon);


        tuesday = (ListView) findViewById(R.id.listViewTuesday3);
        tue = new AdapterCalendarNewRemove(getApplicationContext(), tuesdaySchedules);
        tuesday.setAdapter(tue);

        wednesday = (ListView) findViewById(R.id.listViewWednesday3);
        wed = new AdapterCalendarNewRemove(getApplicationContext(), wednesdaySchedules);
        wednesday.setAdapter(wed);

        thursday = (ListView) findViewById(R.id.listViewThursday3);
        thu = new AdapterCalendarNewRemove(getApplicationContext(), thursdaySchedules);
        thursday.setAdapter(thu);

        friday = (ListView) findViewById(R.id.listViewFriday3);
        fri = new AdapterCalendarNewRemove(getApplicationContext(), fridaySchedules);
        friday.setAdapter(fri);

        saturday = (ListView) findViewById(R.id.listViewSaturday3);
        sat = new AdapterCalendarNewRemove(getApplicationContext(), saturdaySchedules);
        saturday.setAdapter(sat);

        sunday = (ListView) findViewById(R.id.listViewSunday3);
        sun = new AdapterCalendarNewRemove(getApplicationContext(), sundaySchedules);
        sunday.setAdapter(sun);

        ListUtils.setDynamicHeight(monday);
        ListUtils.setDynamicHeight(tuesday);
        ListUtils.setDynamicHeight(wednesday);
        ListUtils.setDynamicHeight(thursday);
        ListUtils.setDynamicHeight(friday);
        ListUtils.setDynamicHeight(saturday);
        ListUtils.setDynamicHeight(sunday);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(check)
        {
            if(db.hasSchedule(s.getName()) && check){
                try {
                    db.removeSchedule(s.getName());
                    finish();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
            }
        }
    }

}
