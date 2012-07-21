package org.matveev.pomodoro4nb.prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Alexey Matveev
 */
public class DefaultPreferencesProvider implements PreferencesProvider {

    public static final int DEFAULT_POMODORO_LENGTH = 25;
    public static final int DEFAULT_SHORT_BREAK_LENGTH = 5;
    public static final int DEFAULT_LONG_BREAK_LENGTH = 30;
    public static final int DEFAULT_LONG_BREAK_INTERVAL = 4;
    public static final int DEFAULT_REMINDER_INTERVAL = 1;
    
    public static final String POMODORO_LENGTH_KEY = "pomodoroLength";
    public static final String SHORT_BREAK_LENGTH_KEY = "shortBreakLength";
    public static final String LONG_BREAK_LENGTH_KEY = "longBreakLength";
    public static final String LONG_BREAK_INTERVAL_KEY = "longBreakInterval";
    public static final String ENABLE_SOUNDS_KEY = "enableSounds";
    public static final String REMINDER_INTERVAL_KEY = "reminderInterval";
    public static final String ENABLE_REMINDER_KEY = "enableReminder";
    
    private final List<PreferencesListener> listeners = new ArrayList<PreferencesListener>();
    private final Preferences prefs;

    public DefaultPreferencesProvider() {
        prefs = NbPreferences.forModule(DefaultPreferencesProvider.class);
        prefs.addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                firePreferencesChange(evt.getKey(), evt.getNewValue());
            }
        });
    }

    private void firePreferencesChange(String key, String newValue) {
        for(PreferencesListener l : listeners) {
            l.preferencesChange(key, newValue);
        }
    }

    @Override
    public String getString(String key, String fallback) {
        return prefs.get(key, fallback);
    }

    @Override
    public int getInteger(String key, int fallback) {
        return prefs.getInt(key, fallback);
    }

    @Override
    public void setString(String key, String value) {
        prefs.put(key, value);
    }

    @Override
    public void setInteger(String key, int value) {
        prefs.putInt(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean fallback) {
        return prefs.getBoolean(key, fallback);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
    }

    @Override
    public void addPrefrencesListener(PreferencesListener l) {
        listeners.add(l);
    }

    @Override
    public void removePrefrencesListener(PreferencesListener l) {
        listeners.remove(l);
    }
}
