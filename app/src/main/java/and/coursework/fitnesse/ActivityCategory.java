package and.coursework.fitnesse;

public class ActivityCategory {
    private String name;
    private int frequency;
    private int averageMinutes;
    private int averageEffortLevel;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getAverageMinutes() {
        return averageMinutes;
    }

    public void setAverageMinutes(int averageMinutes) {
        this.averageMinutes = averageMinutes;
    }

    public int getAverageEffortLevel() {
        return averageEffortLevel;
    }

    public void setAverageEffortLevel(int averageEffortLevel) {
        this.averageEffortLevel = averageEffortLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
