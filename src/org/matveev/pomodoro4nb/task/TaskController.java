package org.matveev.pomodoro4nb.task;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
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
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;
import org.matveev.pomodoro4nb.timer.PomodoroTimer.State;
import org.matveev.pomodoro4nb.timer.TimerController;
import org.matveev.pomodoro4nb.timer.TimerController.StateInfo;
import org.matveev.pomodoro4nb.data.Property;
import org.matveev.pomodoro4nb.data.io.PropertiesSerializer;
import org.matveev.pomodoro4nb.data.io.PropertiesSerializerFactory;
import org.matveev.pomodoro4nb.prefs.DefaultPreferencesProvider;
import org.matveev.pomodoro4nb.task.actions.AddInterruptionAction;
import org.matveev.pomodoro4nb.task.actions.AddTaskAction;
import org.matveev.pomodoro4nb.task.actions.AddUnplannedTaskAction;
import org.matveev.pomodoro4nb.task.actions.BasicAction;
import org.matveev.pomodoro4nb.task.actions.ClearFinishedTaskAction;
import org.matveev.pomodoro4nb.task.actions.EditTaskAction;
import org.matveev.pomodoro4nb.task.actions.MarkTaskAction;
import org.matveev.pomodoro4nb.task.actions.NamedAction;
import org.matveev.pomodoro4nb.task.actions.PreferencesAction;
import org.matveev.pomodoro4nb.task.actions.PriorityAction;
import org.matveev.pomodoro4nb.task.actions.RemoveTaskAction;
import org.matveev.pomodoro4nb.utils.Base64Coder;
import org.matveev.pomodoro4nb.utils.Handler;
import org.matveev.pomodoro4nb.utils.MediaPlayer;
import org.matveev.pomodoro4nb.utils.Resources;

/**
 *
 * @author Alexey Matvey
 */
public class TaskController extends AbstractController {

    public static final String TASK_CONTROLLER_ID = "taskController";
    //
    public static final Property<Boolean> HasTasksProperty = new Property<Boolean>("hasTasks", Boolean.class);
    //
    public static final Property<Task> TaskAddedProperty = new Property<Task>("taskAdded", Task.class);
    public static final Property<Task> TaskRemovedProperty = new Property<Task>("taskRemoved", Task.class);
    public static final Property<Task> ActiveTaskRemovedProperty = new Property<Task>("activeRemoved", Task.class);
    public static final Property<Interruption> InterruptionAddedProperty = new Property<Interruption>("interruptionAdded", Interruption.class);
    //
    private List<ValidatableAction> actions = new CopyOnWriteArrayList<ValidatableAction>();
    //
    private final TaskTable taskTable;
    private final JPopupMenu popupMenu;
    //
    private final PreferencesProvider provider;
    //
    private Task currentTask;

    public TaskController(PreferencesProvider provider) {
        this.provider = provider;

        //<editor-fold defaultstate="collapsed" desc="Create components">
        taskTable = new TaskTable();
        ActionValidator validator = new ActionValidator();
        taskTable.getSelectionModel().addListSelectionListener(validator);
        taskTable.getModel().addTableModelListener(validator);
        taskTable.addMouseListener(new DescriptionViewer(taskTable));
        taskTable.addMouseListener(new RightClickSelector(taskTable));

        popupMenu = createTaskTablePopupMenu();
        PopupListener listener = new PopupListener(popupMenu);
        taskTable.addMouseListener(listener);
        taskTable.getTableHeader().addMouseListener(listener);
        //</editor-fold>


        registerHandler(TimerController.STATE_CHANGED_PROPERTY, new Handler<StateInfo>() {

            @Override
            public void handle(StateInfo oldState, StateInfo newState) {
                if (newState != null && newState.isForced()) {
                    if (State.WORK.equals(newState.getState())) {
                        int index = taskTable.getSelectedRow();
                        if (index != -1) {
                            currentTask = taskTable.getTaskTableModel().getTask(index);
                        }
                    } 
                } else if (newState != null && !newState.isForced()) {
                    if (State.IDLE.equals(newState.getState())) {
                        Notificator.showNotificationBalloon(Notificator.KEY_START_WORK);
                        tryPlaySound();
                    } else if (State.BREAK.equals(newState.getState())) {
                        if (currentTask != null) {
                            final Integer pomodoros = currentTask.getProperty(Task.Pomodoros);
                            currentTask.setProperty(Task.Pomodoros, new Integer(pomodoros + 1));
                            taskTable.getTaskTableModel().fireTableDataChanged();
                        }
                        Notificator.showNotificationBalloon(Notificator.KEY_START_BREAK);
                        tryPlaySound();
                    }
                }
            }
        });
    }

    private void tryPlaySound() {
        if (Boolean.TRUE.equals(provider.getBoolean(
                DefaultPreferencesProvider.ENABLE_SOUNDS_KEY, false))) {
            MediaPlayer.play(Resources.getSound("budzik.wav"));
        }
    }

