package org.matveev.pomodoro4nb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.matveev.pomodoro4nb.controllers.Controller;
import org.matveev.pomodoro4nb.controllers.Handler;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.prefs.PreferencesProviderFactory;
import org.matveev.pomodoro4nb.task.TaskController;
import org.matveev.pomodoro4nb.task.TaskController.ActionWithAccelerator;
import org.matveev.pomodoro4nb.timer.TimerController;
import org.matveev.pomodoro4nb.utils.data.Property;
import org.matveev.pomodoro4nb.utils.data.PropertyListener;

/**
 *
 * @author Alexey Matvey
 */
public class PomodoroMainController implements PropertyListener {

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
}
