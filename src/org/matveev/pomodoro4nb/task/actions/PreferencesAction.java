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

import java.awt.event.ActionEvent;
import org.matveev.pomodoro4nb.prefs.PreferencesDialog;
import org.matveev.pomodoro4nb.prefs.PreferencesProvider;

/**
 *
 * @author Alexey Matveev
 */
public class PreferencesAction extends BasicAction {

    private final PreferencesProvider provider;

    public PreferencesAction(PreferencesProvider provider) {
        super("actionPreferences");
        this.provider = provider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PreferencesDialog.createPreferencesDialog(provider);
    }
}
