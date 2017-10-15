package dreamteam.focus.client;

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
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;



public class AdapterProfiles extends ArrayAdapter<Profile> {


    public AdapterProfiles(Context context,ArrayList<Profile> profilesArray)
    {
        super(context,0, profilesArray);
    }

    @Nullable
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Profile p= getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_listitem , parent, false);
        }
        final TextView textAppName=(TextView)convertView.findViewById(R.id.textViewProfileName);
        final ToggleButton appStatus =(ToggleButton)convertView.findViewById(R.id.toggleProfileStatus);


        textAppName.setText(p.getName());
        appStatus.setChecked(p.isActive());

        final Intent i =new Intent(convertView.getContext(),EditProfile.class);
        //change it i.putExtra()
        i.putExtra(EditProfile.IntentNameString,p.getName() );

        appStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
              if (appStatus.isChecked())
              {

              }
              else
              {

              }
            }
        }
        );

        textAppName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(i);
            }
        });

        return convertView;
    }
}
