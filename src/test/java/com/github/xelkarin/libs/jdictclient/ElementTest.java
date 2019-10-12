/*
 * Copyright (C) 2016 Robert Gill <locke@sdf.lonestar.org>
 *
 * This file is part of jdict-client.
 *
 * jdict-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * jdict-client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with jdict-client.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.github.xelkarin.libs.jdictclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class ElementTest {
    private final String KEY = "element-key";
    private final String VALUE = "This is the element value.";
    private final String ITEMSTRING = KEY + " \"" + VALUE + '"';

    /**
     * Test method for {@link com.github.xelkarin.libs.jdictclient.Element#Element(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testElement() {
        Element element = new Element(KEY, VALUE);
        assertEquals(KEY, element.getKey());
        assertEquals(VALUE, element.getValue());
    }

    /**
     * Test method for {@link com.github.xelkarin.libs.jdictclient.Element#toString()}.
     */
    @Test
    public void testElementToString() {
        Element element = new Element(KEY, VALUE);
        assertEquals(ITEMSTRING, element.toString());
    }
}
