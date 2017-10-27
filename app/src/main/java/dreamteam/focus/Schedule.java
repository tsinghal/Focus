package dreamteam.focus;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bowie on 10/2/17.
 */

public class Schedule implements Serializable {
    private String name;
    private ArrayList<ProfileInSchedule> calendar;
    private boolean active;

    public Schedule(String name, ArrayList<ProfileInSchedule> calendar, boolean active) {
        this.name = name;
        this.calendar = calendar;
        this.active = active;
    }

    public Schedule(String name, ArrayList<ProfileInSchedule> calendar) {
        this.name = name;
        this.calendar = calendar;
        this.active = false;
    }

    public Schedule(String name) {
        this.name = name;
        this.calendar = new ArrayList<>();
        this.active = false;
    }

    public ArrayList<ProfileInSchedule> getCalendar() {
        return calendar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addProfileToCalendar(ProfileInSchedule pis) {
        calendar.add(pis);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}