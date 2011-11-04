package org.matveev.pomodoro4nb.timer;

import org.matveev.pomodoro4nb.timer.PomodoroTimer.State;

/**
 *
 * @author Alexey Matveev
 */
public interface PomodoroTimerListener {

    public void stateChanged(State state, boolean forced);
    
}
