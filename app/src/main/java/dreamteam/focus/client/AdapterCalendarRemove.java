package dreamteam.focus.client;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/14/17.
 */

public class AdapterCalendarRemove extends ArrayAdapter<ProfileInSchedule> {

    public DatabaseConnector db;


    public AdapterCalendarRemove(Context context, ArrayList<ProfileInSchedule> profilesArray)
    {
        super(context,0, profilesArray);
    }

    @Nullable
    @NonNull
    @Override

    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        final ProfileInSchedule s = getItem(position);
        Log.d("LOOK", s.repeatsOn().toString());
        db = new DatabaseConnector(getContext());

        View    conView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_profiledeleteitem, parent, false);


        TextView textProfileTime=(TextView)conView.findViewById(R.id.textViewScheduleTimeofProfile);
        TextView textProfileName=(TextView)conView.findViewById(R.id.textViewProfileScheduleName);
        Button appStatus =(Button) conView.findViewById(R.id.toggleScheduleProfileStatus);


        SimpleDateFormat d1 = new SimpleDateFormat("HH:mm");

        textProfileTime.setText((d1.format(s.getStartTime()).toString()) + " " +
                (d1.format(s.getEndTime()).toString()));
        textProfileName.setText(s.getProfile().getName());

        appStatus.setText("Remove");

        if(((EditSchedule)parent.getContext()).getState()) //if an active state of schedule
        {
                if(getDay(s.repeatsOn().get(0)) )
                {
                   if( getTime(s.getStartTime(),s.getEndTime()))
                   {
                       textProfileTime.setBackgroundColor(Color.GREEN);
                   }
                    else {
                       textProfileTime.setTextColor(Color.WHITE);
                       textProfileTime.setBackgroundColor(Color.RED);
                   }
                }
                else
                {
                    textProfileTime.setTextColor(Color.WHITE);
                    textProfileTime.setBackgroundColor(Color.RED);
                }
        }
        else
        {
            textProfileTime.setTextColor(Color.WHITE);
            textProfileTime.setBackgroundColor(Color.RED);
        }

        appStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.e("error","REMOVE IS CLICKED");

                EditSchedule.pisArray.add(s);
                EditSchedule.positionArray.add(position);
                EditSchedule.profileArray.remove(s);
                Log.d("TAG",s.repeatsOn().toString());
                EditSchedule.profileInScheduleArray.remove(s);//Changed
                ((EditSchedule)parent.getContext()).updateList();
            //    db.removeProfileFromSchedule(s,EditSchedule.scheduleName, s.repeatsOn().get(pos`ition));
//                Log.e("error",String.valueOf(s.repeatsOn().size()));

            }
        });
        return conView;
    }




    public boolean getTime(Date startTime, Date endTime) //check if currentTIme is in between start and end time
    {
        boolean condition=false;
        Date CurrentTime= Calendar.getInstance().getTime();
        SimpleDateFormat d1=new SimpleDateFormat("HH:mm");
        String s = d1.format(CurrentTime);
        try {
            startTime=d1.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String startTimeString=new SimpleDateFormat("HH:mm").format(CurrentTime);
        String[] parts=startTimeString.split(":");


        int currentHour= Integer.parseInt(parts[0]);

        int currentMin=Integer.parseInt(parts[1].toString());

        s = d1.format(startTime);
        try {
            startTime=d1.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startTimeString=new SimpleDateFormat("HH:mm").format(startTime);
        parts=startTimeString.split(":");

        int startHour= Integer.parseInt(parts[0]);
        int startMin=Integer.parseInt(parts[1]);

        s = d1.format(endTime);
        try {
            endTime=d1.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startTimeString=new SimpleDateFormat("HH:mm").format(endTime);
        parts=startTimeString.split(":");

        int endHour= Integer.parseInt(parts[0]);
        int endMin=Integer.parseInt(parts[1]);

        if(startHour<=currentHour && currentHour<=endHour)
        {

            if(startMin<=currentMin && currentMin<=endMin && endHour==startHour && currentMin>=startMin)
            {
                condition=true;
            }

            if(startHour==currentHour && currentMin>=startMin)
                condition=true;

            if(endHour==currentHour && currentMin<=endMin)
                condition=true;

            if((endHour-startHour)>1)
                condition=true;


        }

        Log.e("TimeActivation",startHour+":"+startMin+" - "+currentHour+":"+currentMin+" - "+endHour+":"+endMin+"-"+condition);

        return condition;

    }

    public boolean getDay(Repeat_Enum re) //check if today,s day is equal to PIS day
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String daysArray[] = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        Log.d("DayActivation",daysArray[day]+" "+re.toString());
        if(daysArray[day].equals(re.toString()))
        {
            Log.d("ReturnValue","True");
            return true;
        }
        Log.d("ReturnValue","false");
        return false;
    }
}