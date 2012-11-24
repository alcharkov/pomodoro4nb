/*
 * Copyright (C) 2011-2012 Alexey Matveev <mvaleksej@gmail.com>
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
package org.matveev.pomodoro4nb.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.matveev.pomodoro4nb.core.data.Properties;
import org.matveev.pomodoro4nb.core.data.Property;
import org.matveev.pomodoro4nb.utils.Utils;

/**
 *
 * @author Alexey Matveev
 */
public class DomainObject extends Properties {

    public static final Property<UUID> Id = new Property<UUID>("id", UUID.class);
    public static final Property<String> SerializeKey = new Property<String>("key", String.class);
    public static final Property<String> ClassType = new Property<String>("class", String.class);
    public static final Property<UUID> Parent = new Property<UUID>("parent", UUID.class);
    public static final Property<ChildrenList> Children = new Property<ChildrenList>("children", ChildrenList.class);

    public DomainObject() {
        setProperty(Id, UUID.randomUUID());
        setProperty(ClassType, getClass().getName());
        setProperty(SerializeKey, getClass().getSimpleName());
        setProperty(Children, new ChildrenList());
    }

    private void checkArguments(DomainObject object) {
        if (object == null) {
            throw new IllegalArgumentException("This container cannot contain 'null' elements");
        }
        final Children children = getClass().getAnnotation(Children.class);
        if (children == null || check(children.types(), object.getClass(), children.allowSubtypes())) {
            throw new IllegalArgumentException(String.format(
                    "The container '%s' cannot contain elements with type '%s'", getClass().getName(), object
                    .getClass().getName()));
        }
    }

    private boolean check(Class<? extends org.matveev.pomodoro4nb.core.data.Properties>[] values, Class<? extends org.matveev.pomodoro4nb.core.data.Properties> type, boolean subtypes) {
        if (subtypes) {
            for (Class<? extends org.matveev.pomodoro4nb.core.data.Properties> parent : values) {
                if (parent.isAssignableFrom(type)) {
                    return false;
                }
            }
        }
        return !Arrays.asList(values).contains(type);
    }

    public List<DomainObject> getChildren(Class<? extends DomainObject>... types) {
        final List<DomainObject> result = new ArrayList<DomainObject>();
        for (DomainObject p : getChildren()) {
            final String name = p.getProperty(DomainObject.ClassType);
            if (name != null && Utils.isContainProperty(name, types)) {
                result.add(p);
            }
        }
        return result;
    }

    public void add(DomainObject e) {
        checkArguments(e);
        getChildren().add(e);
    }

    public void insert(int index, DomainObject e) {
        checkArguments(e);
        getChildren().add(index, e);
    }

    public void remove(int index) {
        getChildren().remove(index);
    }

    public void remove(DomainObject e) {
        checkArguments(e);
        getChildren().remove(e);
    }

    public List<DomainObject> getChildren() {
        return getProperty(Children);
    }
    
    public static final class ChildrenList extends CopyOnWriteArrayList<DomainObject> {
        
    }
    
}
