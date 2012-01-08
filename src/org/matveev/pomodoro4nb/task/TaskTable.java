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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.matveev.pomodoro4nb.task.Task.Priority;
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
        super(new TaskTableModel());
        renderer = new AlignmentTableCellRenderer();
        setUI(new TaskTableDragAndDropUI());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setResizingAllowed(false);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return renderer;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Task task = getTaskTableModel().getTask(rowAtPoint(e.getPoint()));
        return String.format(TOOLTIP_TEXT_PATTERN,
                task.getProperty(Task.Description),
                task.getProperty(Task.Pomodoros),
                task.getProperty(Task.Estimate));
    }

    public TaskTableModel getTaskTableModel() {
        return (TaskTableModel) getModel();
    }

    void selectNextRow(int nextIndex) {
        getSelectionModel().setSelectionInterval(nextIndex, nextIndex);
    }

    private static class AlignmentTableCellRenderer extends DefaultTableCellRenderer {

        private static final Map<Task.Priority, Color> colorsMap =
                new EnumMap<Task.Priority, Color>(Task.Priority.class);

        static {
            colorsMap.put(Priority.Improvements, new Color(233, 239, 242));
            colorsMap.put(Priority.Critical, new Color(252, 226, 217));
            colorsMap.put(Priority.Major, new Color(252, 244, 217));
            colorsMap.put(Priority.Minor, new Color(237, 252, 217));
        }
        private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 5, 0, 0);
        private boolean isCompleted;
        private Task.Priority taskPriority;

        /*package*/ AlignmentTableCellRenderer() {
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(EMPTY_BORDER);
            setHorizontalAlignment(SwingConstants.LEFT);

            Task task = ((TaskTable) table).getTaskTableModel().getTask(row);
            isCompleted = Boolean.TRUE.equals(task.getProperty(Task.Completed));
            taskPriority = task.getProperty(Task.TaskPriority);
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
                g2d.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        }

        private Color getBackgroundColorForTaskTag() {
            Color color = Color.WHITE;
            if (taskPriority != null) {
                color = colorsMap.get(taskPriority);
            }
            return color;
        }
    }
}
