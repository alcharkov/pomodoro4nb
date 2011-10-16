package org.matveev.pomodoro4nb;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Alexey Matveev
 */
public class Utils {
    
    private static final String IMAGES_PACKAGE = "/org/matveev/pomodoro4nb/resources/images/";

    public static Icon createIcon(final String iconName) {
        return new ImageIcon(Utils.class.getResource(IMAGES_PACKAGE + iconName));
    }
}
