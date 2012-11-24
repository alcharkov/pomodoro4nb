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
package org.matveev.pomodoro4nb.storage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.matveev.pomodoro4nb.core.data.Properties;
import org.matveev.pomodoro4nb.core.data.Property;
import org.matveev.pomodoro4nb.domain.Children;
import org.matveev.pomodoro4nb.domain.DomainObject;
import org.matveev.pomodoro4nb.domain.Task;

/**
 *
 * @author Alexey Matveev
 */
@Children(types = {Task.class})
public class Storage extends DomainObject {

    public static final Property<String> Version = new Property<String>("version", String.class);

    private static final String STORAGE_VERSION = "1.0";
    
    public Storage() {
        setProperty(Version, STORAGE_VERSION);
    }

    public int getTaskCount() {
        return getChildren().size();
    }

    public Task getTask(int index) {
        return (Task) getChildren().get(index);
    }

    public List<Task> getTaskList() {
        final List<Task> result = new CopyOnWriteArrayList<Task>();
        for (Properties e : getChildren()) {
            result.add((Task) e);
        }
        return result;
    }

    public void removeElements() {
        getChildren().clear();
    }
}
