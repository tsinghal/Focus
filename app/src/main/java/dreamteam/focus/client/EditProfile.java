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
import dreamteam.focus.client.adapter.AdapterAppsEdit;

public class EditProfile extends AppCompatActivity {


    public static ArrayList<String> appsOnDevice;
    public static ArrayList<String> packagesOnDevice;

    AdapterAppsEdit appsList;
    Button submit;
    Button discard;
    String profileName;
    public static String IntentNameString="ProfileName";

    public static ArrayList<String> blockedApps;
    public static ArrayList<String> blockedPackages;
    public  ArrayList<Boolean> statusApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profileName=getIntent().getStringExtra(IntentNameString);

        ((EditText)findViewById(R.id.editViewEditProfileName)).setText(profileName);


        //get blockedApps
        blockedPackages=MainActivity.db.getBlockedApps(profileName);


        Toast.makeText(getApplicationContext(),blockedPackages.size()+" ",Toast.LENGTH_LONG).show();
        //change this

        appsOnDevice=new ArrayList<String>();
        packagesOnDevice=new ArrayList<String>();

        getSystemApps();

//       statusApps=new ArrayList<Boolean>();
//        for (int i=0;i<packagesOnDevice.size();i++)
//        {
//            if(blockedPackages.contains(packagesOnDevice.get(i)))
//            {
//
//                statusApps.add(true);
//                Log.d("Rat",i+" "+appsOnDevice.get(i)+" "+statusApps.get(i));
//            }
//            else
//            {
//
//                statusApps.add(false);
//                Log.d("Rats",i+" "+appsOnDevice.get(i)+" "+statusApps.get(i));
//            }
//        }

        appsList=new AdapterAppsEdit(getApplicationContext(),appsOnDevice);

        ListView listViewApps=(ListView)findViewById(R.id.listViewEditApps);
        listViewApps.setAdapter(appsList);

        submit=(Button)findViewById(R.id.buttonSaveProfile);
        discard=(Button)findViewById(R.id.buttonDiscardChanges);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileName=((EditText)findViewById(R.id.editViewEditProfileName)).getText().toString();
                Profile p=new Profile(profileName,blockedPackages); //change it from null
                MainActivity.db.updateProfile(getIntent().getStringExtra(IntentNameString),p);

                Toast.makeText(getApplicationContext(),"Profile updated successfully",Toast.LENGTH_SHORT);
                finish();
            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.db.removeProfile(getIntent().getStringExtra(IntentNameString));
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

