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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.matveev.pomodoro4nb.data.Children;
import org.matveev.pomodoro4nb.data.Properties;

/**
 *
 * @author Alexey Matveev
 */
@Children({Task.class})
public class Activity extends Properties {    
    
    public int getTaskCount() {
        return getElements().size();
    }
    
    public Task getTask(int index) {
        return (Task) getElements().get(index);
    }
    
    public List<Task> getTaskList() {
        final List<Task> result = new CopyOnWriteArrayList<Task>();
        for (Properties e : getElements()) {
            result.add((Task) e);
        }
        return result;
    }
}
