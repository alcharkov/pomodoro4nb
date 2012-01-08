/*
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
package org.matveev.pomodoro4nb.controllers;

import org.matveev.pomodoro4nb.utils.Handler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matveev.pomodoro4nb.data.Property;
import org.matveev.pomodoro4nb.data.PropertyListener;

/**
 *
 * @author Alexey Matvey
 */
public abstract class AbstractController implements Controller {

    private final List<PropertyListener> listeners = new ArrayList<PropertyListener>();
    private final Map<Property<?>, List<Handler>> handlers = new HashMap<Property<?>, List<Handler>>();

    public AbstractController() {
    }

    public void registerHandler(Property<?> property, Handler handler) {
        List<Handler> list = handlers.get(property);
        if (list == null) {
            list = new ArrayList<Handler>();
            handlers.put(property, list);
        }
        list.add(handler);
    }

    public void unregisterHandler(Property<?> property, Handler handler) {
        List<Handler> list = handlers.get(property);
        if (list != null) {
            handlers.remove(property);
        }
    }

    @Override
    public List<Handler> getHandlers(Property<?> property) {
        List<Handler> foundList = handlers.get(property);
        if (foundList != null) {
            return Collections.unmodifiableList(foundList);
        }
        return Collections.emptyList();
    }

    protected void fire(Property<?> property, Object oldValue, Object newValue) {
        for (PropertyListener l : listeners) {
            l.propertyChange(property, oldValue, newValue);
        }
    }

    @Override
    public void addPropertyListener(PropertyListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removePropertyListener(PropertyListener listener) {
        listeners.remove(listener);
    }
}
