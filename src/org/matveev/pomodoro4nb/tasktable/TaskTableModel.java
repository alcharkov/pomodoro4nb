package org.matveev.pomodoro4nb.tasktable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public class TaskTableModel extends AbstractTableModel {

    public enum Header {

        //COMPLETED("", Boolean.class),
        DESCRIPTION(getString("columnDescription.title"), String.class),
        ESTIMATE(getString("columnEstimate.title"), Integer.class),
        POMODOROS(getString("columnPomodoros.title"), Integer.class),
        INTERAPTIONS(getString("columnInteraptions.title"), Integer.class),
        UNPLANED(getString("columnUnplaned.title"), Integer.class);
        
        private final String title;
        private final Class type;

        private Header(String title, Class type) {
            this.title = title;
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public Class getType() {
            return type;
        }
    }
    
    private List<Task> taskList = new ArrayList<Task>();

    public TaskTableModel() {
    }

    @Override
    public int getRowCount() {
        return taskList.size();
    }

    @Override
    public int getColumnCount() {
        return Header.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return Header.values()[column].getTitle();
    }

    @Override
    public Class getColumnClass(int column) {
        return Header.values()[column].getType();
    }

    @Override
    public Object getValueAt(int row, int column) {
        final Task task = taskList.get(row);
        switch (Header.values()[column]) {
            //case COMPLETED:
            //    return task.isCompleted();
            case DESCRIPTION:
                return task.getDescription();
            case ESTIMATE:
                return task.getEstimate();
            case POMODOROS:
                return task.getPomodoros();
            case INTERAPTIONS:
                return task.getInteraptions();
            case UNPLANED:
                return task.getUnplaned();
        }
        return null;
    }

    public List<Task> getTaskList() {
        return Collections.unmodifiableList(taskList);
    }
    
    public Task getTask(int index) {
        return taskList.get(index);
    }
    
    public void moveTask(int fromIndex, int toIndex) {
        taskList.add(toIndex, taskList.remove(fromIndex));
        fireTableDataChanged();
    }
    
    public void addTask(final Task task) {
        taskList.add(task);
        fireTableDataChanged();
    }
    
    public void removeTask(int ix) {
        taskList.remove(ix);
        fireTableDataChanged();
    }
    
    public void removeTask(final Task task) {
        taskList.remove(task);
        fireTableDataChanged();
    }
    
    public void removeAllTasks() {
        taskList.clear();
        fireTableDataChanged();
    }
    
    private static String getString(final String key) {
        return NbBundle.getMessage(TaskTableModel.class, key);
    }
}
