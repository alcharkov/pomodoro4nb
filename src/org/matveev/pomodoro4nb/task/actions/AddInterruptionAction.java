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
import org.matveev.pomodoro4nb.dialogs.InterruptionDialog;
import org.matveev.pomodoro4nb.domain.Interruption;
import org.matveev.pomodoro4nb.domain.Task;
import org.matveev.pomodoro4nb.task.TaskTable;
import org.matveev.pomodoro4nb.task.TaskTableModel;

/**
 *
 * @author Alexey Matveev
 */
public class AddInterruptionAction extends BasicAction {

    private final TaskTable table;

    public AddInterruptionAction(TaskTable table) {
        super("actionAddInterruption");
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final TaskTableModel model = table.getTaskTableModel();
        final Task parentTask = model.getTask(table.getSelectedRow());

        final InterruptionDialog dialog = new InterruptionDialog();
        dialog.setVisible(true);

        final Interruption interruption = dialog.getResult();
        if (interruption != null) {
            interruption.setProperty(Task.Parent, parentTask.getProperty(Task.Id));
            interruption.setProperty(Interruption.InterruptionType, Interruption.Type.Interruption);
            parentTask.add(interruption);
            model.fireTableDataChanged();
        }
    }

    @Override
    public void validate() {
        setEnabled(table.getSelectedRowCount() > 0);
    }
}
