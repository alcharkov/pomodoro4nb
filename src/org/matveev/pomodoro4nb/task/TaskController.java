package org.matveev.pomodoro4nb.task;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.matveev.pomodoro4nb.Notificator;
import org.matveev.pomodoro4nb.utils.ValidatableAction;
import org.matveev.pomodoro4nb.controllers.AbstractController;
import org.matveev.pomodoro4nb.controls.netbeans.TapPanel;
import org.matveev.pomodoro4nb.data.Properties;
import org.matveev.pomodoro4nb.dialogs.TaskDialog;
import org.matveev.pomodoro4nb.prefs.PreferencesDialog;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.timer.PomodoroTimer.State;
import org.matveev.pomodoro4nb.timer.TimerController;
import org.matveev.pomodoro4nb.timer.TimerController.StateInfo;
import org.matveev.pomodoro4nb.data.Property;
import org.matveev.pomodoro4nb.data.io.PropertiesSerializer;
import org.matveev.pomodoro4nb.data.io.PropertiesSerializerFactory;
import org.matveev.pomodoro4nb.dialogs.InterruptionDialog;
import org.matveev.pomodoro4nb.utils.Base64Coder;
import org.matveev.pomodoro4nb.utils.Handler;
import org.matveev.pomodoro4nb.utils.Resources;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matvey
 */
public class TaskController extends AbstractController {

    public static final String TASK_CONTROLLER_ID = "taskController";
    //<editor-fold defaultstate="collapsed" desc="Properties">
    public static final Property<Task> TASK_ADDED_PROPERTY = new Property<Task>("taskAdded", Task.class);
    public static final Property<Interruption> INTERRUPTION_ADDED_PROPERTY = new Property<Interruption>("interrAdded", Interruption.class);
    public static final Property<Task> TASK_REMOVED_PROPERTY = new Property<Task>("taskRemoved", Task.class);
    public static final Property<Boolean> ACTIVE_REMOVED_PROPERTY = new Property<Boolean>("activeRemoved", Boolean.class);
    public static final Property<Boolean> CAN_START_PROPERTY = new Property<Boolean>("canStart", Boolean.class);
    //</editor-fold>
    private final TaskTable taskTable;
    private final JPopupMenu popupMenu;
    private final Map<String, ValidatableAction> actionsMap = new HashMap<String, ValidatableAction>();
    private final PreferencesProvider provider;
    private Task currentTask;

    public TaskController(PreferencesProvider provider) {
        this.provider = provider;
        //<editor-fold defaultstate="collapsed" desc="Create components">
        taskTable = new TaskTable();
        ActionValidator validator = new ActionValidator();
        taskTable.getSelectionModel().addListSelectionListener(validator);
        taskTable.getModel().addTableModelListener(validator);
        taskTable.addMouseListener(new TooltipViewer(taskTable));
        taskTable.addMouseListener(new RightClickSelector(taskTable));

        popupMenu = createTaskTablePopupMenu();
        PopupListener listener = new PopupListener(popupMenu);
        taskTable.addMouseListener(listener);
        taskTable.getTableHeader().addMouseListener(listener);
        //</editor-fold>

        registerHandler(TimerController.STATE_CHANGED_PROPERTY, new Handler<StateInfo>() {

            @Override
            public void handle(StateInfo oldState, StateInfo newState) {
                if (newState != null && !newState.isForced()) {
                    if (State.IDLE.equals(newState.getState())) {
                        Notificator.showNotificationBalloon(Notificator.KEY_START_WORK);
                        // tryPlaySound();
                    } else if (State.WORK.equals(newState.getState())) {
                        int index = taskTable.getSelectedRow();
                        if (index != -1) {
                            currentTask = taskTable.getTaskTableModel().getTask(index);
                        }
                    } else if (State.BREAK.equals(newState.getState())) {
                        if (currentTask != null) {
                            final Integer pomodoros = currentTask.getProperty(Task.Pomodoros);
                            currentTask.setProperty(Task.Pomodoros, new Integer(pomodoros + 1));
                            taskTable.getTaskTableModel().fireTableDataChanged();
                        }
                        Notificator.showNotificationBalloon(Notificator.KEY_START_BREAK);
                        // tryPlaySound();
                    }
                }
            }
        });

        registerHandler(TimerController.PRE_START_TIMER_PROPERTY, new Handler<Boolean>() {

            @Override
            public void handle(Boolean oldValue, Boolean newValue) {
                boolean canStart = (taskTable.getRowCount() > 0) && (taskTable.getSelectedRowCount() > 0);
                fire(TaskController.CAN_START_PROPERTY, !canStart, canStart);

                if (!canStart) {
                    actionsMap.get(AddTaskAction.class.getName()).actionPerformed(null);
                }
            }
        });
    }

