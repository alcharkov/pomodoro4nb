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
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 *
 * @author Alexey Matveev
 */
public class TaskTransferHandler extends TransferHandler {

    private JTable table = null;

    public TaskTransferHandler(JTable table) {
        this.table = table;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == table);
        return new RowIndexTransferable(table.getSelectedRow());
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        boolean b = info.getComponent() == table && info.isDrop();
        table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return b;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        JTable target = (JTable) info.getComponent();
        JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
        int index = dl.getRow();
        int max = table.getModel().getRowCount();
        if (index < 0 || index >= max) {
            index = max;
        }
        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        try {
            Integer rowFrom = (Integer) info.getTransferable().getTransferData(info.getDataFlavors()[0]);
            if (rowFrom != -1 && rowFrom != index) {
                ((TaskTableModel) table.getModel()).moveTask(rowFrom, index);
                if (index > rowFrom) {
                    index--;
                }
                target.getSelectionModel().addSelectionInterval(index, index);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        if (act == TransferHandler.MOVE) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
