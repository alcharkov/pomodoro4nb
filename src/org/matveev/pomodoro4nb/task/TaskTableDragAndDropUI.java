/*
 * Pomodoro4NB - Netbeans plugin for work with The Pomodoro Technique
 * Copyright (C) 2011 Alexey Matveev <mvaleksej@gmail.com>
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

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;

/**
 *
 * @author Alexey Matveev
 */
/*package*/ class TaskTableDragAndDropUI extends BasicTableUI {

    private boolean isDragging;
    private int startPoint;
    private int offsetY;

    /*package*/ TaskTableDragAndDropUI() {
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
                g.fillRect(cellRect.x, cellRect.y + (table.getRowHeight() + offsetY), table.getWidth(), offsetY * -1);
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
            table.setCursor(DragSource.DefaultMoveDrop);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            table.setCursor(table.getBounds().contains(e.getPoint())
                    ? DragSource.DefaultMoveDrop
                    : DragSource.DefaultMoveNoDrop);

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
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            table.repaint();
        }
    }
}
