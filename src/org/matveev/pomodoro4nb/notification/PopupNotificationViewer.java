/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.matveev.pomodoro4nb.notification;

import javax.swing.JOptionPane;
import org.matveev.pomodoro4nb.utils.Callback;

/**
 *
 * @author amatveev
 */
public class PopupNotificationViewer implements NotifcationViewer {

    @Override
    public void show(NotificationSource source, Callback onHide) {
        final String title = source.getProperty(NotificationSource.Title);
        final String message = source.getProperty(NotificationSource.Text);

        final NotificationPopup popup = new NotificationPopup(title, message, onHide);

        JOptionPane.showMessageDialog(null, popup, title, JOptionPane.PLAIN_MESSAGE);
    }
}
