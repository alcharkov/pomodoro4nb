package org.matveev.pomodoro4nb.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alexey Matveev
 */
public class Task implements Serializable {

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

    public void setInteraptions(int interaptions) {
        this.interaptions = interaptions;
    }

    public int getPomodoros() {
        return pomodoros;
    }

    public void setPomodoros(int pomodoros) {
        this.pomodoros = pomodoros;
    }


    public int getUnplaned() {
        return unplaned;
    }

    public void setUnplaned(int unplaned) {
        this.unplaned = unplaned;
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
