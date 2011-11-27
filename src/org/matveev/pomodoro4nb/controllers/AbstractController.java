package org.matveev.pomodoro4nb.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matveev.pomodoro4nb.utils.data.Property;
import org.matveev.pomodoro4nb.utils.data.PropertyListener;

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
