package org.matveev.pomodoro4nb.task;

import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 *
 * @author Alexey Matveev
 */
public class TaskTableTagFilter extends RowFilter<TaskTableModel, Integer> {

    @Override
    public boolean include(Entry<? extends TaskTableModel, ? extends Integer> entry) {
        // TODO: Added filtering by tag...
        return true;
    }
    
}
