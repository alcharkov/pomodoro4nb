/*
 * Pomodoro4NB - Netbeans plugin for work with The Pomodoro Technique
 * 
 * Copyright (C) 2011 Alexey Matveev <mvaleksej@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.matveev.pomodoro4nb.task;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.Icon;
import org.matveev.pomodoro4nb.data.Children;
import org.matveev.pomodoro4nb.data.Properties;
import org.matveev.pomodoro4nb.data.Property;
import org.matveev.pomodoro4nb.task.Interruption.Type;
import org.matveev.pomodoro4nb.utils.Resources;
import org.matveev.pomodoro4nb.utils.Utils;

/**
 *
 * @author Alexey Matvey
 */
@Children({Interruption.class})
public class Task extends Properties {

    public static final Property<UUID> Parent = new Property<UUID>("parent", UUID.class);
    public static final Property<String> Description = new Property<String>("desc", String.class);
    public static final Property<Integer> Estimate = new Property<Integer>("estimate", Integer.class);
    public static final Property<Integer> Pomodoros = new Property<Integer>("pomodoros", Integer.class);
    public static final Property<Long> Created = new Property<Long>("created", Long.class);
    public static final Property<Boolean> Completed = new Property<Boolean>("completed", Boolean.class);
    public static final Property<Priority> TaskPriority = new Property<Priority>("priority", Priority.class);
    public static final Property<Status> TaskStatus = new Property<Status>("status", Status.class);

    public Task() {
        setProperty(Created, System.currentTimeMillis());
        setProperty(Estimate, 0);
        setProperty(Pomodoros, 0);
    }
    
    public List<Interruption> getInterruptions(Type type) {
        final List<Interruption> result = new ArrayList<Interruption>();
        final List<Properties> elements = getElements(Interruption.class);
        for (Properties e : elements) {
            if (type.equals(e.getProperty(Interruption.InterruptionType))) {
                result.add((Interruption) e);
            }
        }
        return result;
    }
    
    public enum Priority {

        Improvements(Utils.parse("#cbe2e7")),
        Blocker(Utils.parse("#d75d5d")),
        Critical(Utils.parse("#eca063")),
        Major(Utils.parse("#eaca98")),
        Minor(Utils.parse("#efedaa")),
        Trivial(Utils.parse("#d8f3ca"));

        private Priority(Color color) {
            this.color = color;
        }
        
        public final Color color; 
    }

    public enum Status {

        Clear(Resources.createIcon("weather_sun.png")),
        Cloudy(Resources.createIcon("weather_clouds.png")),
        Stormy(Resources.createIcon("weather_lightning.png"));

        private Status(Icon icon) {
            this.icon = icon;
        }
        
        public final Icon icon;
    }
}
