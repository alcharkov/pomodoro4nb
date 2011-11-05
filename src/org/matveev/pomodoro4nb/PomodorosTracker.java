package org.matveev.pomodoro4nb;

import org.matveev.pomodoro4nb.tasktable.TaskTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.matveev.pomodoro4nb.dialogs.AddAndEditTaskDialog;
import org.matveev.pomodoro4nb.io.ObjectSerializer;
import org.matveev.pomodoro4nb.prefs.DefaultPreferencesProvider;
import org.matveev.pomodoro4nb.prefs.PreferencesDialog;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider.PreferencesListener;
import org.matveev.pomodoro4nb.prefs.PreferencesProviderFactory;
import org.matveev.pomodoro4nb.tasktable.Task;
import org.matveev.pomodoro4nb.tasktable.TaskTableModel;
import org.matveev.pomodoro4nb.timer.PomodoroTimer;
import org.matveev.pomodoro4nb.timer.PomodoroTimer.State;
import org.matveev.pomodoro4nb.timer.PomodoroTimerData;
import org.matveev.pomodoro4nb.timer.PomodoroTimerListener;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public class PomodorosTracker implements HasProperties {

    private PreferencesProvider provider;
    private PomodoroTimer timerControl;
    private TaskTable table;
    private JPopupMenu popupMenu;
    private List<ValidatableAction> actions = new ArrayList<ValidatableAction>();
    private Task currentTask;

    public PomodorosTracker() {
    }

    public JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(getTimerControl(), BorderLayout.NORTH);
        content.add(createTaskTablePanel());
        return content;
    }

    @Override
    public void storeProperties(Properties props) throws IOException {
        storeTaskModel(props);
    }

    @Override
    public void loadProperties(Properties props) throws IOException, ClassNotFoundException {
        loadTaskModel(props);
    }

    private void storeTaskModel(Properties props) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        TaskTableModel model = getTable().getTaskTableModel();
        for (Task task : model.getTaskList()) {
            stringBuilder.append(ObjectSerializer.toString(task));
            stringBuilder.append(" ");
        }
        props.setProperty("data", stringBuilder.toString());
    }

    private void loadTaskModel(Properties props) throws IOException, ClassNotFoundException {
        String data = props.getProperty("data");
        if (data != null) {
            TaskTableModel model = getTable().getTaskTableModel();
            String[] records = data.split(" ");
            for (String r : records) {
                if (r != null && !r.isEmpty()) {
                    Task task = (Task) ObjectSerializer.fromString(r);
                    model.addTask(task);
                }
            }
        }
    }

    protected PomodoroTimer getTimerControl() {
        if (timerControl == null) {
            timerControl = new PomodoroTimer(createTimerDataForPrefs(getPreferencesProvider()));
            timerControl.addPomodoroTimerListener(new PomodoroTimerListener() {

                @Override
                public void stateChanged(State state, boolean forced) {
                    if (forced) {
                        return;
                    }
                    if (State.IDLE.equals(state)) {
                        Notificator.showNotification(Notificator.KEY_START_WORK);
                        tryPlaySound();
                    } else if (State.WORK.equals(state)) {
                        int index = getTable().getSelectedRow();
                        if (index != -1) {
                            currentTask = getTable().getTaskTableModel().getTask(index);
                        }
                    } else if (State.BREAK.equals(state)) {
                        if (currentTask != null) {
                            currentTask.incrementPomodoros();
                            getTable().getTaskTableModel().fireTableDataChanged();
                        }
                        Notificator.showNotification(Notificator.KEY_START_BREAK);
                        tryPlaySound();
                    }
                }
            });

            getPreferencesProvider().addPrefrencesListener(new PreferencesListener() {

                @Override
                public void preferencesChange(String key, Object newValue) {
                    timerControl.setNewTimerData(createTimerDataForPrefs(getPreferencesProvider()));
                }
            });
        }
        return timerControl;
    }

    private void tryPlaySound() {
        if (Boolean.TRUE.equals(getPreferencesProvider().getBoolean(
                DefaultPreferencesProvider.ENABLE_SOUNDS_KEY, false))) {
            Utils.playSound("budzik.wav");
        }
    }

    private static PomodoroTimerData createTimerDataForPrefs(PreferencesProvider provider) {
        return new PomodoroTimerData(
                provider.getInteger(DefaultPreferencesProvider.POMODORO_LENGTH_KEY,
                DefaultPreferencesProvider.DEFAULT_POMODORO_LENGTH),
                provider.getInteger(DefaultPreferencesProvider.SHORT_BREAK_LENGTH_KEY,
                DefaultPreferencesProvider.DEFAULT_SHORT_BREAK_LENGTH),
                provider.getInteger(DefaultPreferencesProvider.LONG_BREAK_LENGTH_KEY,
                DefaultPreferencesProvider.DEFAULT_LONG_BREAK_LENGTH),
                provider.getInteger(DefaultPreferencesProvider.LONG_BREAK_INTERVAL_KEY,
                DefaultPreferencesProvider.DEFAULT_LONG_BREAK_INTERVAL));
    }

    protected JScrollPane createTaskTablePanel() {
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
            addAction(popupMenu, new EditTaskAction());
            popupMenu.addSeparator();
            addAction(popupMenu, new MarkAction(getTable()));
            popupMenu.addSeparator();
            popupMenu.add(createTagsSubMenu());
            popupMenu.addSeparator();
            addAction(popupMenu, new ClearAlddDoneTasksAction());
            popupMenu.addSeparator();
            addAction(popupMenu, new ChangePreferencesAction());
        }
        validaActions();
        return popupMenu;
    }

    private JMenu createTagsSubMenu() {
        JMenu tagsMenu = new JMenu(new NullAction("Set tag for task"));
        actions.add((ValidatableAction) tagsMenu.getAction());
        
        tagsMenu.add(new JMenuItem(new SetTagAction(Task.Tag.Improvements)));
        tagsMenu.add(new JMenuItem(new SetTagAction(Task.Tag.Critical)));
        tagsMenu.add(new JMenuItem(new SetTagAction(Task.Tag.Major)));
        tagsMenu.add(new JMenuItem(new SetTagAction(Task.Tag.Minor)));
        tagsMenu.addSeparator();
        tagsMenu.add(new JMenuItem(new SetTagAction(null)));

        return tagsMenu;
    }

    private void addAction(JPopupMenu menu, ValidatableAction action) {
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

    public PreferencesProvider getPreferencesProvider() {
        if (provider == null) {
            provider = PreferencesProviderFactory.getPreferencesProvider();
        }
        return provider;
    }

    //<editor-fold defaultstate="collapsed" desc="Actions">
    private class ChangePreferencesAction extends AbstractAction implements ValidatableAction {

        public ChangePreferencesAction() {
            super(getMessage("buttonsPreferences.text"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PreferencesDialog.createPreferencesDialog(getPreferencesProvider());
        }

        @Override
        public void validate() {
        }
    }

    private static class MarkAction extends AbstractAction implements ValidatableAction {

        private final TaskTable taskTable;

        public MarkAction(TaskTable taskTable) {
            super(getMessage("buttonsMarkCompleted.text"));
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

    private class ClearAlddDoneTasksAction extends AbstractAction implements ValidatableAction {

        public ClearAlddDoneTasksAction() {
            super(getMessage("buttonsClearTaskTable.text"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TaskTableModel model = getTable().getTaskTableModel();
            for (int ix = 0; ix < model.getRowCount(); ix++) {
                Task task = model.getTask(ix);
                if (task.isCompleted()) {
                    model.removeTask(ix);
                }
            }
        }

        @Override
        public void validate() {
            setEnabled(table.getRowCount() > 0);
        }
    }

    private class RemoveTaskAction extends AbstractAction implements ValidatableAction {

        public RemoveTaskAction() {
            super(getMessage("buttonsRemoveTask.text"));
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

    private class EditTaskAction extends AbstractAction implements ValidatableAction {

        public EditTaskAction() {
            super(getMessage("buttonsEditTask.text"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int index = getTable().getSelectedRow();
            TaskTableModel model = getTable().getTaskTableModel();
            Task task = model.getTask(index);
            Task updatedTask = AddAndEditTaskDialog.createAddTaskDialog(task);
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

    private class SetTagAction extends AbstractAction implements ValidatableAction {

        private final Task.Tag tag;

        public SetTagAction(Task.Tag tag) {
            super(tag != null ? tag.toString() : "Without tag");
            this.tag = tag;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TaskTableModel model = getTable().getTaskTableModel();
            model.getTask(getTable().getSelectedRow()).setTag(tag);
            model.fireTableDataChanged();
        }

        @Override
        public void validate() {
            // do nothiing
        }
    }

    private class NullAction extends AbstractAction implements ValidatableAction {

        public NullAction(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // do nothing
        }

        @Override
        public void validate() {
            setEnabled(table.getSelectedRowCount() > 0);
        }
    }

    private class AddNewTaskAction extends AbstractAction implements ValidatableAction {

        public AddNewTaskAction() {
            super(getMessage("buttonsAddNewTask.text"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Task task = AddAndEditTaskDialog.createAddTaskDialog();
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
                getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
