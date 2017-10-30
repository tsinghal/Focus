package dreamteam.focus.client.Adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.client.Schedules.EditProfileInScheduleActivity;
import dreamteam.focus.client.Schedules.EditScheduleActivity;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/14/17.
 */

public class AdapterCalendarRemove extends ArrayAdapter<ProfileInSchedule> {

    public DatabaseConnector db;
    public Context mcontext;
    public ProfileInSchedule s;
    public int hoursLeft,minutesLeft,secondsLeft;

    public AdapterCalendarRemove(Context context, ArrayList<ProfileInSchedule> profilesArray)
    {
        super(context,0, profilesArray);
        hoursLeft=0;
        minutesLeft=0;
        secondsLeft=0;
        mcontext=context;
    }

    @Nullable
    @NonNull
    @Override

    public View getView(final int position, View convertView, final ViewGroup parent)
    {
         s = getItem(position);

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

        if(((EditScheduleActivity)parent.getContext()).getState()) //if an active state of schedule
        {
                if(getDay(s.repeatsOn().get(0)) )
                {
                   if( getTime(s.getStartTime(),s.getEndTime()))
                   {
                       textProfileTime.setBackgroundColor(Color.GREEN);
//                       secondsLeft=getSecondsLeft();
                       textProfileTime.setText(hoursLeft+":"+minutesLeft+":"+secondsLeft);
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

        textProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mcontext,EditProfileInScheduleActivity.class);
                Bundle b=new Bundle();
                s=getItem(position);
                b.putSerializable(EditProfileInScheduleActivity.namePIS,s);
                i.putExtras(b);
                i.putExtra("scheduleName",((EditScheduleActivity)(parent.getContext())).scheduleName);
                ((Activity)parent.getContext()).startActivityForResult(i,1);
            }
        });

        appStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.e("error","REMOVE IS CLICKED");
                s=getItem(position);
                EditScheduleActivity.pisArray.add(s);
                EditScheduleActivity.positionArray.add(position);
                EditScheduleActivity.profileArray.remove(s);
                Log.d("TAG",s.repeatsOn().toString());
                EditScheduleActivity.profileInScheduleArray.remove(s);//Changed
                ((EditScheduleActivity)parent.getContext()).updateList();
            //    db.removeProfileFromSchedule(s,EditScheduleActivity.scheduleName, s.repeatsOn().get(pos`ition));
//                Log.e("error",String.valueOf(s.repeatsOn().size()));

            }
        });
        return conView;
    }




    public boolean getTime(Date startTime, Date endTime) //check if currentTIme is in between start and end time
    {
        boolean condition=false;
        Date CurrentTime= Calendar.getInstance().getTime();
        SimpleDateFormat d1=new SimpleDateFormat("HH:mm:ss");

        String s = d1.format(CurrentTime);
        try {
            CurrentTime=d1.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String startTimeString=new SimpleDateFormat("HH:mm:ss").format(CurrentTime);
        String[] parts=startTimeString.split(":");


        int currentHour= Integer.parseInt(parts[0]);

        int currentMin=Integer.parseInt(parts[1].toString());

        secondsLeft=59-Integer.parseInt(parts[2].toString());

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

            if( currentMin<endMin && endHour==startHour && currentMin>=startMin)
            {

                condition=true;
            }

            if(startHour==currentHour && currentMin>=startMin)
            {
               condition=true;

               if(currentHour==endHour && currentMin>=endMin)
                condition=false;
            }

            if(endHour==currentHour && currentMin<endMin)
            {
                condition=true;

                if(currentHour==startHour && currentMin<startMin)
                condition=false;
            }

            if((endHour-startHour)>1)
            {

                condition=true;
            }


        }

        Log.e("TimeActivation",startHour+":"+startMin+" - "+currentHour+":"+currentMin+" - "+endHour+":"+endMin+"-"+condition);



        if(condition)
        {
            hoursLeft=endHour-currentHour;
            if(currentMin>endMin)
            {
                hoursLeft--;
                minutesLeft=(60-currentMin)+endMin;
            }
            else
                minutesLeft=endMin-currentMin;

        }
        return condition;

    }

    public boolean getDay(Repeat_Enum re) //check if today,s day is equal to PIS day
    {

        String today = (String) DateFormat.format("EEEE", new Date() );
        today = today.toUpperCase();

        if(today.equals(re.toString()))
        {

            return true;
        }

        return false;
    }

//    public int getSecondsLeft()
//    {
//        Date CurrentTime= Calendar.getInstance().getTime();
//        SimpleDateFormat d1=new SimpleDateFormat("HH:mm:ss");
//
//        String s = d1.format(CurrentTime);
//        try {
//            CurrentTime=d1.parse(s);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        String startTimeString=new SimpleDateFormat("HH:mm:ss").format(CurrentTime);
//        String[] parts=startTimeString.split(":");
//
//        return Integer.parseInt(parts[2].toString());
//
//
//
//    }

}


