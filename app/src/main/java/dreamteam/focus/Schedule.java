package dreamteam.focus;

/**
 * Created by aarav on 10/13/17.
 */

public class Schedule {

    private String name;
    private boolean active;


    public Schedule(String name) {
        this.name = name;
        active = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
