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
package org.matveev.pomodoro4nb.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.matveev.pomodoro4nb.utils.Utils;

/**
 * 
 * @author Alexey Matvey
 */
public class Properties {

    public static final Property<UUID> Id = new Property<UUID>("id", UUID.class);
    public static final Property<UUID> Parent = new Property<UUID>("parent", UUID.class);
    public static final Property<String> SerializeKey = new Property<String>("key", String.class);
    public static final Property<String> ClassType = new Property<String>("class", String.class);
    
    private final Map<Property<?>, Object> holder;
    private final List<Properties> elements;
    private final List<PropertyListener> listeners;

    public Properties() {
        holder = new LinkedHashMap<Property<?>, Object>();
        elements = new CopyOnWriteArrayList<Properties>();
        listeners = new CopyOnWriteArrayList<PropertyListener>();

        setProperty(Id, UUID.randomUUID());
        setProperty(ClassType, getClass().getName());
        setProperty(SerializeKey, getClass().getSimpleName());
    }

    public final <T> T getProperty(Property<T> property) {
        return property.getType().cast(holder.get(property));
    }

    public final <T> T getProperty(Property<T> property, T fallback) {
        T value = (T) holder.get(property);
        return value != null ? property.getType().cast(value) : fallback;
    }

    public final <T, V> void setProperty(Property<T> property, V value) {
        if (value != null && !property.getType().equals(value.getClass())) {
            throw new IllegalArgumentException(
                    "Cannot set value with type '" + value.getClass() 
                    + "' to property with type '" + property.getType() + "'");
        }
        
        V oldValue = (V) holder.get(property);
        if (value != oldValue) {
            holder.put(property, value);
            fire(property, oldValue, value);
        }
    }

    public Set<Property<?>> getProperties() {
        return holder.keySet();
    }

    public void addElement(Properties e) {
        checkArguments(e);
        elements.add(e);
    }
    
    public void addElement(int index, Properties e) {
        checkArguments(e);
        elements.add(index, e);
    }

    public void removeElement(int index) {
        elements.remove(index);
    }
    
    public void removeElement(Properties e) {
        checkArguments(e);
        elements.remove(e);
    }
    
    public void removeElements() {
        elements.clear();
    }

    private void checkArguments(Properties element) {
        if (element == null) {
            throw new IllegalArgumentException("This container cannot contain 'null' elements");
        }
        final Children children = getClass().getAnnotation(Children.class);
        if (children == null || !Arrays.asList(children.value()).contains(element.getClass())) {
            throw new IllegalArgumentException("The container '" + getClass().getName()
                    + "' cannot contain elements with type '" + element.getClass().getName() + "'");
        }
    }

    public List<Properties> getElements(Class<? extends Properties>... types) {
        final List<Properties> result = new ArrayList<Properties>();
        for (Properties p : getElements()) {
            final String name = p.getProperty(Properties.ClassType);
            if (name != null && Utils.isContains(name, types)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Properties> getElements() {
        return Collections.unmodifiableList(elements);
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
        for (Map.Entry<Property<?>, Object> entry : holder.entrySet()) {
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
