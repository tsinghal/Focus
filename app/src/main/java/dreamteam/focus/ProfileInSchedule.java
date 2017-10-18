package dreamteam.focus;

        import java.io.Serializable;
        import java.util.ArrayList;
        import java.util.Date;

/**
 * Created by bowie on 10/2/17.
 */

public class ProfileInSchedule implements Serializable {
    private Profile profile;
    private Date startTime;
    private Date endTime;
    private ArrayList<Repeat_Enum> repeats;


    public ProfileInSchedule(Profile profile, Date startTime, Date endTime, ArrayList<Repeat_Enum> repeats) {
        this.profile = profile;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeats = repeats;
    }

    public ProfileInSchedule(Profile profile, Date startTime, Date endTime) {
        this.profile = profile;
        this.startTime = startTime;
        this.endTime = endTime;
        repeats = new ArrayList<>();
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Repeat_Enum> repeatsOn() {
        return repeats;
    }

    /**
     * Checks if a day is part of this repeat configuration
     *
     * @param day: a day in Repeat_Enum
     * @return true if Repeat_Enum is in ProfileInSchdeule.repeats
     * @see Repeat_Enum
     */
    public boolean repeatsOn(Repeat_Enum day) {
        for (Repeat_Enum repeat : repeats) {
            if (repeat.equals(day)) {
                return true;
            }
        }
        return false;
    }
}
