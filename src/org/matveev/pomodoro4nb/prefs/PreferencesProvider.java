package org.matveev.pomodoro4nb.prefs;

/**
 *
 * @author Alexey Matveev
 */
public interface PreferencesProvider {
    
    public void addPrefrencesListener(PreferencesListener l);
    public void removePrefrencesListener(PreferencesListener l);
    
    public String getString(String key, String fallback);
    public void setString(String key, String value);

    public int getInteger(String key, int fallback);
    public void setInteger(String key, int value);
    
    public boolean getBoolean(String key, boolean fallback);
    public void setBoolean(String key, boolean value);
    
    public interface PreferencesListener {
        public void preferencesChange(String key, Object newValue);
    }
}