    public Container createQuickActionPanel() {
        final TapPanel panel = new TapPanel();
        panel.setOrientation(TapPanel.DOWN);
        panel.setPreferredSize(new Dimension(panel.getWidth(), 20));

        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        tools.setRollover(true);

        tools.add(new AddTaskAction(taskTable, true));
        tools.add(new RemoveTaskAction(taskTable, true));
        tools.add(new EditTaskAction(taskTable, true));
        tools.addSeparator();
        tools.add(new MarkAction(taskTable, true));
        tools.addSeparator();
        tools.add(new ClearAlddDoneTasksAction(taskTable, true));
        tools.add(new ChangePreferencesAction(true));

        panel.add(new JPanel());
        panel.add(tools);

        return panel;
    }

    private JPopupMenu createTaskTablePopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        addAction(menu, new AddTaskAction(taskTable));
        addAction(menu, new RemoveTaskAction(taskTable));
        addAction(menu, new EditTaskAction(taskTable));
        menu.addSeparator();
        addAction(menu, new MarkAction(taskTable));
        menu.addSeparator();
        menu.add(createTagsSubMenu());
        menu.addSeparator();
        addAction(menu, new AddInteraptionAction(taskTable));
        addAction(menu, new AddUnplannedTaskAction(taskTable));
        menu.addSeparator();
        addAction(menu, new ClearAlddDoneTasksAction(taskTable));
        menu.addSeparator();
        addAction(menu, new ChangePreferencesAction());

        validaActions();

