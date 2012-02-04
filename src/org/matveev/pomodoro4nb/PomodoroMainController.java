package org.matveev.pomodoro4nb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;
import org.matveev.pomodoro4nb.controllers.Controller;
import org.matveev.pomodoro4nb.utils.Handler;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.prefs.PreferencesProviderFactory;
import org.matveev.pomodoro4nb.task.TaskController;
import org.matveev.pomodoro4nb.timer.TimerController;
import org.matveev.pomodoro4nb.data.Property;
import org.matveev.pomodoro4nb.data.PropertyListener;
import org.matveev.pomodoro4nb.utils.Storable;

/**
 *
 * @author Alexey Matvey
 */
public class PomodoroMainController implements PropertyListener, Storable {

    private final Map<String, Controller> controllers = new HashMap<String, Controller>();
    private final TimerController timerController;
    private final TaskController taskController;
    private final PreferencesProvider provider;

    public PomodoroMainController() {
        provider = PreferencesProviderFactory.getPreferencesProvider();
        timerController = new TimerController(provider);
        taskController = new TaskController(provider);

        registerSubController(TimerController.TIMER_CONTROLLER_ID, timerController);
        registerSubController(TaskController.TASK_CONTROLLER_ID, taskController);
    }

    public Container createContent() {
        final JPanel content = new JPanel(new BorderLayout());
        content.add(controllers.get(TimerController.TIMER_CONTROLLER_ID).createUI(), BorderLayout.NORTH);
        content.add(controllers.get(TaskController.TASK_CONTROLLER_ID).createUI(), BorderLayout.CENTER);
        return content;
    }

    public void createQuickActionPanel(Container c) {
        c.add(taskController.createQuickActionPanel(), BorderLayout.SOUTH);
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
        return provider;
    }

    @Override
    public void restore(Properties props) throws Exception {
        for (Storable s : controllers.values()) {
            s.restore(props);
        }
    }

    @Override
    public void store(Properties props) throws Exception {
        for (Storable s : controllers.values()) {
            s.store(props);
        }
    }
}
