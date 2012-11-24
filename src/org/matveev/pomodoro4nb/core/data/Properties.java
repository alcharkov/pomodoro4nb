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
package org.matveev.pomodoro4nb.core.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author Alexey Matveev
 */
public class Properties {

    private final Map<Property<?>, Object> properties;
    private final List<PropertyListener> listeners;

    public Properties() {
        properties = new LinkedHashMap<Property<?>, Object>();
        listeners = new CopyOnWriteArrayList<PropertyListener>();
    }

    public final <T> T getProperty(Property<T> property) {
        return property.getType().cast(properties.get(property));
    }

    public final <T> T getProperty(Property<T> property, T fallback) {
        T value = (T) properties.get(property);
        return value != null ? property.getType().cast(value) : fallback;
    }

    public final <T, V> void setProperty(Property<T> property, V value) {
        if (value != null && !property.getType().equals(value.getClass())) {
            throw new IllegalArgumentException(
                    "Cannot set value with type '" + value.getClass() 
                    + "' to property with type '" + property.getType() + "'");
        }
        
        final V oldValue = (V) properties.get(property);
        if (value != oldValue) {
            properties.put(property, value);
            fire(property, oldValue, value);
        }
    }

    public Set<Property<?>> getProperties() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    protected void fire(Property<?> property, Object oldValue, Object newValue) {
        for (PropertyListener l : listeners) {
            l.propertyChange(property, oldValue, newValue);
        }
    }

    public void addPropertyListener(PropertyListener listener) {
        listeners.add(listener);
    }

    public void removePropertyListener(PropertyListener listener) {
        listeners.remove(listener);
    }

    @Override
    public String toString() {
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (Map.Entry<Property<?>, Object> entry : properties.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(entry.getKey().getName());
            builder.append('=');
            builder.append(entry.getValue());
        }
        builder.append('}');
        return builder.toString();
    }
}
