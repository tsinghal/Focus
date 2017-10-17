package dreamteam.focus.client.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.client.AddProfileToNewSchedule;
import dreamteam.focus.server.DatabaseConnector;

/**
 * Created by aarav on 10/14/17.
 */

public class AdapterAddProfileToNewSchedule extends ArrayAdapter<Profile> {

    private CheckBox appStatus;
    public Context context;
    private boolean check=false;
    private DatabaseConnector db;

    public AdapterAddProfileToNewSchedule(Context context, ArrayList<Profile> profilesArray)
    {
        super(context,0, profilesArray);
        this.context=context;
        db = new DatabaseConnector(getContext());
    }

    @Nullable
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Profile s = getItem(position);

        View    conView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_addprofile_item , parent, false);

        final TextView textAppName=(TextView)conView.findViewById(R.id.textViewProfileName);
        appStatus =(CheckBox) conView.findViewById(R.id.checkBoxProfile2);


        textAppName.setText(s.getName());

        appStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    Log.e("error","iIS CHECKED!!!!!");
                    AddProfileToNewSchedule.profilesToBeBlocked.add(s);
                }
                else
                {
                    if(AddProfileToNewSchedule.profilesToBeBlocked.contains(s)){
                        AddProfileToNewSchedule.profilesToBeBlocked.remove(s);
                    }
                }
            }
        });

        return conView;
    }
}