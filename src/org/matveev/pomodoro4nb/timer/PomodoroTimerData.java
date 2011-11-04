package org.matveev.pomodoro4nb.timer;

import java.awt.Color;

/**
 *
 * @author Alexey Matveev
 */
public class PomodoroTimerData {

    private static final Color DEFAULT_WORK_PROGRESS_BAR_COLOR = new Color(102, 213, 41);
    private static final Color DEFAULT_BREAK_PROGRESS_BAR_COLOR = new Color(183, 173, 237);
    private static final long DEFAULT_WORK_TIME_IN_MILLIS = 1000 * 7;
    private static final long DEFAULT_BREAK_TIME_IN_MILLIS = 1000 * 5;
    private static final long DEFAULT_LONG_BREAK_TIME_IN_MILLIS = 1000 * 60 * 30;
    private static final int DEFAULT_LONG_BREAK_INTERVAL = 4;
    
    private final Color workProgressBarColor;
    private final Color breakProgressBarColor;
    
    private final long workDurationInMillis;
    private final long breakDurationInMillis;
    private final long longBreakDurationInMillis;
    private final int longBreakInterval;

    public PomodoroTimerData() {
        this(DEFAULT_WORK_PROGRESS_BAR_COLOR,
                DEFAULT_BREAK_PROGRESS_BAR_COLOR,
                DEFAULT_WORK_TIME_IN_MILLIS,
                DEFAULT_BREAK_TIME_IN_MILLIS,
                DEFAULT_LONG_BREAK_TIME_IN_MILLIS,
                DEFAULT_LONG_BREAK_INTERVAL);
    }

    public PomodoroTimerData(Color workProgressBarColor, Color breakProgressBarColor,
            long workDurationInMillis, long breakDurationInMillis, long longBreakDurationInMillis,
            int longBreakInterval) {
        this.workProgressBarColor = workProgressBarColor;
        this.breakProgressBarColor = breakProgressBarColor;
        this.workDurationInMillis = workDurationInMillis;
        this.breakDurationInMillis = breakDurationInMillis;
        this.longBreakDurationInMillis = longBreakDurationInMillis;
        this.longBreakInterval = longBreakInterval;
    }

    public long getBreakDurationInMillis() {
        return breakDurationInMillis;
    }

    public long getLongBreakDurationInMillis() {
        return longBreakDurationInMillis;
    }

    public int getLongBreakInterval() {
        return longBreakInterval;
    }

    public long getWorkDurationInMillis() {
        return workDurationInMillis;
    }

    public Color getBreakProgressBarColor() {
        return breakProgressBarColor;
    }

    public Color getWorkProgressBarColor() {
        return workProgressBarColor;
    }
}
