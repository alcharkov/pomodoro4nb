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
package org.matveev.pomodoro4nb.data.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.matveev.pomodoro4nb.core.data.Property;
import org.matveev.pomodoro4nb.domain.Children;
import org.matveev.pomodoro4nb.domain.DomainObject;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Alexey Matveev
 */
public final class XMLPropertiesSerializer implements DomainObjectSerializer {

    private static final List<Property<?>> EXCLUDES_LIST = Arrays.<Property<?>>asList(DomainObject.SerializeKey);

    @Override
    public String serialize(DomainObject container) throws Exception {
        return new InternalSerializer(container).serialize();
    }

    @Override
    public DomainObject deserealize(String xmlString) throws Exception {
        return new InternalDeserializer(xmlString).deserialize();
    }

    private static final class InternalSerializer {

        private final DomainObject container;

        public InternalSerializer(DomainObject container) {
            this.container = container;
        }

        public String serialize() throws Exception {
            final Document doc = createDocument();
            doc.appendChild(serializeContainer(doc, container));
            return convertToString(doc);
        }

        private Element serializeContainer(final Document doc, final DomainObject container) {
            final Element element = doc.createElement(container.getProperty(DomainObject.SerializeKey));
            for (Property<?> property : container.getProperties()) {
                if (!EXCLUDES_LIST.contains(property)) {
                    element.setAttribute(property.getName(), container.getProperty(property).toString());
                }
            }
            for (DomainObject e : container.getChildren()) {
                element.appendChild(serializeContainer(doc, e));
            }

            return element;
        }

        private Document createDocument() throws ParserConfigurationException {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final DOMImplementation domImpl = builder.getDOMImplementation();
            return domImpl.createDocument(null, null, null);
        }
    }

    private static final class InternalDeserializer {

        private static final Logger LOGGER = Logger.getLogger(InternalDeserializer.class.getName());
        private final String xmlString;

        private InternalDeserializer(String xmlString) {
            this.xmlString = xmlString;
        }

        public DomainObject deserialize() throws Exception {
            final Document doc = parseXMLString(xmlString);
            if (doc == null) {
                return null;
            }
            final Element rootElement = doc.getDocumentElement();
            final DomainObject rootContainer = createDomainObject(rootElement);
            parseAttributes(rootContainer, rootElement);

            final List<NodeList> subElements = extractSubElements(rootElement, rootContainer);
            for (NodeList list : subElements) {
                for (int ix = 0; ix < list.getLength(); ix++) {
                    final Element e = (Element) list.item(ix);
                    rootContainer.add(deserializeDomainObject(e));
                }
            }
            return rootContainer;
        }

        public static List<NodeList> extractSubElements(final Element element, final DomainObject container)
                throws Exception {
            final List<NodeList> result = new ArrayList<NodeList>();

            final Children children = container.getClass().getAnnotation(Children.class);
            if (children != null) {
                final Class<? extends DomainObject>[] types = children.types();
                for (Class<? extends DomainObject> type : types) {
                    final Object instance = type.getConstructor((Class<?>) null).newInstance();
                    if (instance != null) {
                        final String name = ((DomainObject) instance).getProperty(DomainObject.SerializeKey);
                        final NodeList nodeList = element.getElementsByTagName(name);
                        if (nodeList != null) {
                            result.add(nodeList);
                        }
                    }
                }
            }
            return result;
        }

        private static Property<?> extractPropertyFrom(final Field field) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(Property.class)) {
                try {
                    return (Property<?>) field.get(null);
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "cannot extract property from field ''{0}'', Reason: {1}",
                            new Object[]{field.getName(), ex.getMessage()});
                } catch (IllegalAccessException ex) {
                    LOGGER.log(Level.WARNING, "cannot extract property from field ''{0}'', Reason: {1}",
                            new Object[]{field.getName(), ex.getMessage()});
                }
            }
            return null;
        }

        private static void parseAttributes(DomainObject container, final Element element) throws SecurityException {
            for (Field field : container.getClass().getFields()) {
                final Property<?> property = extractPropertyFrom(field);
                if (property != null) {
                    if (element.hasAttribute(property.getName())) {
                        final String value = element.getAttribute(property.getName());
                        Object result = null;
                        if (property.getType().isAssignableFrom(Integer.class)) {
                            result = Integer.valueOf(value);
                        } else if (property.getType().isAssignableFrom(Long.class)) {
                            result = Long.valueOf(value);
                        } else if (property.getType().isAssignableFrom(Boolean.class)) {
                            result = Boolean.valueOf(value);
                        } else if (property.getType().isAssignableFrom(UUID.class)) {
                            result = UUID.fromString(value);
                        } else if (property.getType().isEnum()) {
                            result = Enum.valueOf((Class<Enum>) property.getType(), (String) value);
                        } else if (property.getType().isAssignableFrom(String.class)) {
                            result = value;
                        } else {
                            LOGGER.log(Level.SEVERE, "Cannot parse property ''{0}''", property);
                        }
                        if (result != null) {
                            container.setProperty(property, property.getType().cast(result));
                        }
                    }
                }
            }
        }

        private static Document parseXMLString(final String xml)
                throws ParserConfigurationException, IOException, SAXException {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        }

        private static DomainObject createDomainObject(final Element element) throws Exception {
            final Class type = Class.forName(element.getAttribute(DomainObject.ClassType.getName()));
            final Object instance = type.getConstructor((Class) null).newInstance();
            return instance == null ? null : (DomainObject) instance;
        }

        private static DomainObject deserializeDomainObject(final Element element) throws Exception {
            final DomainObject object = createDomainObject(element);
            if (object != null) {
                parseAttributes(object, element);
                List<NodeList> subElements = extractSubElements(element, object);
                for (NodeList list : subElements) {
                    for (int i = 0; i < list.getLength(); i++) {
                        object.add(deserializeDomainObject((Element) list.item(i)));
                    }
                }
            }
            return object;
        }
    }

    private static String convertToString(final Document doc)
            throws TransformerConfigurationException, TransformerException {
        final DOMSource source = new DOMSource(doc);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        final StringWriter writer = new StringWriter();
        final StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        return writer.toString();
    }
}
