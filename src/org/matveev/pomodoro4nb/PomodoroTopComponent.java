/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.matveev.pomodoro4nb;

import java.awt.BorderLayout;
import java.io.IOException;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;

@ConvertAsProperties(dtd = "-//org.matveev.pomodoro4nb//Pomodoro//EN", autostore = false)
@TopComponent.Description(preferredID = "PomodoroTopComponent", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "org.matveev.pomodoro4nb.PomodoroTopComponent")
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(displayName = "#CTL_PomodoroAction", preferredID = "PomodoroTopComponent")
public final class PomodoroTopComponent extends TopComponent {

    private PomodorosTracker tracker = new PomodorosTracker();
    
    public PomodoroTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(PomodoroTopComponent.class, "CTL_PomodoroTopComponent"));
        setToolTipText(NbBundle.getMessage(PomodoroTopComponent.class, "HINT_PomodoroTopComponent"));
      
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        add(tracker.createContent());
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated 
        // at http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        try {
            tracker.storeProperties(p);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        try {
            tracker.loadProperties(p);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
