package org.matveev.pomodoro4nb.prefs;

/**
 *
 * @author Alexey Matveev
 */
public class PreferencesProviderFactory {

    private PreferencesProviderFactory() {
    }

    public static PreferencesProvider getPreferencesProvider() {
        return new DefaultPreferencesProvider();
    }
}
