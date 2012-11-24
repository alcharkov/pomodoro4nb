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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.matveev.pomodoro4nb.domain.Interruption;

/**
 *
 * @author Alexey Matveev
 */
public class InterruptionDialog extends AbstractDialog<Interruption> {

    private JTextField descriptionField;

    public InterruptionDialog() {
        super("InterruptionDialog");
    }

    @Override
    protected JPanel createDialogContent() {
        final JPanel content = new JPanel(new BorderLayout());

        final JLabel descriptionLabel = new JLabel(getString("InterruptionDialog.descriptionLabel.text"));
        content.add(descriptionLabel, BorderLayout.NORTH);

        descriptionField = new JTextField();
        descriptionField.setPreferredSize(new Dimension(250, 20));
        final Action action = getOkButton().getAction();
        descriptionField.getDocument().addDocumentListener(new DocumentValidator(action));
        content.add(descriptionField, BorderLayout.CENTER);

        return content;
    }

    @Override
    public Interruption getResult() {
        final Interruption interruption = new Interruption();
        interruption.setProperty(Interruption.Description, descriptionField.getText());
        return interruption;
    }
}