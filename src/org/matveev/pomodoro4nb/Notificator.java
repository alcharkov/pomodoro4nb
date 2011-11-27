/* 
 * Pomodoro4NB - Netbeans plugin for work with The Pomodoro Technique
 * Copyright (C) 2011 Alexey Matveev <mvaleksej@gmail.com>
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

import org.matveev.pomodoro4nb.utils.Utils;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public class Notificator {

    private static int DEFAULT_NOTIFACTION_DELAY = 5 * 1000;
    // default notifications
    public static final String KEY_START_WORK = "notificationsStartWork";
    public static final String KEY_START_BREAK = "notificationsStartBreak";
    public static final String KEY_EMPTY_TASK_LIST = "notificationsEmptyTaskList";
//
//    public static void showNotifcationPopup(String key) {
//        NotificationPopup popup = new NotificationPopup(
//                getMessage(key + ".title"),  getMessage(key + ".text"));
//        final NotifyDescriptor descriptor = new NotifyDescriptor(
//                popup,
//                "Pomodoro",
//                ,
//                NotifyDescriptor.PLAIN_MESSAGE,
//                null,
//                NotifyDescriptor.OK_OPTION);
//        DialogDisplayer.getDefault().notify(descriptor);
//    }

    public static void showNotificationBalloon(String key) {
        showNotificationBalloon(key, null);
    }

    public static void showNotificationBalloon(String key, ActionListener listener) {
        final Notification notification = NotificationDisplayer.getDefault().notify(
                getMessage(key + ".title"),
                Utils.createIcon(getMessage(key + ".iconName")),
                getMessage(key + ".text"), listener);
        final Timer removeNotificationTimer = new Timer();
        removeNotificationTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                notification.clear();
            }
        }, DEFAULT_NOTIFACTION_DELAY);
    }

    private static String getMessage(final String key) {
        return NbBundle.getMessage(Notificator.class, key);
    }
}
