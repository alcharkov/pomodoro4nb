
package org.matveev.pomodoro4nb.utils.cursor;

import java.awt.Color;

/**
 *
 * @author Alexey Matvey
 */
public class DummyColorCursor implements Cursor<Color> {

    private final Color color;

    public DummyColorCursor(Color color) {
        this.color = color;
    }

    @Override
    public Color next() {
        return color;
    }
}
