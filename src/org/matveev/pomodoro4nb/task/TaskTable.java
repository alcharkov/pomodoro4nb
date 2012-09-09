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
package org.matveev.pomodoro4nb.task;

import org.matveev.pomodoro4nb.storage.Storage;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.matveev.pomodoro4nb.task.Task.Priority;
import org.matveev.pomodoro4nb.task.Task.Status;
import org.matveev.pomodoro4nb.utils.Resources;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public class TaskTable extends JTable {

    private static final String TOOLTIP_TEXT_PATTERN =
            NbBundle.getMessage(TaskTable.class, "tooltipTextPattern");
    private final TableCellRenderer renderer;

    public TaskTable() {
        super(new TaskTableModel(new Storage()));
        renderer = new AlignmentTableCellRenderer();
        setUI(new TaskTableDragAndDropUI());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setResizingAllowed(false);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return renderer;
    }

//    @Override
//    public String getToolTipText(MouseEvent e) {
//        Task task = getTaskTableModel().getTask(rowAtPoint(e.getPoint()));
//        return String.format(TOOLTIP_TEXT_PATTERN,
//                task.getProperty(Task.Description),
//                task.getProperty(Task.Pomodoros),
//                task.getProperty(Task.Estimate));
//    }
    public TaskTableModel getTaskTableModel() {
        return (TaskTableModel) getModel();
    }

    void selectNextRow(int nextIndex) {
        getSelectionModel().setSelectionInterval(nextIndex, nextIndex);
    }

    private static class AlignmentTableCellRenderer extends DefaultTableCellRenderer {

        private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 5, 0, 0);
        private boolean isCompleted;
        private Priority priority;
        private Status status;

        /*
         * package
         */ AlignmentTableCellRenderer() {
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(EMPTY_BORDER);
            setHorizontalAlignment(SwingConstants.LEFT);

            Task task = ((TaskTable) table).getTaskTableModel().getTask(row);
            isCompleted = Boolean.TRUE.equals(task.getProperty(Task.Completed));
            priority = task.getProperty(Task.TaskPriority);
            status = task.getProperty(Task.TaskStatus);
            setIcon(status != null && column == 0 ? status.icon : null);
            return this;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackgroundColorForTaskTag());
            g2d.fillRect(0, 0, getWidth(), getHeight());

            super.paint(g);
            if (isCompleted) {
                Icon icon = getIcon();
                g2d.drawLine(icon == null ? 0 : icon.getIconWidth() + getInsets().right + getInsets().left + 5, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        }

        private Color getBackgroundColorForTaskTag() {
            return priority == null ? Color.WHITE : priority.color;
        }
    }
}
