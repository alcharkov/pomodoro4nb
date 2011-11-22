package org.matveev.pomodoro4nb.tasktable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import org.matveev.pomodoro4nb.tasktable.Task.Tag;

/**
 *
 * @author Alexey Matveev
 */
public class TaskTable extends JTable {

    private final TableCellRenderer renderer;

    public TaskTable() {
        super(new TaskTableModel());

        addMouseListener(new RightClickSelector(this));
        
        TableRowSorter<TaskTableModel> sorter =
                new TableRowSorter<TaskTableModel>((TaskTableModel) getModel());
        sorter.setRowFilter(new TaskTableTagFilter());
        setRowSorter(sorter);

        this.renderer = new AlignmentTableCellRenderer();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setResizingAllowed(false);
        setUI(new DragAndDropUI());
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return renderer;
    }

    public TaskTableModel getTaskTableModel() {
        return (TaskTableModel) getModel();
    }

    void selectNextRow(int nextIndex) {
        getSelectionModel().setSelectionInterval(nextIndex, nextIndex);
    }

    private static class AlignmentTableCellRenderer extends DefaultTableCellRenderer {

        private static final Map<Task.Tag, Color> colorsMap = new EnumMap<Task.Tag, Color>(Task.Tag.class);

        static {
            colorsMap.put(Tag.Improvements, new Color(233, 239, 242));
            colorsMap.put(Tag.Critical, new Color(252, 226, 217));
            colorsMap.put(Tag.Major, new Color(252, 244, 217));
            colorsMap.put(Tag.Minor, new Color(237, 252, 217));
        }
        private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 5, 0, 0);
        private boolean isCompleted;
        private Tag taskTag;

        /*package*/ AlignmentTableCellRenderer() {
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(EMPTY_BORDER);
            setHorizontalAlignment(SwingConstants.LEFT);
            
            final Task task = ((TaskTable) table).getTaskTableModel().getTask(
                    table.convertRowIndexToModel(row));
            
            taskTag = task.getTag();
            isCompleted = task.isCompleted();
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
            if (taskTag != null) {
                color = colorsMap.get(taskTag);
            }
            return color;
        }
    }

    private static class DragAndDropUI extends BasicTableUI {

        private boolean isDragging;
        private int startPoint;
        private int offsetY;

        /*package*/ DragAndDropUI() {
        }

        @Override
        protected MouseInputListener createMouseInputListener() {
            return new DragDropRowMouseInputHandler();
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);

            if (isDragging) {
                g.setColor(table.getParent().getBackground());
                Rectangle cellRect = table.getCellRect(table.getSelectedRow(), 0, false);
                g.copyArea(cellRect.x, cellRect.y, table.getWidth(), table.getRowHeight(), cellRect.x, offsetY);

                if (offsetY < 0) {
                    g.fillRect(cellRect.x, cellRect.y + (table.getRowHeight() + offsetY), table.getWidth(), (offsetY * -1));
                } else {
                    g.fillRect(cellRect.x, cellRect.y, table.getWidth(), offsetY);
                }
            }
        }

        private class DragDropRowMouseInputHandler extends MouseInputHandler {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                startPoint = (int) e.getPoint().getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int fromIndex = table.getSelectedRow();

                if (fromIndex >= 0) {
                    isDragging = true;

                    int rowHeight = table.getRowHeight();
                    int middleOfSelectedRow = (rowHeight * fromIndex) + (rowHeight / 2);

                    int toIndex = -1;
                    int yMousePoint = (int) e.getPoint().getY();

                    if (yMousePoint < (middleOfSelectedRow - rowHeight)) {
                        toIndex = fromIndex - 1;
                    } else if (yMousePoint > (middleOfSelectedRow + rowHeight)) {
                        toIndex = fromIndex + 1;
                    }

                    if (toIndex >= 0 && toIndex < table.getRowCount()) {
                        final TaskTable taskTable = (TaskTable) table;
                        TaskTableModel model = taskTable.getTaskTableModel();
                        for (int i = 0; i < model.getColumnCount(); i++) {
                            model.moveTask(fromIndex, toIndex);
                        }
                        taskTable.selectNextRow(toIndex);
                        startPoint = yMousePoint;
                    }

                    offsetY = (startPoint - yMousePoint) * -1;
                    table.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                isDragging = false;
                table.repaint();
            }
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
