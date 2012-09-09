package org.matveev.pomodoro4nb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;
import org.matveev.pomodoro4nb.controllers.Controller;
import org.matveev.pomodoro4nb.data.Property;
import org.matveev.pomodoro4nb.data.PropertyListener;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.prefs.PreferencesProviderFactory;
import org.matveev.pomodoro4nb.storage.StorageProvider;
import org.matveev.pomodoro4nb.task.TaskController;
import org.matveev.pomodoro4nb.timer.ReminderController;
import org.matveev.pomodoro4nb.timer.TimerController;
import org.matveev.pomodoro4nb.utils.Handler;
import org.matveev.pomodoro4nb.utils.Storable;

/**
 *
 * @author Alexey Matvey
 */
public class PomodoroMainController implements PropertyListener, Storable {

    private final Map<String, Controller> controllers = new HashMap<String, Controller>();
    private final PreferencesProvider prefsProvider;
    private final StorageProvider storageProvider;

    public PomodoroMainController() {
        prefsProvider = PreferencesProviderFactory.getPreferencesProvider();
        storageProvider = new StorageProvider();
        
        registerSubController(ReminderController.ID, new ReminderController(prefsProvider));
        registerSubController(TimerController.ID, new TimerController(prefsProvider));
        registerSubController(TaskController.ID, new TaskController(prefsProvider));
    }

    public Container createContent() {
        final JPanel content = new JPanel(new BorderLayout());
        content.add(controllers.get(TimerController.ID).createUI(), BorderLayout.NORTH);
        content.add(controllers.get(TaskController.ID).createUI(), BorderLayout.CENTER);
        return content;
    }

    public final <T> T getProperty(Property<T> property) {
        for (Controller c : controllers.values()) {
            final T value = c.getProperty(property);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public final void registerSubController(String indentifier, Controller c) {
        controllers.put(indentifier, c);
        c.addPropertyListener(this);
    }

    public void unregisterSubController(String identifier) {
        Controller c = controllers.remove(identifier);
        if (c != null) {
            c.removePropertyListener(this);
        }
    }

    @Override
    public void propertyChange(Property<?> property, Object oldValue, Object newValue) {
        for (Controller c : controllers.values()) {
            for (Handler handler : c.getHandlers(property)) {
                handler.handle(oldValue, newValue);
            }
        }
    }

    public PreferencesProvider getPreferencesProvider() {
        return prefsProvider;
    }

    @Override
    public void restore(Properties props) throws Exception {
        for (Storable s : controllers.values()) {
            s.restore(props);
        }
        storageProvider.restore(props);
        ((TaskController)controllers.get(TaskController.ID)).setStorageProvider(storageProvider);
    }

    @Override
    public void store(Properties props) throws Exception {
        for (Storable s : controllers.values()) {
            s.store(props);
        }
        storageProvider.store(props);
    }
}
