package dreamteam.focus.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SnapHelper;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import dreamteam.focus.R;

/**
 * Created by shatrujeet lawa on 10/8/2017.
 */

public class CreateProfile extends AppCompatActivity {



    ArrayList<String> appsOnDevice;
    AdapterApps appsList;

    private Button addNewProfile;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);


        appsOnDevice=new ArrayList<String>();
        appsOnDevice.add("Facebook");
        appsOnDevice.add("SnapChat");

        appsList=new AdapterApps(getApplicationContext(),appsOnDevice);

        ListView listViewApps=(ListView)findViewById(R.id.listViewApps);
        listViewApps.setAdapter(appsList);



    }
}