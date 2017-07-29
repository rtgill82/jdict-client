/*
 * Copyright (C) 2016 Robert Gill <locke@sdf.lonestar.org>
 *
 * This file is part of JDictClient.
 *
 * JDictClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * JDictClient is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with JDictClient.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.lonestar.sdf.locke.libs.jdictclient;

/**
 * An Element is a simple key, value pair.
 * <p>
 * It is used for data returned by the DICT protocol in the format of:<br />
 * <p>
 * <pre>
 * {@code
 * key "value"
 * }
 * </pre>
 * <p>
 * This includes data returned by SHOW DATABASES, SHOW STRATEGIES, and MATCH.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class Element {
    /**
     * Key used to identify the element
     */
    private String key;

    /**
     * Value of the element (often a description)
     */
    private String value;

    /**
     * Construct a new Element.
     *
     * @param key   the key identifying the element.
     * @param value the value of the element (often a description).
     */
    public Element(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Get the element key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the element value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates and returns a copy of this object.
     */
    @Override
    public Element clone() {
        Element element = new Element(
            getKey(),
            getValue()
        );

        return element;
    }

    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        return key + " \"" + value + '"';
    }
}
