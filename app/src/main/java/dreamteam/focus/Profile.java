package dreamteam.focus;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bowie on 10/2/17.
 */

public class Profile {
    private String name;
    private ArrayList<String> blockedApps;
    private Date activateNowForTime;
    private boolean active;
    private ArrayList<String> blockedNotifications;

    public Profile(String name, ArrayList<String> blockedApps) {
        this.name = name;
        this.blockedApps = blockedApps;
        blockedNotifications = new ArrayList<>();
        active = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getApps() {
        return blockedApps;
    }

//    public void setBlockedApps(ArrayList<String> blockedApps) {
//        this.blockedApps = blockedApps;
//    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<String> getBlockedNotifications() {
        return blockedNotifications;
    }

    public void addBlockedNotifications(String notification) {
        this.blockedNotifications.add(notification);
    }
}
