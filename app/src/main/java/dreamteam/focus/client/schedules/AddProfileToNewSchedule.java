package dreamteam.focus.client.schedules;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.client.adaptors.AdapterAddProfileToNewSchedule;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/13/17.
 */

public class AddProfileToNewSchedule extends AppCompatActivity {

    private Button addProfile, discard;
    ArrayList<Profile> profileArray;
    AdapterAddProfileToNewSchedule profileArrayAdapter;
    private TimePicker startTime, endTime;
    private Switch monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private DatabaseConnector db;
    public static String scheduleName;
    ArrayList<Repeat_Enum> r;
    public static ArrayList<Profile> profilesToBeBlocked;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addprofiletoschedule);
        profilesToBeBlocked = new ArrayList<Profile>();

        db = new DatabaseConnector(this);


        scheduleName = "TemporaryScheduleNameToCreate";


        r = new ArrayList<Repeat_Enum>();


        startTime = (TimePicker) findViewById(R.id.timePickerStartTime);
        endTime = (TimePicker) findViewById(R.id.timePickerEndTime);

        addProfile = (Button) findViewById(R.id.buttonAddProfile);
        discard = (Button) findViewById(R.id.buttonDiscardProfile);

        monday = (Switch) findViewById(R.id.switch1);
        tuesday = (Switch) findViewById(R.id.switch2);
        wednesday = (Switch) findViewById(R.id.switch3);
        thursday = (Switch) findViewById(R.id.switch4);
        friday = (Switch) findViewById(R.id.switch5);
        saturday = (Switch) findViewById(R.id.switch6);
        sunday = (Switch) findViewById(R.id.switch7);

        ListView lvNames = (ListView) findViewById(R.id.ListViewProfilesToAdd);
        profileArray = new ArrayList<Profile>();
        profileArray = db.getProfiles();

        profileArrayAdapter = new AdapterAddProfileToNewSchedule(getApplicationContext(), profileArray);

        lvNames.setAdapter(profileArrayAdapter);

        monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    r.add(Repeat_Enum.MONDAY);
                } else {
                    if (r.contains(Repeat_Enum.MONDAY)) {
                        r.remove(Repeat_Enum.MONDAY);
                    }
                }
            }
        });

        tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    r.add(Repeat_Enum.TUESDAY);
                } else {
                    if (r.contains(Repeat_Enum.TUESDAY)) {
                        r.remove(Repeat_Enum.TUESDAY);
                    }
                }
            }
        });

        wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    r.add(Repeat_Enum.WEDNESDAY);
                } else {
                    if (r.contains(Repeat_Enum.WEDNESDAY)) {
                        r.remove(Repeat_Enum.WEDNESDAY);
                    }
                }
            }
        });

        thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    r.add(Repeat_Enum.THURSDAY);
                } else {
                    if (r.contains(Repeat_Enum.THURSDAY)) {
                        r.remove(Repeat_Enum.THURSDAY);
                    }
                }
            }
        });

        friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    r.add(Repeat_Enum.FRIDAY);
                } else {
                    if (r.contains(Repeat_Enum.FRIDAY)) {
                        r.remove(Repeat_Enum.FRIDAY);
                    }
                }
            }
        });

        saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    r.add(Repeat_Enum.SATURDAY);
                } else {
                    if (r.contains(Repeat_Enum.SATURDAY)) {
                        r.remove(Repeat_Enum.SATURDAY);
                    }
                }
            }
        });

        sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    r.add(Repeat_Enum.SUNDAY);
                } else {
                    if (r.contains(Repeat_Enum.SUNDAY)) {
                        r.remove(Repeat_Enum.SUNDAY);
                    }
                }
            }
        });

        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startHour = startTime.getHour();
                int startMin = startTime.getMinute();
                int endHour = endTime.getHour();
                int endMin = endTime.getMinute();
                Date startTime = new Date();
                Date endTime = new Date();

                Log.e("error", "ADD PROFILE CLICK INSIDE THE FUNCTION r: " + r.size());

                //Error Checking for 10 hours & see at least one day has been selected!
                if (r.isEmpty() || r.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Select At-least One Day", Toast.LENGTH_LONG).show();

                } else {
                    SimpleDateFormat d1 = new SimpleDateFormat("HH:mm");
                    try {
                        startTime = d1.parse(startHour + ":" + startMin);
                        endTime = d1.parse(endHour + ":" + endMin);
                        Log.e("error", "ADDED PROFILE TO START TIME: " + startTime.toString() + " END TIME: " + endTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (endHour < startHour) //if end of night
                    {

                        Toast.makeText(getApplicationContext(), "Exceeding the ten hour limit", Toast.LENGTH_LONG).show();
                        return;

                    }
                    if (endHour == startHour && endMin < startMin) {
                        Toast.makeText(getApplicationContext(), "Exceeding the ten hour limit", Toast.LENGTH_LONG).show();
                        return;
                    }


                    int timeLimit = (endHour - startHour) * 60;
                    timeLimit += (endMin - startMin);


                    if (timeLimit > 600 || timeLimit < 10) {
                        Toast.makeText(getApplicationContext(), "Exceeding the 10 minute-10 hour limit", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!profilesToBeBlocked.isEmpty()) {
                        for (int i = 0; i < profilesToBeBlocked.size(); i++) {
                            for (int j = 0; j < r.size(); j++) {
                                ArrayList<Repeat_Enum> individualR = new ArrayList<Repeat_Enum>();
                                individualR.add(r.get(j));
                                ProfileInSchedule p = new ProfileInSchedule(profilesToBeBlocked.get(i), startTime, endTime, individualR);
                                //db.addProfileInSchedule(p, scheduleName);
                                AddScheduleActivity.profileInScheduleArray.add(p);
                                AddScheduleActivity.profileArray.add(p);
                            }

                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Select At-least One Profile", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
