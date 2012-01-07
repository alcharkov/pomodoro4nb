package org.matveev.pomodoro4nb.prefs;

import org.matveev.pomodoro4nb.data.Properties;
import org.matveev.pomodoro4nb.data.Property;

/**
 *
 * @author Alexey Matvey
 */
public class Settings extends Properties {
    
    public static final Property<Integer> PomodoroLength = new Property<Integer>("pomodoroLength", Integer.class);
    public static final Property<Integer> ShortBreakLength = new Property<Integer>("shortBreakLength", Integer.class);
    public static final Property<Integer> LongBreakLength = new Property<Integer>("longBreakLength", Integer.class);
    public static final Property<Integer> LongBreakInterval = new Property<Integer>("longBreakInterval", Integer.class);
    public static final Property<Boolean> EnableSounds = new Property<Boolean>("enableSounds", Boolean.class);

    public Settings() {
        setProperty(PomodoroLength, 25);
        setProperty(ShortBreakLength, 5);
        setProperty(LongBreakLength, 30);
        setProperty(LongBreakInterval, 4);
        setProperty(EnableSounds, Boolean.TRUE);
    }
}
