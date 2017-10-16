package dreamteam.focus.client;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/14/17.
 */

public class AdapterCalendarNewRemove extends ArrayAdapter<ProfileInSchedule> {

    public DatabaseConnector db;


    public AdapterCalendarNewRemove(Context context, ArrayList<ProfileInSchedule> profilesArray)
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

        appStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.e("error","REMOVE IS CLICKED");

                CreateSchedule.pisArray.add(s);
                CreateSchedule.positionArray.add(position);

                CreateSchedule.profileArray.remove(s);
                CreateSchedule.profileInScheduleArray.remove(s);
                ((CreateSchedule)parent.getContext()).updateList();
                //    db.removeProfileFromSchedule(s,EditSchedule.scheduleName, s.repeatsOn().get(position));
//                Log.e("error",String.valueOf(s.repeatsOn().size()));

            }
        });
        return conView;
    }
}