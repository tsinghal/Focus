package dreamteam.focus.client;

import android.support.annotation.IntegerRes;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;

public class timePicker extends AppCompatActivity {

    TimePicker timePicker;
    Button buttonSetTime;
    static String IntentProfileName="profile Name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        timePicker=(TimePicker)findViewById(R.id.timePicker);
        buttonSetTime=(Button)findViewById(R.id.buttonSetTime);

        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile p=MainActivity.db.getProfileByName(getIntent().getStringExtra(IntentProfileName));

                Date startTime=new Date();
                startTime=Calendar.getInstance().getTime();
                SimpleDateFormat d1=new SimpleDateFormat("HH:mm");
                String s = d1.format(startTime);
                try {
                    startTime=d1.parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String startTimeString=new SimpleDateFormat("HH:mm").format(startTime);
                String[] parts=startTimeString.split(":");


                int startHour= Integer.parseInt(parts[0]);

                int startMin=Integer.parseInt(parts[1].toString());



                Date endTime=new Date();
                int endHour=timePicker.getHour();
                int endMin=timePicker.getMinute();

                try {
                    endTime=d1.parse(endHour+":"+endMin);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(endHour<startHour)
                {
                    Toast.makeText(getApplicationContext(),"Exceeding the ten hour limit",Toast.LENGTH_LONG).show();
                    return;

                }
                if(endHour==startHour && endMin<startMin)
                {
                    Toast.makeText(getApplicationContext(),"Exceeding the ten hour limit",Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d("Start Time",startHour+":"+startMin);
                Log.d("End Time",endHour+":"+endMin);


                int timeLimit=(endHour-startHour)*60;
                timeLimit+=(endMin-startMin);
                Log.d("Limit",timeLimit+"");
                Toast.makeText(getApplicationContext(),timeLimit+"", Toast.LENGTH_SHORT).show();
                if(timeLimit>600 || timeLimit<10)
                {
                    Toast.makeText(getApplicationContext(),"Exceeding the 10 minute-10 hour limit",Toast.LENGTH_LONG).show();
                    return;
                }

                MainActivity.db.activateProfile(new ProfileInSchedule(p,startTime,endTime));
                p= MainActivity.db.getProfileByName(p.getName());
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
    }


}