        return menu;
    }

    private JMenu createTagsSubMenu() {
        JMenu tagsMenu = new JMenu(new NullAction(taskTable, "Set tag for task"));

        ValidatableAction a = (ValidatableAction) tagsMenu.getAction();
        actionsMap.put(a.getClass().getName(), a);

        tagsMenu.add(new JMenuItem(new SetTagAction(taskTable, Task.Priority.Improvements)));
        tagsMenu.add(new JMenuItem(new SetTagAction(taskTable, Task.Priority.Blocker)));
        tagsMenu.add(new JMenuItem(new SetTagAction(taskTable, Task.Priority.Critical)));
        tagsMenu.add(new JMenuItem(new SetTagAction(taskTable, Task.Priority.Major)));
        tagsMenu.add(new JMenuItem(new SetTagAction(taskTable, Task.Priority.Minor)));
        tagsMenu.add(new JMenuItem(new SetTagAction(taskTable, Task.Priority.Trivial)));
        tagsMenu.addSeparator();
        tagsMenu.add(new JMenuItem(new SetTagAction(taskTable, null)));

        return tagsMenu;
    }

    private void addAction(JPopupMenu menu, ValidatableAction action) {
        actionsMap.put(action.getClass().getName(), action);

        JMenuItem item = new JMenuItem(action);
        if (action instanceof BasicAction) {
            item.setAccelerator(((BasicAction) action).getActionKeyStroke());
        }
        menu.add(item);
    }

    @Override
    public Container createUI() {
        return createTaskTablePanel();
    }

    private JScrollPane createTaskTablePanel() {
        final JScrollPane scrollPane = new JScrollPane(taskTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                setPreferredTableColumnWidths(e.getComponent().getSize(), taskTable,
                        new double[]{0.6, 0.1, 0.1, 0.1, 0.1});
                taskTable.revalidate();
                taskTable.repaint();
            }
        });
        scrollPane.addMouseListener(new PopupListener(popupMenu));
        registerShortCuts(scrollPane);
        return scrollPane;
    }

    private void registerShortCuts(JComponent panel) {
        InputMap im = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();
        for (Action action : actionsMap.values()) {
            if (action instanceof BasicAction) {
                im.put(((BasicAction) action).getActionKeyStroke(), action.getValue(Action.NAME));
                am.put(action.getValue(Action.NAME), action);
            }
        }
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

    @Override
    public void store(java.util.Properties props) throws Exception {
        final PropertiesSerializer serializer = PropertiesSerializerFactory.createXMLSerializer();

        final StringBuilder data = new StringBuilder();

        final List<Task> taskList = taskTable.getTaskTableModel().getTaskList();
        for (Task t : taskList) {
            data.append(serializer.serialize(t));
            data.append("\n");
        }

        props.setProperty("model", Base64Coder.encodeString(data.toString()));
    }

    public void restore(java.util.Properties props) throws Exception {
        Object data = props.getProperty("data");
        JOptionPane.showMessageDialog(null, data);
        if (data != null) {
            final PropertiesSerializer serializer = PropertiesSerializerFactory.createXMLSerializer();
            final String xmlString = Base64Coder.decodeString((String) data);
            final TaskTableModel model = taskTable.getTaskTableModel();
            Activity activity = (Activity) serializer.deserealize(xmlString);
            for (Properties p : activity.getElements()) {
                model.addTask((Task) p);
            }
        }
    }

    public Iterable<ValidatableAction> getActionList() {
        return Collections.unmodifiableCollection(actionsMap.values());
    }

    //<editor-fold defaultstate="collapsed" desc="Actions">
    private class MarkAction extends BasicAction {

        private final TaskTable taskTable;

        public MarkAction(TaskTable taskTable) {
            this(taskTable, false);
        }

        public MarkAction(TaskTable taskTable, boolean showIcon) {
            super("actionMarkTask", showIcon);
            this.taskTable = taskTable;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Task task = taskTable.getTaskTableModel().getTask(taskTable.getSelectedRow());
            task.setProperty(Task.Completed, !Boolean.TRUE.equals(task.getProperty(Task.Completed)));
            taskTable.getTaskTableModel().fireTableDataChanged();
        }

        @Override
        public void validate() {
            setEnabled(taskTable.getSelectedRowCount() > 0);
        }
    }

    private class ChangePreferencesAction extends BasicAction {

        public ChangePreferencesAction() {
            super("actionPreferences", false);
        }

        public ChangePreferencesAction(boolean showIcon) {
            super("actionPreferences", showIcon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PreferencesDialog.createPreferencesDialog(provider);
        }
    }

    private class ClearAlddDoneTasksAction extends BasicAction {

        private TaskTable table;

        public ClearAlddDoneTasksAction(TaskTable table) {
            this(table, false);
        }

        public ClearAlddDoneTasksAction(TaskTable table, boolean showIcon) {
            super("actionClearTaskTable", showIcon);
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TaskTableModel model = table.getTaskTableModel();
            for (int ix = 0; ix < model.getRowCount(); ix++) {
                Task task = model.getTask(ix);
                if (Boolean.TRUE.equals(task.getProperty(Task.Completed))) {
                    model.removeTask(ix);
                }
            }
        }

        @Override
        public void validate() {
            setEnabled(table.getRowCount() > 0);
        }
    }

    private class RemoveTaskAction extends BasicAction {

        private final TaskTable table;

        public RemoveTaskAction(TaskTable table) {
            this(table, false);
        }

        public RemoveTaskAction(TaskTable table, boolean showIcon) {
            super("actionRemoveTask", showIcon);
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] rows = table.getSelectedRows();
            TaskTableModel model = (TaskTableModel) table.getModel();
            for (int row : rows) {
                model.removeTask(row);
            }
            fire(ACTIVE_REMOVED_PROPERTY, false, true);
        }

        @Override
        public void validate() {
            setEnabled(table.getSelectedRowCount() > 0);
        }
    }

    private class AddUnplannedTaskAction extends BasicAction {

        private final TaskTable table;

        public AddUnplannedTaskAction(TaskTable table) {
            this(table, false);
        }

        public AddUnplannedTaskAction(TaskTable table, boolean showIcon) {
            super("actionUnplanned", showIcon);
            this.table = table;
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

                parentTask.addElement(interrupt);

                model.addTask(unplanned);
                model.fireTableDataChanged();

                fire(INTERRUPTION_ADDED_PROPERTY, null, interrupt);
            }
        }

        @Override
        public void validate() {
            setEnabled(table.getSelectedRowCount() > 0);
        }
    }

    private class AddInteraptionAction extends BasicAction {

        private final TaskTable table;

        public AddInteraptionAction(TaskTable table) {
            this(table, false);
        }

        public AddInteraptionAction(TaskTable table, boolean showIcon) {
            super("actionAddInterruption", showIcon);
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
                parentTask.addElement(interruption);
                model.fireTableDataChanged();
            }
        }

        @Override
        public void validate() {
            setEnabled(table.getSelectedRowCount() > 0);
        }
    }

    private class EditTaskAction extends BasicAction {

        private final TaskTable table;

        public EditTaskAction(TaskTable table) {
            this(table, false);
        }

        public EditTaskAction(TaskTable table, boolean showIcon) {
            super("actionEditTask", showIcon);
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int index = table.getSelectedRow();
            TaskTableModel model = table.getTaskTableModel();
            Task task = model.getTask(index);

            final TaskDialog taskDialog = new TaskDialog(task);
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

    private static class SetTagAction extends AbstractAction implements ValidatableAction {

        private TaskTable table;
        private final Task.Priority priority;

        public SetTagAction(TaskTable table, Task.Priority priority) {
            super(priority != null ? priority.toString() : "Without priority");
            this.table = table;
            this.priority = priority;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TaskTableModel model = table.getTaskTableModel();
            model.getTask(table.getSelectedRow()).setProperty(Task.TaskPriority, priority);
            model.fireTableDataChanged();
        }

        @Override
        public void validate() {
            // do nothiing
        }
    }

    private static class NullAction extends AbstractAction implements ValidatableAction {

        private final TaskTable table;

        public NullAction(TaskTable table, String text) {
            super(text);
            this.table = table;
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

    private class AddTaskAction extends BasicAction {

        private final TaskTable table;

        public AddTaskAction(TaskTable table) {
            this(table, false);
        }

        public AddTaskAction(TaskTable table, boolean showIcon) {
            super("actionAddNewTask", showIcon);
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TaskDialog dialog = new TaskDialog();
            dialog.setVisible(true);
            final Task task = dialog.getResult();
            if (task != null) {
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

    private abstract class BasicAction extends AbstractAction implements ValidatableAction {

        private final KeyStroke stroke;

        public BasicAction(String key, boolean showIcon) {
            super();
            putValue(Action.NAME, getString(key + ".text"));
            putValue(Action.LONG_DESCRIPTION, getString(key + ".description"));
            if (showIcon) {
                putValue(Action.SMALL_ICON, Resources.createIcon(getString(key + ".icon")));
            }
            stroke = KeyStroke.getKeyStroke(getString(key + ".shortcut"));
        }

        @Override
        public void validate() {
            // do nothing
        }

        public final KeyStroke getActionKeyStroke() {
            return stroke;
        }

        private String getString(String key) {
            return NbBundle.getBundle(BasicAction.class).getString(key);
        }
    }
//</editor-fold>
    private void validaActions() {
        for (ValidatableAction a : actionsMap.values()) {
            a.validate();


        }
    }

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

    private static final class PopupListener extends MouseAdapter {

        private final JPopupMenu popup;

        public PopupListener(JPopupMenu popup) {
            this.popup = popup;
        }

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
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private static final class TooltipViewer extends MouseAdapter {

        private final TaskTable table;

        public TooltipViewer(TaskTable table) {
            this.table = table;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p);

            TaskTableModel model = table.getTaskTableModel();
            Task task = (Task) model.getValueAt(row, column);


            table.setToolTipText(task.getProperty(Task.Description));
        }
    }

    private static final class RightClickSelector extends MouseAdapter {

        private final TaskTable table;

        public RightClickSelector(TaskTable table) {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                Point p = e.getPoint();
                int rowNumber = table.rowAtPoint(p);
                ListSelectionModel model = table.getSelectionModel();
                model.setSelectionInterval(rowNumber, rowNumber);
            }
        }
    }
}