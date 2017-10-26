package dreamteam.focus.client.Schedules;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Array;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.client.MainActivity;

public class EditProfileInScheduleActivity extends AppCompatActivity {

    public static String namePIS="PIS";
    public Button updateSchedule;
    public Button discardChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofileinschedule);

        final Bundle b=getIntent().getExtras();
        final ProfileInSchedule pis = (ProfileInSchedule) b.getSerializable(namePIS);

//        if(b!=null)
//        {
//            pis=
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(),"Bundle is null",Toast.LENGTH_LONG);
//            finish();
//        }

        final TextView pisName=(TextView) findViewById(R.id.textViewPisName);
        final Spinner daySpinner=(Spinner)findViewById(R.id.spinnerPisDay);
        final TimePicker startTimePicker=(TimePicker)findViewById(R.id.timePickerPisStartTime);
        final TimePicker endTimePicker=(TimePicker)findViewById(R.id.timePickerPisEndTime);
        updateSchedule=(Button)findViewById(R.id.buttonPisSave);
        discardChanges=(Button)findViewById(R.id.buttonPisDiscard);

        pisName.setText(pis.getProfile().getName());
        int v=0;
        String day=pis.repeatsOn().get(0).toString();

        switch (day)
        {
            case "MONDAY":
                v=0;
                break;
            case "TUESDAY":
                v=1;
                break;
            case "WEDNESDAY":
                v=2;
                break;
            case "THURSDAY":
                v=3;
                break;
            case "FRIDAY":
                v=4;
                break;
            case "SATURDAY":
                v=5;
                break;
            case "SUNDAY":
                v=6;
                break;
        }
        daySpinner.setSelection(v);



        SimpleDateFormat d1=new SimpleDateFormat("HH:mm");

        Date startTime=pis.getStartTime();
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

        startTimePicker.setHour(startHour);
        startTimePicker.setMinute(startMin);


        Date endTime=pis.getEndTime();
        s = d1.format(endTime);
        try {
            endTime=d1.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startTimeString=new SimpleDateFormat("HH:mm").format(endTime);
        parts=startTimeString.split(":");


        int endHour= Integer.parseInt(parts[0]);
        int endMin=Integer.parseInt(parts[1].toString());

        endTimePicker.setHour(endHour);
        endTimePicker.setMinute(endMin);

        updateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=pisName.getText().toString();
                if(name.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Name is empty",Toast.LENGTH_LONG).show();
                    return;
                }
                String days=daySpinner.getSelectedItem().toString();
                Repeat_Enum e=null;

                switch (days)
                {
                    case "MONDAY":
                        e=Repeat_Enum.MONDAY;
                        break;
                    case "TUESDAY":
                        e=Repeat_Enum.TUESDAY;
                        break;
                    case "WEDNESDAY":
                        e=Repeat_Enum.WEDNESDAY;
                        break;
                    case "THURSDAY":
                        e=Repeat_Enum.THURSDAY;
                        break;
                    case "FRIDAY":
                        e=Repeat_Enum.FRIDAY;
                        break;
                    case "SATURDAY":
                        e=Repeat_Enum.SATURDAY;
                        break;
                    case "SUNDAY":
                        e=Repeat_Enum.SUNDAY;
                        break;
                }
                int startH = startTimePicker.getHour();
                int startM = startTimePicker.getMinute();
                int endH = endTimePicker.getHour();
                int endM = endTimePicker.getMinute();
                Date startT = new Date() ;
                Date endT = new Date();



                    SimpleDateFormat d1 = new SimpleDateFormat("HH:mm");
                    try {
                        startT = d1.parse(startH+":"+startM);
                        endT = d1.parse(endH+":"+endM);
                        Log.e("TImeActivationPIS","ADDED PROFILE TO START TIME: "+ startT.toString() + " END TIME: " + endT.toString());
                    } catch (ParseException qp) {
                        qp.printStackTrace();
                    }

                if(endH<startH) //if end of night
                {

                    Toast.makeText(getApplicationContext(), "Exceeding the ten hour limit", Toast.LENGTH_LONG).show();
                    return;

                }
                if(endH==startH && endM<startM)
                {
                    Toast.makeText(getApplicationContext(),"Exceeding the ten hour limit",Toast.LENGTH_LONG).show();
                    return;
                }



                int timeLimit=(endH-startH)*60;
                timeLimit+=(endM-startM);


                if(timeLimit>600 || timeLimit<10)
                {
                    Toast.makeText(getApplicationContext(),"Exceeding the 10 minute-10 hour limit",Toast.LENGTH_LONG).show();
                    return;
                }


                ArrayList<Repeat_Enum> re = new ArrayList<>();
                  re.add(e);
                ProfileInSchedule NPis=new ProfileInSchedule(pis.getProfile(),startT,endT,re);

   //             String scheduleName=getIntent().getStringExtra("scheduleName").toString();

//                MainActivity.db.removeProfileFromSchedule(pis,scheduleName,pis.repeatsOn().get(0));
//                MainActivity.db.addProfileInSchedule(NPis,scheduleName);

                Bundle bundle=new Bundle();  //sending old and new PIS to edit scehdule
                bundle.putSerializable(EditScheduleActivity.editPISOld,pis);
                bundle.putSerializable(EditScheduleActivity.editPISNew,NPis);

                Intent i=new Intent();
                i.putExtras(bundle);
                setResult(Activity.RESULT_OK,i);
                finish();
            }



        });


        discardChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
