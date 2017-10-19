package dreamteam.focus.client;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.client.adapter.AdapterApps;

/**
 * Created by shatrujeet lawa on 10/8/2017.
 */

public class CreateProfile extends AppCompatActivity {
    public static ArrayList<String> appsOnDevice;
    public static ArrayList<String> packagesOnDevice;

    AdapterApps appsList;
    Button submit;
    Button discard;
    String profileName;

    public static ArrayList<String> blockedPackages;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);
        blockedPackages=new ArrayList<String>();

        profileName=((EditText)findViewById(R.id.editViewProfileName)).getText().toString();

        //change this
        appsOnDevice=new ArrayList<String>();
        packagesOnDevice=new ArrayList<String>();

        getSystemApps();



        appsList=new AdapterApps(getApplicationContext(),appsOnDevice);

        ListView listViewApps=(ListView)findViewById(R.id.listViewApps);
        listViewApps.setAdapter(appsList);

        submit=(Button)findViewById(R.id.buttonCreateProfile);
        discard=(Button)findViewById(R.id.buttonDiscardProfile);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileName=((EditText)findViewById(R.id.editViewProfileName)).getText().toString();

                if(!profileName.isEmpty())
                {
                    Profile p = new Profile(profileName, blockedPackages);

                    try {
                        MainActivity.db.createProfile(p);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Choose unique name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Profile successfully created", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getSystemApps()
    {
        PackageManager pm=getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            String appName=getAppNameFromPackage(packageInfo.packageName);

           if(!appName.equals("this app") && !appName.contains(".") && !packageInfo.packageName.equals("com.htc.launcher") && !packageInfo.packageName.equals("dreamteam.focus") && !packageInfo.packageName.equals("com.google.android.apps.nexuslauncher") && !packageInfo.packageName.equals("com.android.systemui")&& !packageInfo.packageName.equals("com.google.android.packageinstaller")  )
            {
                appsOnDevice.add(appName);
                packagesOnDevice.add(packageInfo.packageName);

            }
        }

    }

    public String getAppNameFromPackage(String packageName) {
        PackageManager manager = getApplicationContext().getPackageManager();
        ApplicationInfo info;
        try {
            info = manager.getApplicationInfo(packageName, 0);
        } catch (final Exception e) {
            info = null;
        }
        return (String) (info != null ? manager.getApplicationLabel(info) : "this app");
    }
}