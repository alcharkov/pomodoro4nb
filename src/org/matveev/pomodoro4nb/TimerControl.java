package org.matveev.pomodoro4nb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author Alexey Matveev
 */
public class TimerControl extends JLabel {

    public static final String PROGRESS_PROPERTY = "progress";
    public static final String STATUS_PROPERTY = "status";
    
    private static final int DEFAULT_POMODORO_TIME = 60 * 25;
    private static final int DEFAULT_BREAK_TIME = 60 * 5;

    private boolean isStarted;
    
    public boolean isStarted() {
        return isStarted;
    }

    /*package*/ void removeAllPropertyChangeLiteners() {
        PropertyChangeListener [] listeners = getPropertyChangeListeners();
        for(PropertyChangeListener l : listeners) {
            removePropertyChangeListener(l);
        }
    }

    public enum Status {
        WORK,
        BREAK,
        DONE;
    }

    public interface TimerControlInputHandler {
        public void handle();
    }
    
    private Status status = Status.WORK;
    private Timer timer;
    private int timeValue = DEFAULT_POMODORO_TIME;
    private int percent;

    public TimerControl(final TimerControlInputHandler handler) {
        
        setText(getFormatedTime(DEFAULT_POMODORO_TIME));
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(createFont());
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                handler.handle();
            }
        });
    }

    private Font createFont() {
        try {
            InputStream is = getClass().getResourceAsStream("resources/fonts/digital.ttf");
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 20);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Can't create font!", ex);
        }
        return new Font(Font.MONOSPACED, Font.BOLD, 16);
    }

    public void start() {
        isStarted = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                setText(getFormatedTime(--timeValue));
                int fullTime = Status.WORK.equals(status) ? DEFAULT_POMODORO_TIME : DEFAULT_BREAK_TIME;
                if (Status.WORK.equals(status)) {
                    percent = 100 * (fullTime - timeValue) / fullTime;
                } else {
                    percent = 100 * timeValue / fullTime;
                }
                firePropertyChange(PROGRESS_PROPERTY, percent, percent);

                if (timeValue < 0) {
                    stop();
                    if (Status.WORK.equals(status)) {
                        firePropertyChange(STATUS_PROPERTY, Status.WORK, Status.BREAK);
                        timeValue = DEFAULT_BREAK_TIME;
                        status = Status.BREAK;
                        Notificator.showNotification(Notificator.KEY_START_BREAK);
                        start();
                    } else {
                        firePropertyChange(STATUS_PROPERTY, Status.WORK, Status.DONE);
                        timeValue = DEFAULT_POMODORO_TIME;
                        status = Status.WORK;
                        Notificator.showNotification(Notificator.KEY_START_WORK);
                    }
                }
                repaint();
            }
        }, 0, 1000);
    }

    private String getFormatedTime(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        if(Status.WORK.equals(status)) {
            g2d.setColor(new Color(102, 213, 41));
        } else {
            g2d.setColor(new Color(101, 154, 234));
        }
        Rectangle2D.Double shape = new Rectangle2D.Double(
                0, 0,  getParent().getWidth() / 100 * percent, getHeight());
        g2d.fill(shape);
        super.paintComponent(g);
    }

    public void stop() {
        isStarted = false;
        timer.cancel();
        timeValue = Status.WORK.equals(status) ? DEFAULT_BREAK_TIME : DEFAULT_POMODORO_TIME;
        setText(getFormatedTime(timeValue));
        repaint();
    }
}