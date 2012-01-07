package org.matveev.pomodoro4nb.controllers;

import java.awt.Container;
import java.util.List;
import org.matveev.pomodoro4nb.utils.Storable;
import org.matveev.pomodoro4nb.data.Property;
import org.matveev.pomodoro4nb.data.PropertyListener;

/**
 *
 * @author Alexey Matvey
 */
public interface Controller extends Storable {
    
    public Container createUI();
    
    public void addPropertyListener(PropertyListener listener);
    public void removePropertyListener(PropertyListener listener);
    
    public List<Handler> getHandlers(Property<?> property);
}
