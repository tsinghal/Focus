package dreamteam.focus.client.adapter;

import android.content.Context;
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
import dreamteam.focus.client.EditProfile;

/**
 * Created by shatrujeet lawa on 10/13/2017.
 */

public class AdapterAppsEdit extends ArrayAdapter<String> {

    public ArrayList<Boolean> statusList;
    public AdapterAppsEdit(Context context, ArrayList<String> appArray){//ArrayList<Boolean> statusApps) {
        super(context, 0, appArray);

//        this.statusApps=statusApps;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String appName = getItem(position);
        int index = EditProfile.appsOnDevice.indexOf(appName);
        final String packageName=EditProfile.packagesOnDevice.get(index);
        final TextView textAppName;
        CheckBox appStatus;



        View    conView = LayoutInflater.from(getContext()).inflate(R.layout.app_listitem, parent, false);



        textAppName = (TextView) conView.findViewById(R.id.textViewAppName);
        textAppName.setText(appName);

        appStatus=(CheckBox)conView.findViewById(R.id.checkBoxAppStatus);



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
                        EditProfile.blockedPackages.remove(packageName);

                    }

                } else {
                    if (isChecked) {

                        EditProfile.blockedPackages.add(packageName);


                    }
                }


            }
        });

        return conView;
    }

    public void setStatusList(ArrayList<Boolean> list)
    {
        this.statusList=list;
        notifyDataSetChanged();
    }

    static class ViewHolder{
        CheckBox appStatus;
    }
}
