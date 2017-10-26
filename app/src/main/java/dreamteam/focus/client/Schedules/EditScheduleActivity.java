package dreamteam.focus.client.Schedules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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


public class EditScheduleActivity extends AppCompatActivity implements Serializable{

    private Button addSchedule, discard, delete;
    private EditText nameText;
    private Button addProfileButton;
    public static ArrayList<ProfileInSchedule> profileArray;  //the static rference for the ProfileInSchedule,vhanges nothing in database
    public static ArrayList<Integer> positionArray;
    public static ArrayList<ProfileInSchedule> pisArray; //contains the profileInSchedules that needs to be removed from the DB
    public static ArrayList<ProfileInSchedule> profileInScheduleArray;//contains the profileInSchedules that needs to be added to the DB
    AdapterCalendarRemove mon,tue,wed,thu,fri,sat,sun;
    private EditText name;
    public static String scheduleName;
    DatabaseConnector db;
    private ArrayList<ProfileInSchedule> mondaySchedules, tuesdaySchedules, wednesdaySchedules, thursdaySchedules,
            fridaySchedules, saturdaySchedules, sundaySchedules;
    private ListView monday,tuesday, wednesday, thursday, friday, saturday, sunday;
    public Schedule CurrentSchedule;

    public static String editPISOld="OldPIS";
    public static String editPISNew="NewPIS";


    Handler mHandler = new Handler(); //this stuff is to update the list every second Timer
    Thread downloadThread;
    boolean isRunning = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_edit_schedule);
        db=new DatabaseConnector(this);
        pisArray=new ArrayList<ProfileInSchedule>();
        positionArray=new ArrayList<Integer>();
        profileInScheduleArray=new ArrayList<ProfileInSchedule>();


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            scheduleName = bundle.getString("Schedule Name");
        }

        try {
            for(int i=0;i<db.getSchedules().size();i++)
            {
                if(db.getSchedules().get(i).getName().equals(scheduleName))
                {
                    CurrentSchedule=db.getSchedules().get(i);
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
                i.putExtra("EditScheduleActivity:ScheduleName", scheduleName); //TODO: check this
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
                    try {

                        db.addProfileInSchedule(profileInScheduleArray.get(i), scheduleName);
                    }
                    catch(android.database.SQLException e)
                    {
                        boolean checks=true;

                        ProfileInSchedule pp=profileInScheduleArray.get(i);
                        for (int j=0;j<pisArray.size();j++) {

                            ProfileInSchedule currentPIS=pisArray.get(j);

                            if(currentPIS.getProfile().getName().toString().equals(pp.getProfile().getName().toString()))
                            {
                                if (currentPIS.getStartTime().getTime()==pp.getStartTime().getTime() && currentPIS.getEndTime().getTime()==pp.getEndTime().getTime())
                                {
                                    if(currentPIS.repeatsOn().size()!=0) {
                                        if(currentPIS.repeatsOn().get(0)==pp.repeatsOn().get(0)) {
                                            Log.d("Taggss","It does contain");
                                            pisArray.remove(currentPIS);
                                            checks=false;

                                        }
                                    }
                                }
                            }
                        }

                        if(checks)
                            Toast.makeText(getApplicationContext(),"You can't have two Profiles with same components",Toast.LENGTH_SHORT).show();

                    }

                }

                for(int i=0;i<pisArray.size();i++)
                {
                    pis=pisArray.get(i);
//                    pos=positionArray.get(i);
                    db.removeProfileFromSchedule(pis,scheduleName, pis.repeatsOn().get(0));
                }






                String newName = name.getText().toString();
                if(name.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(), "Please Enter A Name First", Toast.LENGTH_SHORT).show();
                } else {
                    if (!newName.equals(scheduleName)) {
                        if (!db.hasSchedule(newName)) {
                            db.updateScheduleName(scheduleName, newName);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Schedule Name Already Exisits!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        finish();
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


        //This code would be for Timer
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (isRunning) {
                    try {
                        Thread.sleep(1000); // run at every 1 seconds
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                updateList();
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        });

        downloadThread.start();
        //code for Timer ends



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateList();
    }

    public void updateList()
    {
//
//        try {
//            profileArray = (ArrayList<ProfileInSchedule>) db.getProfilesInSchedule(scheduleName);
//            Log.e("error","In EditSchedule.java -> getting profileArray of: "+scheduleName+ " FOUND: "+profileArray.size());
//        } catch (ParseException e) {
//            e.getMessage();
//        }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {     //Update profile array with new Profile In schedule
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                Bundle b=data.getExtras();
                ProfileInSchedule oldPIS=(ProfileInSchedule) b.getSerializable(editPISOld);
                ProfileInSchedule newPIS=(ProfileInSchedule) b.getSerializable(editPISNew);


                for (int i=0;i<profileArray.size();i++) {

                    ProfileInSchedule currentPIS=profileArray.get(i);

                    if(currentPIS.getProfile().getName().toString().equals(oldPIS.getProfile().getName().toString()))
                    {
                        if (currentPIS.getStartTime().getTime()==oldPIS.getStartTime().getTime() && currentPIS.getEndTime().getTime()==oldPIS.getEndTime().getTime())
                        {
                            if(currentPIS.repeatsOn().size()!=0) {
                                if(currentPIS.repeatsOn().get(0)==oldPIS.repeatsOn().get(0)) {
                                    Log.d("Tagsy","It does contain");
                                    profileArray.remove(currentPIS);
                                    pisArray.add(currentPIS);
                                }
                            }
                        }
                    }
                }

                try {
                    if(db.getProfilesInSchedule(scheduleName).contains(oldPIS)){
                        Log.d("Tagsy","It does contain"+profileArray.size());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //profileArray.remove(oldPIS);
                profileArray.add(newPIS);

                profileInScheduleArray.add(newPIS);
                //pisArray.add(oldPIS);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        updateList();
    }
}
