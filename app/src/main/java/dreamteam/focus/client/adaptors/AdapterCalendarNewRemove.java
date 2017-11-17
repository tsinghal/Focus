package dreamteam.focus.client.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.R;
import dreamteam.focus.client.schedules.AddScheduleActivity;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/14/17.
 */

public class AdapterCalendarNewRemove extends ArrayAdapter<ProfileInSchedule> {

    public DatabaseConnector db;


    public AdapterCalendarNewRemove(Context context, ArrayList<ProfileInSchedule> profilesArray) {
        super(context, 0, profilesArray);
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ProfileInSchedule s = getItem(position);
        Log.v("AdaptCalendarNewRemove", s.repeatsOn().toString());
        db = new DatabaseConnector(getContext());


        View conView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_profiledeleteitem, parent, false);


        TextView textProfileTime = (TextView) conView.findViewById(R.id.textViewScheduleTimeofProfile);
        TextView textProfileName = (TextView) conView.findViewById(R.id.textViewProfileScheduleName);
        Button appStatus = (Button) conView.findViewById(R.id.toggleScheduleProfileStatus);


        SimpleDateFormat d1 = new SimpleDateFormat("HH:mm");

        textProfileTime.setText((d1.format(s.getStartTime()).toString()) + " " +
                (d1.format(s.getEndTime()).toString()));
        textProfileName.setText(s.getProfile().getName());

        appStatus.setText("Remove");

        appStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("error", "REMOVE IS CLICKED");

                AddScheduleActivity.pisArray.add(s);
                AddScheduleActivity.positionArray.add(position);

                AddScheduleActivity.profileArray.remove(s);
                AddScheduleActivity.profileInScheduleArray.remove(s);
                ((AddScheduleActivity) parent.getContext()).updateList();
                //    db.removeProfileFromSchedule(s,EditScheduleActivity.scheduleName, s.repeatsOn().get(position));
//                Log.e("error",String.valueOf(s.repeatsOn().size()));

            }
        });
        return conView;
    }
}