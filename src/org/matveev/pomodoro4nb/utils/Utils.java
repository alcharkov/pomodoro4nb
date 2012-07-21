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
package org.matveev.pomodoro4nb.utils;

import java.util.Arrays;
import java.util.List;
import org.matveev.pomodoro4nb.data.Properties;
import org.matveev.pomodoro4nb.data.Property;

/**
 *
 * @author Alexey Matveev
 */
public class Utils {

    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean isWindows() {
        return isSpecifiedPlatform("win");
    }

    public static boolean isMac() {
        return isSpecifiedPlatform("mac");
    }

    public static boolean isUnix() {
        return isSpecifiedPlatform("nix", "nux");
    }

    private static boolean isSpecifiedPlatform(String... suffixes) {
        final String os = System.getProperty("os.name").toLowerCase();
        if (Utils.isNotEmpty(os)) {
            final List<String> suffixesList = Arrays.asList(suffixes);
            for (String s : suffixesList) {
                if (os.indexOf(s) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isContains(String name, Class<? extends Properties>[] types) {
        for (Class<? extends Properties> type : types) {
            if (type.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPropertiesEqual(Properties first, Properties second, Property<?> property) {
        if (first == null || second == null) {
            return false;
        }
        final Object objFirst = first.getProperty(property);
        final Object objSecond = second.getProperty(property);
        return objFirst != null && objSecond != null && objFirst.equals(objSecond);
    }
}
