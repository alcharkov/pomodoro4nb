package org.matveev.pomodoro4nb.timer;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.matveev.pomodoro4nb.controls.RolloverButton;
import org.matveev.pomodoro4nb.utils.Resources;

/**
 *
 * @author Alexey Matveev
 */
public class PomodoroTimer extends JPanel {

    public enum State {

        IDLE, WORK, BREAK
    }
    private final List<PomodoroTimerListener> timerListeners = new ArrayList<PomodoroTimerListener>();
    private JLabel timeLabel;
    private JButton controlButton;
    private final Action startAction = new StartTimerAction();
    private final Action stopAction = new StopTimerAction();
    private Timer countTime;
    private Timer progressTimer;
    private PomodoroTimerData data;
    private State state;
    private double progress;
    private long startTime;
    private long endTime;
    private boolean isForcedStateChange;
    private int pomodoros;

    public PomodoroTimer(PomodoroTimerData data) {
        this.data = data;
        createComponents();
    }

    private void createComponents() {
        setLayout(new BorderLayout());

        controlButton = new RolloverButton(startAction);
        add(controlButton, BorderLayout.WEST);

        timeLabel = new JLabel("00:00");
        timeLabel.setFont(Resources.createFont("digital.ttf", 20, null));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setOpaque(false);
        add(timeLabel);
    }

    /*package*/ void setTimerData(PomodoroTimerData data) {
        this.data = data;
        if (state == State.IDLE) {
            updateGUI();
        }
    }

    public void forcedStop() {
        setState(State.IDLE);
        isForcedStateChange = true;
    }

    public void setNewTimerData(PomodoroTimerData data) {
        if (data != null) {
            this.data = data;
        }
    }

    public void setState(State newState) {
        this.state = newState;
        fireStateChanged();
        stop();
        if (!State.IDLE.equals(state)) {
            if (State.BREAK.equals(state)) {
                pomodoros++;
            }
            start();

        }
    }

    protected void fireStateChanged() {
        for (PomodoroTimerListener l : timerListeners) {
            l.stateChanged(state, isForcedStateChange);
        }
        isForcedStateChange = false;
    }

    private void cancelTimers() {
        if (countTime != null) {
            countTime.cancel();
        }
        if (progressTimer != null) {
            progressTimer.cancel();
        }
    }

    /*package*/ void addPomodoroTimerListener(PomodoroTimerListener listener) {
        timerListeners.add(listener);
    }

    /*package*/ void removePomodoroTimerListener(PomodoroTimerListener listener) {
        timerListeners.remove(listener);
    }

    private void cancelAndRecreateTimers() {

        countTime = new Timer("P4NB-CountTimer");
        countTime.schedule(new TimerTask() {

            @Override
            public void run() {
                long delta = endTime - System.currentTimeMillis();
                if (delta > 0) {
                    timeLabel.setText(TimeFormater.format(delta));
                } else {
                    nextState();
                }
            }
        }, 0, 1000);

        progressTimer = new Timer("P4NB-ProgressTimer");
        progressTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                switch (state) {
                    case WORK:
                        progress = (double) (System.currentTimeMillis() - startTime)
                                / (double) (endTime - startTime);
                        break;
                    case BREAK:
                        progress = (double) (endTime - System.currentTimeMillis())
                                / (double) (endTime - startTime);
                        break;
                }
                repaint();
            }
        }, 0, (endTime - startTime) / getWidth());

    }

    private void updateTimeValuesAccordingToState() {
        progress = 0;
        startTime = System.currentTimeMillis();
        if (State.WORK.equals(state)) {
            endTime = startTime + data.getPomodoroLengthInMillis();
        } else if (State.BREAK.equals(state)) {
            if (pomodoros == data.getLongBreakInterval()) {
                endTime = startTime + data.getLongBreakLengthInMillis();
                pomodoros = 0;
            } else {
                endTime = startTime + data.getShortBreakLengthInMillis();
            }
        }
    }

    public void start() {
        updateTimeValuesAccordingToState();
        cancelAndRecreateTimers();
        updateGUI();
        repaint();
    }

    private void updateGUI() {
        controlButton.setAction(State.IDLE.equals(state) ? startAction : stopAction);
        timeLabel.setText(TimeFormater.format(data.getPomodoroLengthInMillis()));
    }

    private void nextState() {
        State newState = State.IDLE;
        if (state == State.WORK) {
            newState = State.BREAK;
        }
        setState(newState);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(State.WORK.equals(state)
                ? data.getWorkProgressBarColor()
                : data.getBreakProgressBarColor());
        Rectangle2D.Double shape = new Rectangle2D.Double(0, 0, getWidth() * progress, getHeight());
        g2d.fill(shape);
    }

    public void forcedStart() {
        setState(State.WORK);
    }

    public void stop() {
        cancelTimers();
        updateTimeValuesAccordingToState();
        updateGUI();
        repaint();
    }

    private class StartTimerAction extends AbstractAction {

        public StartTimerAction() {
            super("", Resources.createIcon("control_play.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            isForcedStateChange = true;
            setState(State.WORK);
        }
    }

    private class StopTimerAction extends AbstractAction {

        public StopTimerAction() {
            super("", Resources.createIcon("control_stop.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            isForcedStateChange = true;
            setState(State.IDLE);
        }
    }

    private static final class TimeFormater {

        private static final String DEFAULT_TIME_FORMAT_PATTERN = "%02d:%02d";

        public static String format(String format, long timeInMillis) {
            return String.format(format, timeInMillis / (1000 * 60), timeInMillis / 1000 % 60);
        }

        public static String format(long time) {
            return format(DEFAULT_TIME_FORMAT_PATTERN, time);
        }
    }
}