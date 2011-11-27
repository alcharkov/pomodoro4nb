package org.matveev.pomodoro4nb.timer;

import org.matveev.pomodoro4nb.timer.PomodoroTimer.State;

/**
 *
 * @author Alexey Matveev
 */
/*package*/ interface PomodoroTimerListener {

    public void stateChanged(State state, boolean forced);
    public void preStart();
    
}
