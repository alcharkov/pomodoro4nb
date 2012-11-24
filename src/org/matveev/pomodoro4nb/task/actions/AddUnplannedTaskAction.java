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
import org.matveev.pomodoro4nb.domain.PropertyChangeNotifier;
import org.matveev.pomodoro4nb.dialogs.TaskDialog;
import org.matveev.pomodoro4nb.domain.Interruption;
import org.matveev.pomodoro4nb.domain.Task;
import org.matveev.pomodoro4nb.task.TaskController;
import org.matveev.pomodoro4nb.task.TaskTable;
import org.matveev.pomodoro4nb.task.TaskTableModel;

/**
 *
 * @author Alexey Matveev
 */
public class AddUnplannedTaskAction extends BasicAction {

    private final TaskTable table;
    private final PropertyChangeNotifier notifier;

    public AddUnplannedTaskAction(TaskTable table, PropertyChangeNotifier notifier) {
        super("actionUnplanned");
        this.table = table;
        this.notifier = notifier;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final TaskTableModel model = table.getTaskTableModel();
        final Task parentTask = model.getTask(table.getSelectedRow());

        final TaskDialog dialog = new TaskDialog();
        dialog.setVisible(true);

        final Task unplanned = dialog.getResult();
        if (unplanned != null) {
            unplanned.setProperty(Task.Parent, parentTask.getProperty(Task.Id));

            Interruption interrupt = new Interruption();
            interrupt.setProperty(Interruption.Parent, parentTask.getProperty(Task.Id));
            interrupt.setProperty(Interruption.InterruptionType, Interruption.Type.Unplanned);
            interrupt.setProperty(Interruption.Description, "Created unplaned task");

            parentTask.add(interrupt);

            model.addTask(unplanned);
            model.fireTableDataChanged();

            notifier.fire(TaskController.InterruptionAddedProperty, null, interrupt);
        }
    }

    @Override
    public void validate() {
        setEnabled(table.getSelectedRowCount() > 0);
    }
}