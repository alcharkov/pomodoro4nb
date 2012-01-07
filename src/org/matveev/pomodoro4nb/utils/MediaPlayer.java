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

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author Alexey Matveev
 */
public final class MediaPlayer {

    public static final Logger LOGGER = Logger.getLogger(MediaPlayer.class.getName());

    public static void play(final InputStream stream) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(stream));
                    clip.start();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Cannot play media file", e);
                }
            }
        });
    }
}
