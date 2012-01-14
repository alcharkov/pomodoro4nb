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
package org.matveev.pomodoro4nb.task;

import java.util.UUID;
import org.matveev.pomodoro4nb.data.Properties;
import org.matveev.pomodoro4nb.data.Property;

/**
 *
 * @author Alexey Matveev
 */
public class Interruption extends Properties {
    
    public enum Type {
        Interruption,
        Unplanned;
    }
    
    public static final Property<Type> InterruptionType = new Property<Type>("type", Type.class);
    public static final Property<UUID> Parent = new Property<UUID>("parent", UUID.class);
    public static final Property<String> Description = new Property<String>("description", String.class);
}
