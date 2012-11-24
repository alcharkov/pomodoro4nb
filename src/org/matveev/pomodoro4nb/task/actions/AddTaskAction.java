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
import org.matveev.pomodoro4nb.dialogs.DialogResult;
import org.matveev.pomodoro4nb.dialogs.TaskDialog;
import org.matveev.pomodoro4nb.domain.Task;
import org.matveev.pomodoro4nb.task.TaskTable;
import org.matveev.pomodoro4nb.task.TaskTableModel;

/**
 *
 * @author Alexey Matveev
 */
public class AddTaskAction extends BasicAction {

    private final TaskTable table;

    public AddTaskAction(TaskTable table) {
        super("actionAddNewTask");
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final TaskDialog dialog = new TaskDialog();
        dialog.setVisible(true);
        
        final Task task = dialog.getResult();
        if (DialogResult.OK == dialog.getDialogResult() && task != null) {
            final TaskTableModel model = table.getTaskTableModel();
            model.addTask(task);

            int rowIndex = model.getRowCount() - 1;
            table.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
            table.scrollRectToVisible(table.getCellRect(rowIndex, 0, true));
        }
    }

    @Override
    public void validate() {
        // do nothing
    }
}