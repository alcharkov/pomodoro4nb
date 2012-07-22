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
package org.matveev.pomodoro4nb.task.actions;

import java.awt.event.ActionEvent;
import org.matveev.pomodoro4nb.task.Task;
import org.matveev.pomodoro4nb.task.Task.Status;
import org.matveev.pomodoro4nb.task.TaskTable;

/**
 *
 * @author Alexey Matveev
 */
public class MarkTaskAction extends BasicAction {

    private final TaskTable table;

    public MarkTaskAction(TaskTable table) {
        super("actionMarkTask");
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Task task = table.getTaskTableModel().getTask(table.getSelectedRow());
        final boolean completed = !Boolean.TRUE.equals(task.getProperty(Task.Completed));
        task.setProperty(Task.Completed, completed);
        Status status = null;
        if (completed) {
            int delta = task.getProperty(Task.Estimate) - task.getProperty(Task.Pomodoros);
            if (delta >= 0) status = Status.Clear;
            else if (delta < 0 && delta > -3) status = Status.Cloudy;
            else status = Status.Stormy;
        }
        task.setProperty(Task.TaskStatus, status);
        
        table.getTaskTableModel().fireTableDataChanged();
    }

    @Override
    public void validate() {
        setEnabled(table.getSelectedRowCount() > 0);
    }
}
