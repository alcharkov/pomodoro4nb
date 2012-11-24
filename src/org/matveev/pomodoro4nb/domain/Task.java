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
package org.matveev.pomodoro4nb.domain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import org.matveev.pomodoro4nb.core.data.Properties;
import org.matveev.pomodoro4nb.core.data.Property;
import org.matveev.pomodoro4nb.domain.Interruption.Type;
import org.matveev.pomodoro4nb.utils.Resources;
import org.matveev.pomodoro4nb.utils.Utils;

/**
 *
 * @author Alexey Matvey
 */
@Children(types = {Interruption.class})
public class Task extends DomainObject {

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
        final List<DomainObject> elements = getChildren(Interruption.class);
        for (Properties e : elements) {
            if (type.equals(e.getProperty(Interruption.InterruptionType))) {
                result.add((Interruption) e);
            }
        }
        return result;
    }

    public enum Priority {

        Improvements(Utils.parseColor("#cbe2e7")),
        Blocker(Utils.parseColor("#d75d5d")),
        Critical(Utils.parseColor("#eca063")),
        Major(Utils.parseColor("#eaca98")),
        Minor(Utils.parseColor("#efedaa")),
        Trivial(Utils.parseColor("#d8f3ca"));

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
