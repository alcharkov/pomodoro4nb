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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.matveev.pomodoro4nb.utils.Utils;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public abstract class AbstractDialog<T> extends JDialog {

    private final JButton okButton;
    private final JButton cancelButton;
    
    private DialogResult dialogResult = DialogResult.Cancel;

    public AbstractDialog(String key) {
        setTitle(getString(key + ".title"));
        setModal(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        final Dimension size = new Dimension(65, 24);

        okButton = new JButton(new HideAction("OK", DialogResult.OK));
        okButton.setPreferredSize(size);
        okButton.requestFocusInWindow();

        cancelButton = new JButton(new HideAction("Cancel", DialogResult.Cancel));
        cancelButton.setPreferredSize(size);

        setLayout(new BorderLayout());
        initDialogContent();
        add(createButtonsFooter(), BorderLayout.SOUTH);
        pack();
    }

    private void initDialogContent() {
        final JPanel dialogContent = createDialogContent();
        dialogContent.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(dialogContent, BorderLayout.CENTER);
    }

    protected abstract JPanel createDialogContent();
    
    public abstract T getResult();

    protected final String getString(String key) {
        return NbBundle.getMessage(AbstractDialog.class, key);
    }

    private JPanel createButtonsFooter() {
        final JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));

        boolean isMac = Utils.isMac();
        footer.add(isMac ? cancelButton : okButton);
        footer.add(isMac ? okButton : cancelButton);
        return footer;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public DialogResult getDialogResult() {
        return dialogResult;
    }
    
    private final class HideAction extends AbstractAction {

        private final DialogResult newResult;
        
        private HideAction(String name, DialogResult input) {
            super(name);
            this.newResult = input;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            dialogResult = newResult;
        }
    }
}
