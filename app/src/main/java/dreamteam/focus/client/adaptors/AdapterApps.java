package dreamteam.focus.client.adaptors;

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

import dreamteam.focus.R;
import dreamteam.focus.client.profiles.CreateProfileActivity;


public class AdapterApps extends ArrayAdapter<String> {


    public AdapterApps(Context context, ArrayList<String> appArray) {
        super(context, 0, appArray);
    }

    @Nullable
    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final String appName = getItem(position);
        int index = CreateProfileActivity.appsOnDevice.indexOf(appName);
        final String packageName = CreateProfileActivity.packagesOnDevice.get(index);
        final TextView textAppName;
        CheckBox appStatus;

        View conView = LayoutInflater.from(getContext()).inflate(R.layout.app_listitem, parent, false);

        textAppName = (TextView) conView.findViewById(R.id.textViewAppName);
        textAppName.setText(appName);

        appStatus = (CheckBox) conView.findViewById(R.id.checkBoxAppStatus);

        if (CreateProfileActivity.blockedPackages.contains(packageName)) {
            Log.e("BlockedPackage", packageName + " " + appName + " " + position);
            appStatus.setChecked(true);
        }

        appStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (CreateProfileActivity.blockedPackages.contains(appName)) {
                    if (!isChecked) {
                        CreateProfileActivity.blockedPackages.remove(packageName);

                    }
                } else {
                    if (isChecked) {
                        CreateProfileActivity.blockedPackages.add(packageName);
                    }
                }
            }
        });

        return conView;
    }
}
