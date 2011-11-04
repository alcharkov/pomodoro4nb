package org.matveev.pomodoro4nb;

import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
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

    public static void showNotification(String key) {
        showNotification(key, null);
    }
    
    public static void showNotification(String key, ActionListener listener) {
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
