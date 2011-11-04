package org.matveev.pomodoro4nb.tasktable;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alexey Matveev
 */
public class Task implements Serializable {

    public enum Tag {
        Improvements,
        Blocker,
        Critical,
        Major,
        Minor,
    }
    
    private final String description;
    private final int estimate;
    private int pomodoros;
    private int interaptions;
    private int unplaned;
    private final Date creationDate;
    private boolean completed;

    public Task(String description, int estimate) {
        this.description = description;
        this.estimate = estimate;
        creationDate = new Date();
    }

    public String getDescription() {
        return description;
    }

    public int getEstimate() {
        return estimate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getInteraptions() {
        return interaptions;
    }

    public void incrementInteraption() {
        this.interaptions += 1;
    }

    public int getPomodoros() {
        return pomodoros;
    }

    public void incrementPomodoros() {
        this.pomodoros += 1;
    }

    public int getUnplaned() {
        return unplaned;
    }

    public void incrementUnplaned() {
        this.unplaned += 1;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Task{"
                + "description=" + description
                + ", estimate=" + estimate
                + ", pomodoros=" + pomodoros
                + ", interaptions=" + interaptions
                + ", unplaned=" + unplaned
                + ", creationDate=" + creationDate
                + ", completed=" + completed + '}';
    }
}
