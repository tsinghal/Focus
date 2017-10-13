package dreamteam.focus.server;

import java.lang.reflect.Array;
import java.util.HashMap;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.Schedule;

/**
 * Created by bowie on 10/2/17.
 */

public class DatabaseConnector {
    public boolean createProfile(Profile profile) {return false;}

    public boolean removeProfile(Profile profile) {return false;}

    public boolean updateProfile(Profile profile) {return false;}

    public boolean activateProfile(Profile profile) {return false;}

    public boolean deactivateProfile(Profile profile) {return false;}

    public boolean getNotificationsForProfile(Profile profile) {return false;}

    public boolean addSchedule(Schedule schedule) {return false;}

    public boolean removeSchedule(Schedule schedule) {return false;}

    public boolean updateSchedule(Schedule schedule) {return false;}

    public boolean addProfileToSchedule(ProfileInSchedule pis, Schedule schedule) {return false;}

    public boolean removeProfileToSchedule(ProfileInSchedule pis, Schedule schedule) {return false;}

    public boolean activateSchedule(Schedule schedule) {return false;}

    public boolean deactivateSchedule(Schedule schedule) {return false;}

    public boolean getNotificationsForSchedule(Schedule schedule) {return false;}

    public boolean getProfiles() {return false;}

    public boolean getSchedules() {return false;}
}
