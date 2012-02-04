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
package org.matveev.pomodoro4nb.task.actions;

import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.matveev.pomodoro4nb.utils.Resources;
import org.matveev.pomodoro4nb.utils.ValidatableAction;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexey Matveev
 */
public abstract class BasicAction extends AbstractAction implements ValidatableAction {

    private final KeyStroke stroke;

    public BasicAction(String key) {
        super();
        putValue(Action.NAME, getString(key + ".text"));
        putValue(Action.LONG_DESCRIPTION, getString(key + ".description"));
        putValue(Action.SMALL_ICON, Resources.createIcon(getString(key + ".icon")));
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
        final ResourceBundle bundle = NbBundle.getBundle(BasicAction.class);
        return bundle.containsKey(key) ? bundle.getString(key) : null;
    }
}