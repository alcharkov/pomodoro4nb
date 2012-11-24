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
package org.matveev.pomodoro4nb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;
import org.matveev.pomodoro4nb.controllers.Controller;
import org.matveev.pomodoro4nb.core.data.Property;
import org.matveev.pomodoro4nb.core.data.PropertyListener;
import org.matveev.pomodoro4nb.notification.NotificationService;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.prefs.PreferencesProviderFactory;
import org.matveev.pomodoro4nb.storage.StorageController;
import org.matveev.pomodoro4nb.task.TaskController;
import org.matveev.pomodoro4nb.timer.ReminderController;
import org.matveev.pomodoro4nb.timer.TimerController;
import org.matveev.pomodoro4nb.utils.Handler;
import org.matveev.pomodoro4nb.utils.Storable;

/**
 *
 * @author Alexey Matveev
 */
public class Pomodoro4NbController implements PropertyListener, Storable {

    private final Map<String, Controller> controllers = new HashMap<String, Controller>();
    private final PreferencesProvider prefsProvider;
    private final StorageController storageProvider;
    private final NotificationService notificationService;
    
    

    public Pomodoro4NbController() {
        notificationService = new NotificationService();
        prefsProvider = PreferencesProviderFactory.getPreferencesProvider();
        storageProvider = new StorageController();
        
        registerSubController(ReminderController.ID, new ReminderController(this));
        registerSubController(TimerController.ID, new TimerController(prefsProvider));
        registerSubController(TaskController.ID, new TaskController(this));
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

    public NotificationService getNotificationService() {
        return notificationService;
    }
}
