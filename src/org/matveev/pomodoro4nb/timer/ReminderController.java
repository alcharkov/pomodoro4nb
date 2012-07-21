/*
 * Copyright (C) 2012 Alexey Matveev <mvaleksej@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.matveev.pomodoro4nb.timer;

import java.awt.Container;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.matveev.pomodoro4nb.Notificator;
import org.matveev.pomodoro4nb.controllers.AbstractController;
import org.matveev.pomodoro4nb.prefs.DefaultPreferencesProvider;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.timer.PomodoroTimer.State;
import org.matveev.pomodoro4nb.timer.TimerController.StateInfo;
import org.matveev.pomodoro4nb.utils.Handler;
import org.matveev.pomodoro4nb.utils.MediaPlayer;
import org.matveev.pomodoro4nb.utils.Resources;

/**
 *
 * @author Alexey Matveev
 */
public class ReminderController extends AbstractController {

    public static final String ID = "reminderController";
    private final PreferencesProvider provider;
    private ScheduledExecutorService scheduler;

    public ReminderController(PreferencesProvider provider) {
        this.provider = provider;
        if (Boolean.TRUE.equals(provider.getBoolean(
                DefaultPreferencesProvider.ENABLE_REMINDER_KEY, false))) {
            registerHandler(TimerController.STATE_CHANGED_PROPERTY,
                    new Handler<TimerController.StateInfo>() {

                        @Override
                        public void handle(StateInfo oldState, StateInfo newState) {
                            setReminderActive(newState != null && State.IDLE == newState.getState());
                        }
                    });

            setReminderActive(true);
        }
    }

    private void setReminderActive(boolean active) {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        if (active) {
            final int interval = getInterval();
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    Notificator.showNotificationBalloon(Notificator.KEY_START_WORK);
                    MediaPlayer.play(Resources.getSound("budzik.wav"));
                }
            }, interval, interval, TimeUnit.MINUTES);
        }
    }

    private int getInterval() {
        return provider.getInteger(DefaultPreferencesProvider.REMINDER_INTERVAL_KEY,
                DefaultPreferencesProvider.DEFAULT_REMINDER_INTERVAL);
    }

    @Override
    public Container createUI() {
        return null;
    }
}
