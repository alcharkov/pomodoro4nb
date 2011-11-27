package org.matveev.pomodoro4nb.controls;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class RolloverButton extends JButton {

    private static final AlphaComposite COMPOSITE =
            AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private boolean revalidateBlocked;

    public RolloverButton() {
    }

    public RolloverButton(Icon icon) {
        super(icon);
    }
    
    public RolloverButton(Action action) {
        super(action);
        addMouseListener(new MouseOverHandler());
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBorderPainted(false);
        setRequestFocusEnabled(false);
        setMargin(new Insets(1, 1, 1, 1));
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        setBorderPainted(false);
        repaint();
    } 

    @Override
    public void setBorderPainted(boolean b) {
        try {
            revalidateBlocked = true;
            super.setBorderPainted(b);
            setContentAreaFilled(b);
        } finally {
            revalidateBlocked = false;
        }
    } 
    
    @Override
    public void revalidate() {
        if (!revalidateBlocked) {
            super.revalidate();
        }
    } 
    
    @Override
    public void paint(Graphics g) {
        if (isEnabled()) {
            super.paint(g);
        } else {
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(COMPOSITE);
            super.paint(g2);
        }
    } 
    
    class MouseOverHandler extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            setContentAreaFilled(true);
            setBorderPainted(isEnabled());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setContentAreaFilled(false);
            setBorderPainted(false);
        }
    }
}