    public Container createQuickActionPanel() {
        final TapPanel panel = new TapPanel();
        panel.setOrientation(TapPanel.DOWN);
        panel.setPreferredSize(new Dimension(panel.getWidth(), 20));

        final JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        tools.setRollover(true);

        addAction(tools, new AddTaskAction(taskTable));
        addAction(tools, new RemoveTaskAction(taskTable, this));
        addAction(tools, new EditTaskAction(taskTable));
        tools.addSeparator();
        addAction(tools, new MarkTaskAction(taskTable));
        tools.addSeparator();
        addAction(tools, new ClearFinishedTaskAction(taskTable));
        addAction(tools, new PreferencesAction(provider));

        panel.add(tools);
        panel.add(new JPanel());

        validateAcions();
        
        return panel;
    }

    private JPopupMenu createTaskTablePopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        addAction(menu, new AddTaskAction(taskTable));
        addAction(menu, new RemoveTaskAction(taskTable, this));
        addAction(menu, new EditTaskAction(taskTable));
        menu.addSeparator();
        addAction(menu, new MarkTaskAction(taskTable));
        menu.addSeparator();
        menu.add(createTagsSubMenu());
        menu.addSeparator();
        addAction(menu, new AddInterruptionAction(taskTable));
        addAction(menu, new AddUnplannedTaskAction(taskTable, this));
        menu.addSeparator();
        addAction(menu, new ClearFinishedTaskAction(taskTable));
        menu.addSeparator();
        addAction(menu, new PreferencesAction(provider));

        validateAcions();

        return menu;
    }

    private JMenu createTagsSubMenu() {
        JMenu tagsMenu = new JMenu(new NamedAction(taskTable, "Set tag for task"));

        ValidatableAction a = (ValidatableAction) tagsMenu.getAction();
        actions.add(a);

        tagsMenu.add(new JMenuItem(new PriorityAction(taskTable, Task.Priority.Improvements)));
        tagsMenu.add(new JMenuItem(new PriorityAction(taskTable, Task.Priority.Blocker)));
        tagsMenu.add(new JMenuItem(new PriorityAction(taskTable, Task.Priority.Critical)));
        tagsMenu.add(new JMenuItem(new PriorityAction(taskTable, Task.Priority.Major)));
        tagsMenu.add(new JMenuItem(new PriorityAction(taskTable, Task.Priority.Minor)));
        tagsMenu.add(new JMenuItem(new PriorityAction(taskTable, Task.Priority.Trivial)));
        tagsMenu.addSeparator();
        tagsMenu.add(new JMenuItem(new PriorityAction(taskTable, null)));

        return tagsMenu;
    }

    private void addAction(JToolBar toolbar, ValidatableAction action) {
        actions.add(action);

        JButton btn = new JButton(action);
        btn.setText(null);
        toolbar.add(btn);
    }

    private void addAction(JPopupMenu menu, ValidatableAction action) {
        actions.add(action);

        JMenuItem item = new JMenuItem(action);
        item.setIcon(null);
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
        for (Action action : actions) {
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
        final Activity activity = taskTable.getTaskTableModel().getActivity();
        final String data = serializer.serialize(activity);
        props.setProperty("model", Base64Coder.encodeString(data));
    }

    @Override
    public void restore(java.util.Properties props) throws Exception {
        Object data = props.getProperty("model");
        if (data != null) {
            final PropertiesSerializer serializer = PropertiesSerializerFactory.createXMLSerializer();
            final String xmlString = Base64Coder.decodeString((String) data);
            Activity activity = (Activity) serializer.deserealize(xmlString.trim());
            taskTable.setModel(new TaskTableModel(activity != null ? activity : new Activity()));
        }
    }

    public Task getCurretTask() {
        return currentTask;
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

    private void validateAcions() {
        for (ValidatableAction a : actions) {
            a.validate();
        }
    }

    private final class ActionValidator implements TableModelListener, ListSelectionListener {

        public ActionValidator() {
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            validateAcions();
            updateProperties(e);
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            validateAcions();
        }

        private void updateProperties(final EventObject event) {
            if (event.getSource() instanceof JTable) {
                putProperty(TaskController.HasTasksProperty,
                        ((JTable) event.getSource()).getRowCount() > 0);
            }
        }
    }

    private static final class DescriptionViewer extends MouseAdapter {

        private final TaskTable table;

        public DescriptionViewer(TaskTable table) {
            this.table = table;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            final Point p = e.getPoint();
            final TaskTableModel model = table.getTaskTableModel();
            Task task = (Task) model.getValueAt(table.rowAtPoint(p), table.columnAtPoint(p));
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
                final int rowNumber = table.rowAtPoint(e.getPoint());
                final ListSelectionModel model = table.getSelectionModel();
                model.setSelectionInterval(rowNumber, rowNumber);
            }
        }
    }
}