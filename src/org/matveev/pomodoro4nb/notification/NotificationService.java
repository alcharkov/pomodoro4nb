/* 
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
package org.matveev.pomodoro4nb.notification;

import java.util.EnumMap;
import java.util.Map;
import org.matveev.pomodoro4nb.utils.Callback;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public final class NotificationService {

    public enum DisplayType {

        Balloon,
        Popup
    }

    public enum NotificationType {

        Work,
        Break;
    }
    
    public final Map<DisplayType, NotifcationViewer> viewers =
            new EnumMap<DisplayType, NotifcationViewer>(DisplayType.class);
    
    public final Map<NotificationType, NotificationSource> sources =
            new EnumMap<NotificationType, NotificationSource>(NotificationType.class);

    public NotificationService() {
        registerViewers();
        registerSources();
    }

    private void registerViewers() {
        viewers.put(DisplayType.Popup, new PopupNotificationViewer());
        viewers.put(DisplayType.Balloon, new BalloonNotificationViewer());
    }

    private void registerSources() {
        sources.put(NotificationType.Work, createSource("notificationsStartWork"));
        sources.put(NotificationType.Break, createSource("notificationsStartBreak"));
    }

    private NotificationSource createSource(String key) {
        final NotificationSource source = new NotificationSource();
        source.setProperty(NotificationSource.Title, getMessage(key + ".title"));
        source.setProperty(NotificationSource.Icon, getMessage(key + ".iconName"));
        source.setProperty(NotificationSource.Text, getMessage(key + ".text"));
        return source;
    }

    public void showNotification(NotificationType sourceType, DisplayType displayType, Callback onHide) {
        viewers.get(displayType).show(sources.get(sourceType), onHide);
    }

    private static String getMessage(final String key) {
        return NbBundle.getMessage(NotificationService.class, key);
    }
}
