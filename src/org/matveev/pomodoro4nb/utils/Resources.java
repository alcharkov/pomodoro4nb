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

import java.awt.Font;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Alexey Matveev
 */
public final class Resources {

    private static final Logger LOGGER = Logger.getLogger(Resources.class.getName());
    
    private static final String IMAGES_PACKAGE = "/org/matveev/pomodoro4nb/resources/images/";
    private static final String FORNTS_PACKAGE = "/org/matveev/pomodoro4nb/resources/fonts/";
    private static final String SOUNDS_PACKAGE = "/org/matveev/pomodoro4nb/resources/sounds/";

    public static Icon createIcon(final String iconName) {
        return new ImageIcon(Resources.class.getResource(IMAGES_PACKAGE + iconName));
    }

    public static Font createFont(final String fontName, final int size, final Font fallback) {
        try {
            InputStream is = Resources.class.getResourceAsStream(FORNTS_PACKAGE + fontName);
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, size);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Can't create font!", ex);
            return fallback;
        }
    }
    
    public static InputStream getSound(String soundName) {
        return Resources.class.getResourceAsStream(SOUNDS_PACKAGE + soundName);
    }
}
