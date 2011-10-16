package org.matveev.pomodoro4nb;

import javax.swing.Action;

/**
 *
 * @author Alexey Matveev
 */
public interface ValidatableAction extends Action {
    
    public void validate();
    
}
