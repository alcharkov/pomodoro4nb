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

import java.awt.Color;

/**
 *
 * @author Alexey Matveev
 */
public class SequentialColorCursor implements Cursor<Color> {

    private final float startHue;
    private final float endHue;
    private final float saturation;
    private final float brightness;
    private final float step;
    private float currentHue;

    public SequentialColorCursor(float startHue, float endHue, float saturation, float brightness, float step) {
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
        if (step < 0 ? currentHue <= endHue : currentHue >= endHue) {
            currentHue = startHue;
        }
        return Color.getHSBColor((currentHue += step) / 360F, saturation, brightness);
    }
}
