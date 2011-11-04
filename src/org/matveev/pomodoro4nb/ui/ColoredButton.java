package org.matveev.pomodoro4nb.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;

/**
 *
 * @author Alexey Matveev
 */
public class ColoredButton extends RolloverButton {

    private Color color = Color.WHITE;

    public ColoredButton() {
        setFocusPainted(false);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(ColoredButton.this, "Select a color.", color);
                if (newColor != null) {
                    color = newColor;
                }
            }
        });
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final int width = getWidth();
        final int height = getHeight();
        final int offset = 5;
        int rightCorner = width - 2 * offset;
        int bottomCorner = height - 2 * offset;
        if (rightCorner > 0 && bottomCorner > 0) {
            final Color oldColor = g.getColor();
            g.setColor(color);
            g.fillRect(offset, offset, rightCorner, bottomCorner);
            g.setColor(oldColor);
        }
    }
}
