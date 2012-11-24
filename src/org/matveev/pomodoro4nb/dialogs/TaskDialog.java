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
package org.matveev.pomodoro4nb.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import org.matveev.pomodoro4nb.domain.Task;
import org.matveev.pomodoro4nb.utils.Utils;

/**
 *
 * @author Alexey Matveev
 */
public class TaskDialog extends AbstractDialog<Task> {

    private JTextField descriptionField;
    private JSpinner estimateSpinner;
    private Task taskToEdit;

    public TaskDialog() {
        super("TaskDialog");
    }

    public void setTaskToEdit(Task taskToEdit) {
        this.taskToEdit = taskToEdit;
        if (taskToEdit != null) {
            descriptionField.setText(taskToEdit.getProperty(Task.Description));
            estimateSpinner.setValue(taskToEdit.getProperty(Task.Estimate));
        }

    }

    @Override
    protected JPanel createDialogContent() {
        final JPanel contentPanel = new JPanel(new GridBagLayout());

        final JLabel descriptionLabel = new JLabel(getString("TaskDialog.descriptionLabel.text"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 0, 5);
        contentPanel.add(descriptionLabel, gbc);

        final JLabel estimateLabel = new JLabel(getString("TaskDialog.estimateLabel.text"));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 0, 5);
        contentPanel.add(estimateLabel, gbc);

        descriptionField = new JTextField();
        descriptionField.setPreferredSize(new Dimension(200, 20));
        descriptionField.getDocument().addDocumentListener(new DocumentValidator(getOkButton().getAction()));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        contentPanel.add(descriptionField, gbc);

        estimateSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        contentPanel.add(estimateSpinner, gbc);

        return contentPanel;
    }

    @Override
    public Task getResult() {
        final String description = descriptionField.getText();
        if (Utils.isNotEmpty(description)) {
            Task newTask = new Task();
            newTask.setProperty(Task.Description, description);
            newTask.setProperty(Task.Estimate, estimateSpinner.getValue());
            return newTask;
        }
        return taskToEdit;
    }

    public Task getEditable() {
        return taskToEdit;
    }
}
