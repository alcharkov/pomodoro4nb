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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Alexey Matveev
 */
public class RowIndexTransferable implements Transferable, Serializable {

    private final DataFlavor dataFlavor;

    public RowIndexTransferable(int fromRowIndex) {
        dataFlavor = new DataFlavor(Integer.class, "FromRowIndex");
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{ dataFlavor };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == dataFlavor;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this;
    }
}
