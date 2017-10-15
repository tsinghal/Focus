package dreamteam.focus.client;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;

/**
 * Created by shatrujeet lawa on 10/13/2017.
 */

public class AdapterAppsEdit extends ArrayAdapter<String> {

    public AdapterAppsEdit(Context context, ArrayList<String> appArray) {
        super(context, 0, appArray);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String appName = getItem(position);
        int index=EditProfile.appsOnDevice.indexOf(appName);
        final String packageName=EditProfile.packagesOnDevice.get(index);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_listitem, parent, false);
        }

        final TextView textAppName = (TextView) convertView.findViewById(R.id.textViewAppName);
        CheckBox appStatus = (CheckBox) convertView.findViewById(R.id.checkBoxAppStatus);


        textAppName.setText(appName);
        if (EditProfile.blockedPackages.contains(packageName))
        {
          Log.e("BlockedPackage",packageName+" "+appName+" "+position);
          appStatus.setChecked(true);
        }




        appStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Listener Reached","Yes");
                if (EditProfile.blockedPackages.contains(packageName)) {
                    if (!isChecked){
                        //EditProfile.blockedApps.remove(appName);
                        EditProfile.blockedPackages.remove(packageName);

                    }

                } else {
                    if (isChecked) {
                        //EditProfile.blockedApps.add(appName);
                        EditProfile.blockedPackages.add(packageName);
                    }
                }
            }
        });

        return convertView;
    }
}
