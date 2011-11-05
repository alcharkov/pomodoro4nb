package org.matveev.pomodoro4nb;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Alexey Matveev
 */
public interface HasProperties {
    public void loadProperties(Properties props) throws IOException, ClassNotFoundException;
    public void storeProperties(Properties props) throws IOException;
}
