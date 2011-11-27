package org.matveev.pomodoro4nb.timer;

import java.awt.Container;
import java.io.IOException;
import java.util.Properties;
import org.matveev.pomodoro4nb.controllers.AbstractController;
import org.matveev.pomodoro4nb.controllers.Handler;
import org.matveev.pomodoro4nb.prefs.DefaultPreferencesProvider;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.task.TaskController;
import org.matveev.pomodoro4nb.timer.PomodoroTimer.State;
import org.matveev.pomodoro4nb.utils.data.Property;

/**
 *
 * @author Alexey Matvey
 */
public class TimerController extends AbstractController {

    public static final String TIMER_CONTROLLER_ID = "timerController";
    public static final Property<StateInfo> STATE_CHANGED_PROPERTY = new Property<StateInfo>("stateChanged", StateInfo.class);
    public static final Property<Boolean> PRE_START_TIMER_PROPERTY = new Property<Boolean>("preStart", Boolean.class);
    private final PomodoroTimer timer;
    private final PreferencesProvider provider;

    public TimerController(final PreferencesProvider aProvider) {
        this.provider = aProvider;
        timer = new PomodoroTimer(createTimerDataForPrefs(provider));
        timer.addPomodoroTimerListener(new PomodoroTimerListener() {

            @Override
            public void stateChanged(State state, boolean forced) {
                final StateInfo info = new StateInfo(state, forced);
                fire(STATE_CHANGED_PROPERTY, null, info);
            }

            @Override
            public void preStart() {
                fire(PRE_START_TIMER_PROPERTY, null, null);
            }
        });

        provider.addPrefrencesListener(new PreferencesProvider.PreferencesListener() {

            @Override
            public void preferencesChange(String key, Object newValue) {
                timer.setTimerData(createTimerDataForPrefs(provider));
            }
        });

        //<editor-fold defaultstate="collapsed" desc="Register handlers">
        registerHandler(TaskController.ACTIVE_REMOVED_PROPERTY, new Handler<Boolean>() {

            @Override
            public void handle(Boolean oldValue, Boolean newValue) {
                if (Boolean.TRUE.equals(newValue)) {
                    timer.forcedStop();
                }
            }
        });

        registerHandler(TaskController.CAN_START_PROPERTY, new Handler<Boolean>() {

            @Override
            public void handle(Boolean oldValue, Boolean newValue) {
                if (Boolean.TRUE.equals(newValue)) {
                    timer.forcedStart();
                }
            }
        });
        //</editor-fold>
    }

    private static PomodoroTimerData createTimerDataForPrefs(PreferencesProvider provider) {
        return new PomodoroTimerData(
                provider.getInteger(DefaultPreferencesProvider.POMODORO_LENGTH_KEY,
                DefaultPreferencesProvider.DEFAULT_POMODORO_LENGTH),
                provider.getInteger(DefaultPreferencesProvider.SHORT_BREAK_LENGTH_KEY,
                DefaultPreferencesProvider.DEFAULT_SHORT_BREAK_LENGTH),
                provider.getInteger(DefaultPreferencesProvider.LONG_BREAK_LENGTH_KEY,
                DefaultPreferencesProvider.DEFAULT_LONG_BREAK_LENGTH),
                provider.getInteger(DefaultPreferencesProvider.LONG_BREAK_INTERVAL_KEY,
                DefaultPreferencesProvider.DEFAULT_LONG_BREAK_INTERVAL));
    }

    @Override
    public Container createUI() {
        return timer;
    }

    @Override
    public void restore(Properties props) throws IOException, ClassNotFoundException {
        // do nothing
    }

    @Override
    public void store(Properties props) throws IOException {
        // do nothing
    }

    public static final class StateInfo {

        private final State state;
        private final boolean isForced;

        public StateInfo(State state, boolean isForced) {
            this.state = state;
            this.isForced = isForced;
        }

        public boolean isForced() {
            return isForced;
        }

        public State getState() {
            return state;
        }
    }
}
