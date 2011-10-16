package org.matveev.pomodoro4nb;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.matveev.pomodoro4nb.model.Task;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public class AddTaskDialog extends JDialog {

    private static final Logger LOG = Logger.getLogger(AddTaskDialog.class.getName());
    private static final int MAX_REOMENDED_POMODOROS_COUNT = 7;
    private DialogResult result = DialogResult.CANCEL;
    private JTextField descriptionField;
    private JTextField estimateField;
    private JLabel hintLabel;

    public AddTaskDialog(Window owner) {
        super(owner);
        setModal(true);

        createComponents();
    }

    public DialogResult getResult() {
        return result;
    }

    public Task getTask() {
        if (isEmpty(descriptionField.getText()) || isEmpty(estimateField.getText())) {
            return null;
        }
        return new Task(descriptionField.getText(), Integer.parseInt(estimateField.getText()));
    }

    private boolean isEmpty(final String text) {
        return text == null || (text != null && text.isEmpty());
    }

    private void createComponents() {
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        descriptionField = new JTextField();
        descriptionField.setPreferredSize(new Dimension(200, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        panel.add(descriptionField, gbc);

        estimateField = new JFormattedTextField(new Integer(0));
        estimateField.setPreferredSize(new Dimension(40, 20));
        estimateField.getDocument().addDocumentListener(new DocumentListener() {
            //<editor-fold defaultstate="collapsed" desc="Implementation">

            @Override
            public void insertUpdate(DocumentEvent e) {
                validateEstimate(e);
                pack();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // do nothing
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // do nothing
            }

            private void validateEstimate(final DocumentEvent e) {
                try {
                    final Document doc = e.getDocument();
                    final String text = doc.getText(0, doc.getLength());
                    if (text != null && !text.isEmpty()) {
                        if (MAX_REOMENDED_POMODOROS_COUNT < Integer.parseInt(text)) {
                            hintLabel.setVisible(true);
                            hintLabel.setText(NbBundle.getMessage(getClass(),
                                    "addTaskDialog.bigEstimateMessage"));
                        } else {
                            hintLabel.setVisible(false);
                        }
                    }
                } catch (BadLocationException ex) {
                    LOG.log(Level.WARNING, "", ex);
                }
            }
            //</editor-fold>
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        panel.add(estimateField, gbc);

        JLabel descriptionLabel = new JLabel(NbBundle.getMessage(getClass(),
                "addTaskDialog.descriptionLabel.text"));
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        panel.add(descriptionLabel, gbc);

        JLabel estimateLabel = new JLabel(NbBundle.getMessage(getClass(),
                "addTaskDialog.estimateLabel.text"));
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        panel.add(estimateLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        add(panel, gbc);

        hintLabel = new JLabel();
        hintLabel.setIcon(Utils.createIcon("hint.png"));
        hintLabel.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(hintLabel, gbc);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.setPreferredSize(new Dimension(150, 20));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                result = DialogResult.OK;
                setVisible(false);
            }
        });
        buttonsPanel.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                result = DialogResult.CANCEL;
                setVisible(false);
            }
        });
        buttonsPanel.add(cancelButton);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;
        add(buttonsPanel, gbc);

        setTitle(NbBundle.getMessage(getClass(), "addTaskDialog.title"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
    }
}
