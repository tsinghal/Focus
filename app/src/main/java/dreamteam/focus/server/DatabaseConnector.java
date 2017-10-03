package dreamteam.focus.server;

/**
 * Created by bowie on 10/2/17.
 */

public class DatabaseConnector {
    public bool createProfile(Profile profile) {}

    public bool removeProfile(Profile profile) {}

    public bool updateProfile(Profile profile) {}

    public bool activateProfile(Profile profile) {}

    public bool deactivateProfile(Profile profile) {}

    public HashMap<String, Integer> getNotificationsForProfile(Profile profile) {}

    public bool addSchedule(Schedule schedule) {}

    public bool removeSchedule(Schedule schedule) {}

    public bool updateSchedule(Schedule schedule) {}

    public bool addProfileToSchedule(ProfileInSchedule pis, Schedule schedule) {}

    public bool removeProfileToSchedule(ProfileInSchedule pis, Schedule schedule) {}

    public bool activateSchedule(Schedule schedule) {}

    public bool deactivateSchedule(Schedule schedule) {}

    public HashMap<String, Integer> getNotificationsForSchedule(Schedule schedule) {}

    public Array<Profile> getProfiles() {}

    public Array<Profile> getSchedules() {}
}
