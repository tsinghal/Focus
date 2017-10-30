package dreamteam.focus.client.adaptors;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import dreamteam.focus.R;
import dreamteam.focus.Schedule;
import dreamteam.focus.client.schedules.EditScheduleActivity;
import dreamteam.focus.client.schedules.SchedulesActivity;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/13/17.
 */


public class AdapterSchedules extends ArrayAdapter<Schedule> {


    public Context context;
    private DatabaseConnector db;

    public AdapterSchedules(Context context, ArrayList<Schedule> schedulesArray) {
        super(context, 0, schedulesArray);
        this.context = context;
    }

    ToggleButton appStatus;

    @Nullable
    @NonNull
    @Override


    public View getView(int position, View convertView, final ViewGroup parent) {
        final Schedule s = getItem(position);


        View conView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_listitem, parent, false);

        TextView textAppName = (TextView) conView.findViewById(R.id.textViewScheduleName);
        appStatus = (ToggleButton) conView.findViewById(R.id.toggleScheduleStatus);


        textAppName.setText(s.getName());
        appStatus.setChecked(s.isActive());

        //change it i.putExtra()

        appStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    s.setActive(true);
                    SchedulesActivity.db.activateSchedule(s.getName());
                    ((SchedulesActivity) parent.getContext()).updateList();
                } else {
                    //Off
                    s.setActive(false);
                    SchedulesActivity.db.deactivateSchedule(s.getName());
                    ((SchedulesActivity) parent.getContext()).updateList();
                }
            }
        });

        textAppName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent y = new Intent(context.getApplicationContext(), EditScheduleActivity.class);
                Log.e("error", "intent created");
                y.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                y.putExtra("Schedule Name", s.getName().toString());
                Log.e("error", "intent created with extra");
                v.getContext().startActivity(y);
                Log.e("error", "Activity statrted created");
            }
        });


        return conView;
    }
}
