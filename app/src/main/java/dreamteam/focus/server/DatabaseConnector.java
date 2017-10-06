package dreamteam.focus.server;

import java.util.ArrayList;
import java.util.HashMap;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.Schedule;

/**
 * Created by bowie on 10/2/17.
 */

public class DatabaseConnector {
    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean createProfile(Profile profile) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean removeProfile(Profile profile) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean updateProfile(Profile profile) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean activateProfile(Profile profile) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean deactivateProfile(Profile profile) {
        return false;
    }

    /**
     * @param profile
     * @return a map of application names and the number of missed notifications
     */
    public HashMap<String, Integer> getNotificationsForProfile(Profile profile) {
        return null;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean addSchedule(Schedule schedule) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean removeSchedule(Schedule schedule) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean updateSchedule(Schedule schedule) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean addProfileToSchedule(ProfileInSchedule pis, Schedule schedule) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean removeProfileToSchedule(ProfileInSchedule pis, Schedule schedule) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean activateSchedule(Schedule schedule) {
        return false;
    }

    /**
     * @param profile
     * @return true on success; false on fail
     */
    public Boolean deactivateSchedule(Schedule schedule) {
        return false;
    }

    /**
     * @param schedule
     * @return a map of application names and the number of missed notifications
     */
    public HashMap<String, Integer> getNotificationsForSchedule(Schedule schedule) {
        return null;
    }

    /**
     * @return an array of all the profiles present in the database
     */
    public ArrayList<Profile> getProfiles() {
        return null;
    }

    /**
     * @return an array of all the schedules present in the database
     */
    public ArrayList<Profile> getSchedules() {
        return null;
    }
}
