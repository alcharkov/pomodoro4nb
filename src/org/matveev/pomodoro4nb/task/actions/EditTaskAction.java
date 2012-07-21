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
import org.matveev.pomodoro4nb.dialogs.TaskDialog;
import org.matveev.pomodoro4nb.task.Task;
import org.matveev.pomodoro4nb.task.TaskTable;
import org.matveev.pomodoro4nb.task.TaskTableModel;

/**
 *
 * @author Alexey Matveev
 */
public class EditTaskAction extends BasicAction {

    private final TaskTable table;

    public EditTaskAction(TaskTable table) {
        super("actionEditTask");
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final int index = table.getSelectedRow();
        
        final TaskTableModel model = table.getTaskTableModel();
        final Task task = model.getTask(index);

        final TaskDialog taskDialog = new TaskDialog();
        taskDialog.setTaskToEdit(task);
        taskDialog.setVisible(true);

        Task updatedTask = taskDialog.getResult();
        if (updatedTask != null) {
            model.insertTask(index, updatedTask);
            model.fireTableDataChanged();
        }
    }

    @Override
    public void validate() {
        setEnabled(table.getSelectedRowCount() > 0);
    }
}