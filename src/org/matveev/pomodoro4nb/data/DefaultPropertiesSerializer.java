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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.matveev.pomodoro4nb.utils.json.JSONException;
import org.matveev.pomodoro4nb.utils.json.JSONObject;

/**
 *
 * @author Alexey Matveev
 */
public class DefaultPropertiesSerializer implements PropertiesSerializer {

    private static final Logger LOGGER = Logger.getLogger(DefaultPropertiesSerializer.class.getName());

    @Override
    public String write(Properties props) {
        final JSONObject obj = new JSONObject();
        for (Property<?> p : props.getProperties()) {
            try {
                obj.put(p.getName(), props.getProperty(p));
            } catch (JSONException ex) {
                LOGGER.log(Level.SEVERE, "", ex);
            }
        }
        return obj.toString();
    }

    @Override
    public Properties read(Class<? extends Properties> type, String jsonString) {
        try {
            final Properties props = new Properties();
            final JSONObject parsedMap = new JSONObject(jsonString);
            for (Field field : type.getDeclaredFields()) {
                final Property<?> property = extractPropertyFrom(field);
                if (property != null) {
                    if (parsedMap.has(property.getName())) {
                        final Object value = parsedMap.get(property.getName());
                        Object result = null;
                        if (property.getType().isAssignableFrom(Number.class)
                                || property.getType().equals(Boolean.class)) {
                            result = value;
                        } else if (property.getType().isEnum()) {
                            result = Enum.valueOf((Class<Enum>) property.getType(), (String) value);
                        } else if (UUID.class.equals(property.getType())) {
                            result = UUID.fromString((String) value);
                        }
                        if (result != null) {
                            props.setProperty(property, property.getType().cast(value));
                        }
                    }
                }
            }
            return props;
        } catch (JSONException ex) {
            LOGGER.log(Level.SEVERE, "", ex);
        }
        return null;
    }

    private static Property<?> extractPropertyFrom(Field field) {
        if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(Property.class)) {
            try {
                return (Property<?>) field.get(null);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.WARNING, "Cannot extract property from field", ex);
            } catch (IllegalAccessException ex) {
                LOGGER.log(Level.WARNING, "Cannot extract property from field", ex);
            }
        }
        return null;
    }
}