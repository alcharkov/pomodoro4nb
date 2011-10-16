package org.matveev.pomodoro4nb;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.matveev.pomodoro4nb.io.ObjectSerializer;
import org.matveev.pomodoro4nb.model.Task;
import org.matveev.pomodoro4nb.model.TaskTableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public class PomodorosTracker {

    private TimerControl timerControl;
    private TaskTable table;
    private JPopupMenu popupMenu;
    private List<ValidatableAction> actions = new ArrayList<ValidatableAction>();

    public PomodorosTracker() {
    }
    
    
    public JPanel createContent()  {
        // TODO: read settings here...
        JPanel content = new JPanel(new BorderLayout());
        content.add(getTimerControl(), BorderLayout.NORTH);
        content.add(createTaskTablePanel());
        return content;
    }
    
    
    public void storeProperties(Properties props) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        TaskTableModel model = getTable().getTaskTableModel();
        for(Task task : model.getTaskList()) {
            stringBuilder.append(ObjectSerializer.toString(task));
            stringBuilder.append(" ");
        }
        props.setProperty("data", stringBuilder.toString());
    }

    
    public TimerControl getTimerControl() {
        if(timerControl == null) {
            timerControl = new TimerControl(new TimerControl.TimerControlInputHandler() {

                @Override
                public void handle() {
                    if(timerControl.isStarted()) {
                        timerControl.stop();
                        timerControl.removeAllPropertyChangeLiteners();
                    } else {
                        if(canStartTimer()) {
                            timerControl.start();
                            timerControl.addPropertyChangeListener(
                                    new TaskMetricsUpdater(getTable().getTaskTableModel().getTask(
                                    getTable().getSelectedRow())));
                        }
                    }
                }
                
                private boolean canStartTimer() {
                    return getTable().getSelectedRowCount() > 0;
                }
            });
        }
        return timerControl;
    }
    
    private class TaskMetricsUpdater implements PropertyChangeListener {

        private final Task task;

        public TaskMetricsUpdater(Task task) {
            this.task = task;
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(TimerControl.STATUS_PROPERTY.equals(evt.getPropertyName())) {
                if(TimerControl.Status.DONE.equals(evt.getNewValue())) {
                    task.setPomodoros(task.getPomodoros() + 1);
                    getTable().getTaskTableModel().fireTableDataChanged();
                    getTable().selectNextRow(getTable().getTaskTableModel().getTaskList().indexOf(task));
                }
            }
        }
        
    }

    public void updateContent(Properties props) throws IOException, ClassNotFoundException {
           String data = props.getProperty("data");
        if(data != null) {
            TaskTableModel model = getTable().getTaskTableModel();
            String [] records = data.split(" ");
            for(String r : records) {
                Task task = (Task) ObjectSerializer.fromString(r);
                model.addTask(task);
            }
        }
    }
    
    
    private JScrollPane createTaskTablePanel() {
        final JScrollPane scrollPane = new JScrollPane(getTable(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                setPreferredTableColumnWidths(e.getComponent().getSize(), getTable(),
                        new double[]{0.6, 0.1, 0.1, 0.1, 0.1});
                getTable().revalidate();
                getTable().repaint();
            }
        });
        scrollPane.addMouseListener(new PopupListener());
        return scrollPane;
    }

    private TaskTable getTable() {
        if (table == null) {
            table = new TaskTable();
            ActionValidator validator = new ActionValidator();
            table.getSelectionModel().addListSelectionListener(validator);
            table.getModel().addTableModelListener(validator);

            PopupListener listener = new PopupListener();
            table.addMouseListener(listener);
            table.getTableHeader().addMouseListener(listener);
        }
        return table;
    }

    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            addAction(popupMenu, new AddNewTaskAction());
            addAction(popupMenu, new RemoveTaskAction());
            popupMenu.addSeparator();
            addAction(popupMenu, new MarkAction(getTable()));
            popupMenu.addSeparator();
            addAction(popupMenu, new ClearTaskTableAction());
        }
        return popupMenu;
    }

    public void addAction(JPopupMenu menu, ValidatableAction action) {
        actions.add(action);
        menu.add(action);
    }

    private void setPreferredTableColumnWidths(Dimension d, JTable table, double[] percentages) {
        Dimension tableDim = d;

        double total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int) Math.round(tableDim.width * (percentages[i] / total)));
        }
    }

    private void validaActions() {
        for (ValidatableAction a : actions) {
            a.validate();
        }
    }


    private static String getMessage(final String key) {
        return NbBundle.getMessage(PomodorosTracker.class, key);
    }

    //<editor-fold defaultstate="collapsed" desc="Actions">
    private static class MarkAction extends AbstractAction implements ValidatableAction {

        private final TaskTable taskTable;

        public MarkAction(TaskTable taskTable) {
            super(getMessage("buttonsMarkCompleted.tooltip"),
                    /*createIcon(getMessage("buttonsMarkCompleted.iconName"))*/ null);
            this.taskTable = taskTable;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Task task = taskTable.getTaskTableModel().getTask(taskTable.getSelectedRow());
            task.setCompleted(!task.isCompleted());
            taskTable.getTaskTableModel().fireTableDataChanged();
        }

        @Override
        public void validate() {
            setEnabled(taskTable.getSelectedRowCount() > 0);
        }
    }


    private class ClearTaskTableAction extends AbstractAction implements ValidatableAction {

        public ClearTaskTableAction() {
            super(getMessage("buttonsClearTaskTable.tooltip"),
                    Utils.createIcon(getMessage("buttonsClearTaskTable.iconName")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
       

            ((TaskTableModel) table.getModel()).removeAllTasks();
        }

        @Override
        public void validate() {
            setEnabled(table.getRowCount() > 0);
        }
    }

    private class RemoveTaskAction extends AbstractAction implements ValidatableAction {

        public RemoveTaskAction() {
            super(getMessage("buttonsRemoveTask.tooltip"),
                    Utils.createIcon(getMessage("buttonsRemoveTask.iconName")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] rows = table.getSelectedRows();
            TaskTableModel model = (TaskTableModel) table.getModel();
            for (int row : rows) {
                model.removeTask(row);
            }
            table.clearSelection();
        }

        @Override
        public void validate() {
            setEnabled(table.getSelectedRowCount() > 0);
        }
    }

    private class AddNewTaskAction extends AbstractAction implements ValidatableAction {

        public AddNewTaskAction() {
            super(getMessage("buttonsAddNewTask.tooltip"),
                    Utils.createIcon(getMessage("buttonsAddNewTask.iconName")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AddTaskDialog dialog = new AddTaskDialog(null);
            dialog.setVisible(true);

            final Task task = dialog.getTask();
            if (task != null) {
                ((TaskTableModel) table.getModel()).addTask(task);
            }
        }

        @Override
        public void validate() {
            // do nothing
        }
    }
    //</editor-fold>

    private class ActionValidator implements TableModelListener, ListSelectionListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            validaActions();
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            validaActions();
        }
    }

    private class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                validaActions();
                getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
