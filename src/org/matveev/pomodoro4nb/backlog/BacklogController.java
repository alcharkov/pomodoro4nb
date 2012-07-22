/*
 * Copyright (C) 2012 Alexey Matveev <mvaleksej@gmail.com>
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
package org.matveev.pomodoro4nb.backlog;

import java.awt.Container;
import org.matveev.pomodoro4nb.controllers.AbstractController;
import org.matveev.pomodoro4nb.data.io.PropertiesSerializer;
import org.matveev.pomodoro4nb.data.io.PropertiesSerializerFactory;
import org.matveev.pomodoro4nb.utils.Base64Coder;

/**
 *
 * @author Alexey Matveev
 */
public class BacklogController extends AbstractController {

    private Backlog backlog = new Backlog();

    public BacklogController() {
        backlog = new Backlog();
        registerHandler();
    }

    private void registerHandler() {
    }

    @Override
    public Container createUI() {
        return null;
    }

    @Override
    public void store(java.util.Properties props) throws Exception {
        final PropertiesSerializer serializer = PropertiesSerializerFactory.createXMLSerializer();
        final String data = serializer.serialize(backlog);
        props.setProperty("backlog", Base64Coder.encodeString(data));
    }

    @Override
    public void restore(java.util.Properties props) throws Exception {
        final Object data = props.getProperty("backlog");
        if (data != null) {
            final PropertiesSerializer serializer = PropertiesSerializerFactory.createXMLSerializer();
            final String xmlString = Base64Coder.decodeString((String) data);
            backlog = (Backlog) serializer.deserealize(xmlString.trim());
        }
    }
}
