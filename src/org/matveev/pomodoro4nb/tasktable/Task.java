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
        Critical,
        Major,
        Minor,
    }

    private String description;
    private int estimate;
    private int pomodoros;
    private int interaptions;
    private int unplaned;
    private final Date creationDate;
    private Tag tag;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
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
                + ", tag=" + tag
                + ", completed=" + completed + '}';
    }
}
