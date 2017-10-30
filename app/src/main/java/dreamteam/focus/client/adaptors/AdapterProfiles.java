package dreamteam.focus.client.adaptors;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.client.MainActivity;
import dreamteam.focus.client.TimePicker;
import dreamteam.focus.client.profiles.EditProfileActivity;
import dreamteam.focus.client.profiles.ProfilesActivity;


public class AdapterProfiles extends ArrayAdapter<Profile> {

    public Context mcontext;
    public AdapterProfiles adapter;

    public AdapterProfiles(Context context, ArrayList<Profile> profilesArray) {
        super(context, 0, profilesArray);
        this.mcontext = context;
        this.adapter = this;
    }

    @Nullable
    @NonNull
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final Profile p = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_listitem, parent, false);
        }
        final TextView textAppName = (TextView) convertView.findViewById(R.id.textViewProfileName);
        final ToggleButton appStatus = (ToggleButton) convertView.findViewById(R.id.toggleProfileStatus);

        textAppName.setText(p.getName());


        appStatus.setChecked(p.isActive());

        appStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                     if (appStatus.isChecked() && !p.isActive()) {
                                                         Intent i = new Intent(mcontext, TimePicker.class);
                                                         i.putExtra(TimePicker.IntentProfileName, p.getName());
                                                         buttonView.getContext().startActivity(i);

                                                     }

                                                     if (!appStatus.isChecked() && p.isActive()) {
                                                         MainActivity.db.deactivateProfile(p);
//                  if(mcontext instanceof ProfilesActivity){
//                      Log.d("TAG","IN");
//                      ((ProfilesActivity)mcontext).updateList();
//                  }
                                                         ((ProfilesActivity) parent.getContext()).updateList();
                                                     }
                                                 }
                                             }
        );

        textAppName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent i;
                i = new Intent(v.getContext(), EditProfileActivity.class);
                i.putExtra(EditProfileActivity.IntentNameString, p.getName());
                v.getContext().startActivity(i);
            }
        });

        return convertView;
    }
}
