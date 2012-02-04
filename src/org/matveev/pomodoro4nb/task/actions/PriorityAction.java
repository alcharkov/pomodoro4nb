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
import javax.swing.AbstractAction;
import org.matveev.pomodoro4nb.task.Task;
import org.matveev.pomodoro4nb.task.TaskTable;
import org.matveev.pomodoro4nb.task.TaskTableModel;
import org.matveev.pomodoro4nb.utils.ValidatableAction;

/**
 *
 * @author Alexey Matveev
 */
public class PriorityAction extends AbstractAction implements ValidatableAction {

    private TaskTable table;
    private final Task.Priority priority;

    public PriorityAction(TaskTable table, Task.Priority priority) {
        super(priority != null ? priority.toString() : "Without priority");
        this.table = table;
        this.priority = priority;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TaskTableModel model = table.getTaskTableModel();
        model.getTask(table.getSelectedRow()).setProperty(Task.TaskPriority, priority);
        model.fireTableDataChanged();
    }

    @Override
    public void validate() {
        // do nothiing
    }
}
