package and.coursework.fitnesse.objects;

public class Activity {
    private String activity;
    private String minutes;
    private String longitude;
    private String latitude;
    private String description;
    private String monthAdded;
    private String yearAdded;
    private String dayAdded;
    private int effortLevel;

    public String getDayAdded() {
        return dayAdded;
    }

    public void setDayAdded(String dayAdded) {
        this.dayAdded = dayAdded;
    }

    public String getMonthAdded() {
        return monthAdded;
    }

    public void setMonthAdded(String monthAdded) {
        this.monthAdded = monthAdded;
    }

    public String getYearAdded() {
        return yearAdded;
    }

    public void setYearAdded(String yearAdded) {
        this.yearAdded = yearAdded;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEffortLevel() {
        return effortLevel;
    }

    public void setEffortLevel(int effortLevel) {
        this.effortLevel = effortLevel;
    }
}
