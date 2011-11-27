package org.matveev.pomodoro4nb.utils.cursor;

import java.awt.Color;

/**
 *
 * @author Alexey Matvey
 */
public class ColorCursor implements Cursor<Color> {

    private final float startHue;
    private final float endHue;
    private final float saturation;
    private final float brightness;
    private final float step;
    
    private float currentHue;
    
    public ColorCursor(float startHue, float endHue, float saturation, float brightness, float step) {
        this.startHue = startHue;
        this.endHue = endHue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.step = step;
        
        currentHue = startHue;
    }
    
    @Override
    public Color next() {
        currentHue += step;
        if(step < 0 ? currentHue <= endHue : currentHue >= endHue) {
            currentHue = startHue;
        }
        return Color.getHSBColor((currentHue += step) / 360f, saturation, brightness);
    }
}
