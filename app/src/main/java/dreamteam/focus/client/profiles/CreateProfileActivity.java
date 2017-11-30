package dreamteam.focus.client.profiles;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import dreamteam.focus.Profile;
import dreamteam.focus.R;
import dreamteam.focus.client.ArrangeAppsByName;
import dreamteam.focus.client.MainActivity;
import dreamteam.focus.client.adaptors.AdapterApps;

/**
 * Created by shatrujeet lawa on 10/8/2017.
 */

public class CreateProfileActivity extends AppCompatActivity {
    public static ArrayList<String> appsOnDevice;
    public static ArrayList<String> packagesOnDevice;
    public static TreeMap<String, String> treemap;
    public static HashMap<String, String> map;
    public static ArrayList<String> blockedPackages;

    AdapterApps appsList;
    Button submit;
    Button discard;
    String profileName;

    private SearchView searchView;
    private MenuItem searchMenuItem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);
        blockedPackages = new ArrayList<String>();

        profileName = ((EditText) findViewById(R.id.editViewProfileName)).getText().toString();

        //change this
        appsOnDevice = new ArrayList<String>();
        packagesOnDevice = new ArrayList<String>();
        treemap = new TreeMap<String, String>();
        map = new HashMap<String, String>();

        getSystemApps();

        appsList = new AdapterApps(getApplicationContext(), appsOnDevice);

        ListView listViewApps = (ListView) findViewById(R.id.listViewApps);
        listViewApps.setAdapter(appsList);

        submit = (Button) findViewById(R.id.buttonCreateProfile);
        discard = (Button) findViewById(R.id.buttonDiscardProfile);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileName = ((EditText) findViewById(R.id.editViewProfileName)).getText().toString();

                if (!profileName.isEmpty()) {
                    Profile p = new Profile(profileName, blockedPackages);

                    try {
                        MainActivity.db.createProfile(p);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Choose unique name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Profile successfully created", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Name is empty", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                appsList.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }


    public void getSystemApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            String appName = getAppNameFromPackage(packageInfo.packageName);

            if (!appName.equals("this app") && !appName.contains(".") && !packageInfo.packageName.equals("com.htc.launcher") && !packageInfo.packageName.equals("dreamteam.focus") && !packageInfo.packageName.equals("com.google.android.apps.nexuslauncher") && !packageInfo.packageName.equals("com.android.systemui") && !packageInfo.packageName.equals("com.google.android.packageinstaller")) {

                map.put(packageInfo.packageName, appName);
            }
        }
        ArrangeAppsByName arrange = new ArrangeAppsByName();
        TreeMap<String, String> sortedMap = arrange.sortMapByValue(map);
        for (String key : sortedMap.keySet()) {
            packagesOnDevice.add(key);
            appsOnDevice.add(sortedMap.get(key));
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