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

import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.matveev.pomodoro4nb.task.Interruption.Type;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public class TaskTableModel extends AbstractTableModel {

    public enum Header {

        DESCRIPTION(getString("columnDescription.title"), String.class),
        ESTIMATE(getString("columnEstimate.title"), Integer.class),
        POMODOROS(getString("columnPomodoros.title"), Integer.class),
        INTERAPTIONS(getString("columnInteraptions.title"), Integer.class),
        UNPLANED(getString("columnUnplaned.title"), Integer.class);
        
        private final String title;
        private final Class type;

        private Header(String title, Class type) {
            this.title = title;
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public Class getType() {
            return type;
        }
    }
    
    private final Activity activity;

    public TaskTableModel(Activity activity) {
        this.activity = activity;
    }
    
    /*package*/ Activity getActivity() {
        return activity;
    }
    
    
    @Override
    public int getRowCount() {
        return activity.getTaskCount();
    }

    @Override
    public int getColumnCount() {
        return Header.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return Header.values()[column].getTitle();
    }

    @Override
    public Class getColumnClass(int column) {
        return Header.values()[column].getType();
    }

    @Override
    public Object getValueAt(int row, int column) {
        final Task task = activity.getTask(row);
        switch (Header.values()[column]) {
            case DESCRIPTION:
                return task.getProperty(Task.Description);
            case ESTIMATE:
                return task.getProperty(Task.Estimate);
            case POMODOROS:
                return task.getProperty(Task.Pomodoros);
            case INTERAPTIONS:
                return task.getInterruptions(Type.Interruption).size();
            case UNPLANED:
                return task.getInterruptions(Type.Unplanned).size();
        }
        return null;
    }

    public List<Task> getTaskList() {
        return activity.getTaskList();
    }

    public Task getTask(int index) {
        return activity.getTask(index);
    }

    public void moveTask(int fromIndex, int toIndex) {
        final Task task = activity.getTask(fromIndex);
        activity.removeElement(task);
        activity.addElement(toIndex, task);
        fireTableDataChanged();
    }

    public void addTask(final Task task) {
        activity.addElement(task);
        fireTableDataChanged();
    }

    public void insertTask(int index, Task updatedTask) {
        activity.removeElement(index);
        activity.addElement(index, updatedTask);
    }

    public void removeTask(int index) {
        activity.removeElement(index);
        fireTableDataChanged();
    }

    public void removeTask(final Task task) {
        activity.removeElement(task);
        fireTableDataChanged();
    }

    public void removeAllTasks() {
        activity.removeAllTasks();
        fireTableDataChanged();
    }

    private static String getString(final String key) {
        return NbBundle.getMessage(TaskTableModel.class, key);
    }
}
