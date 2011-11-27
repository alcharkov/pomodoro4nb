package org.matveev.pomodoro4nb.controllers;

/**
 *
 * @author Alexey Matvey
 */
public interface Handler<T> {
    public void handle(T oldValue, T newValue);
}
