/*
 * Pomodoro4NB - Netbeans plugin for work with The Pomodoro Technique
 * 
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
package org.matveev.pomodoro4nb.task;

import java.util.Date;
import java.util.UUID;
import org.matveev.pomodoro4nb.data.Properties;
import org.matveev.pomodoro4nb.data.Property;

/**
 *
 * @author Alexey Matvey
 */
public class Task extends Properties {

    public static final Property<UUID> Id = new Property<UUID>("id", UUID.class);
    public static final Property<String> Description = new Property<String>("desc", String.class);
    public static final Property<Integer> Estimate = new Property<Integer>("estimate", Integer.class);
    public static final Property<Integer> Pomodoros = new Property<Integer>("pomodoros", Integer.class);
    public static final Property<Integer> Interaptions = new Property<Integer>("interaptions", Integer.class);
    public static final Property<Integer> Unplaned = new Property<Integer>("unplaned", Integer.class);
    public static final Property<Boolean> Completed = new Property<Boolean>("completed", Boolean.class);
    public static final Property<Priority> TaskPriority = new Property<Priority>("priority", Priority.class);
    public static final Property<Date> CreationDate = new Property<Date>("date", Date.class);

    public enum Priority {

        Improvements,
        Blocker,
        Critical,
        Major,
        Minor,
        Trivial
    }

    public Task() {
        setProperty(Id, UUID.randomUUID());
        setProperty(CreationDate, new Date());
    }

    public static Task createTask(String desc, int estimate) {
        final Task task = new Task();
        task.setProperty(Description, desc);
        task.setProperty(Estimate, estimate);
        task.setProperty(Pomodoros, 0);
        return task;
    }

    public static void increment(Task task, Property<Integer> property) {
        Integer value = task.getProperty(property);
        if (value != null) {
            task.setProperty(property, ++value);
        }
    }
}
