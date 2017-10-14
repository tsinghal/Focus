package dreamteam.focus.client;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import dreamteam.focus.Profile;
import dreamteam.focus.R;



public class AdapterApps extends ArrayAdapter<String> {


    public AdapterApps(Context context,ArrayList<String> appArray)
    {
        super(context,0, appArray);
    }

    @Nullable
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        String p= getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_listitem , parent, false);
        }
        TextView textAppName=(TextView)convertView.findViewById(R.id.textViewAppName);
        CheckBox appStatus =(CheckBox) convertView.findViewById(R.id.checkBoxAppStatus);


        textAppName.setText(p);
        appStatus.setChecked(false); //checge it

        return convertView;
    }
}
