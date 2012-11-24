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
package org.matveev.pomodoro4nb.notification;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.matveev.pomodoro4nb.utils.Callback;
import org.matveev.pomodoro4nb.utils.Null;
import org.matveev.pomodoro4nb.utils.Resources;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;

/**
 *
 * @author amatveev
 */
public class BalloonNotificationViewer implements NotifcationViewer {

    private static int DEFAULT_NOTIFACTION_DELAY = 5 * 1000;

    private ScheduledExecutorService removeNotificationTimer;
    
    public BalloonNotificationViewer() {
        
    }
    
    @Override
    public void show(final NotificationSource source, final Callback onHide) {
        final Notification notification = NotificationDisplayer.getDefault().notify(
                source.getProperty(NotificationSource.Title),
                Resources.createIcon(source.getProperty(NotificationSource.Icon)),
                source.getProperty(NotificationSource.Text),
                null);

        removeNotificationTimer = Executors.newSingleThreadScheduledExecutor();
        removeNotificationTimer.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                notification.clear();
                removeNotificationTimer.shutdownNow();
                if (onHide != null) {
                    onHide.call(Null.NULL);
                }
            }
        }, DEFAULT_NOTIFACTION_DELAY, 0, TimeUnit.MILLISECONDS);
    }
}
