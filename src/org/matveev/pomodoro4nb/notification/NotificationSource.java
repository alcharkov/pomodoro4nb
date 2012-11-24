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

import org.matveev.pomodoro4nb.core.data.Properties;
import org.matveev.pomodoro4nb.core.data.Property;

/**
 *
 * @author Alexey Matveev
 */
public class NotificationSource extends Properties {

    public static final Property<String> Title = new Property<String>("title", String.class);
    public static final Property<String> Text = new Property<String>("text", String.class);
    public static final Property<String> Icon = new Property<String>("icon", String.class);
    
    public NotificationSource() {
    }
}
