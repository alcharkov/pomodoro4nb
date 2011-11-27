package org.matveev.pomodoro4nb.utils;

import java.awt.Font;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Alexey Matveev
 */
public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
    private static final String IMAGES_PACKAGE = "/org/matveev/pomodoro4nb/resources/images/";
    private static final String FORNTS_PACKAGE = "/org/matveev/pomodoro4nb/resources/fonts/";
    private static final String SOUNDS_PACKAGE = "/org/matveev/pomodoro4nb/resources/sounds/";

    public static Icon createIcon(final String iconName) {
        return new ImageIcon(Utils.class.getResource(IMAGES_PACKAGE + iconName));
    }

    public static Font createFont(final String fontName, final int size, final Font fallback) {
        try {
            InputStream is = Utils.class.getResourceAsStream(FORNTS_PACKAGE + fontName);
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, size);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Can't create font!", ex);
            return fallback;
        }
    }

    public static void playSound(final String soundName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(
                            Utils.class.getResourceAsStream(SOUNDS_PACKAGE + soundName)));
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}
