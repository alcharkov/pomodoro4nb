package org.matveev.pomodoro4nb.timer;

import java.awt.Color;

/**
 *
 * @author Alexey Matveev
 */
public class PomodoroTimerData {

    private static final Color DEFAULT_WORK_PROGRESS_BAR_COLOR = new Color(102, 213, 41);
    private static final Color DEFAULT_BREAK_PROGRESS_BAR_COLOR = new Color(183, 173, 237);

    private final long pomodoroLengthInMillis;
    private final long shortBreakLengthInMillis;
    private final long longBreakLengthInMillis;
    private final int longBreakInterval;

    public PomodoroTimerData(int pomodoroLength, int shortBreakLength,
            int longBreakLength, int longBreakInterval) {
        this.pomodoroLengthInMillis = toMillis(pomodoroLength);
        this.shortBreakLengthInMillis = toMillis(shortBreakLength);
        this.longBreakLengthInMillis = toMillis(longBreakLength);
        this.longBreakInterval = longBreakInterval;
    }
    
    private static long toMillis(int timeInMinutes) {
        return timeInMinutes * 60 * 1000;
    }

    public int getLongBreakInterval() {
        return longBreakInterval;
    }

    public long getLongBreakLengthInMillis() {
        return longBreakLengthInMillis;
    }

    public long getPomodoroLengthInMillis() {
        return pomodoroLengthInMillis;
    }

    public long getShortBreakLengthInMillis() {
        return shortBreakLengthInMillis;
    }
    
    public Color getBreakProgressBarColor() {
        return DEFAULT_BREAK_PROGRESS_BAR_COLOR;
    }

    public Color getWorkProgressBarColor() {
        return DEFAULT_WORK_PROGRESS_BAR_COLOR;
    }
}
