/*
 * Copyright (C) 2016 Robert Gill <rtgill82@gmail.com>
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
package com.github.rtgill82.libs.jdictclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Robert Gill &lt;rtgill82@gmail.com&gt;
 */
public class DatabaseTest {
    private final String DATABASE = "wn";
    private final String DESCRIPTION = "WordNet (r) 3.0 (2006)";

    /**
     * Test method for {@link com.github.rtgill82.libs.jdictclient.Database#Database(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testDatabase() {
        Database db = new Database(DATABASE, DESCRIPTION);
        assertEquals(DATABASE, db.getName());
        assertEquals(DESCRIPTION, db.getDescription());
        assertNull(db.getInfo());
    }

    /**
     * Test method for {@link com.github.rtgill82.libs.jdictclient.Database#Database(com.github.rtgill82.libs.jdictclient.Element)}.
     */
    @Test
    public void testConvertElement() {
        Element element = new Element(DATABASE, DESCRIPTION);
        Database db = new Database(element);
        assertEquals(DATABASE, db.getName());
        assertEquals(DESCRIPTION, db.getDescription());
        assertNull(db.getInfo());
    }

    /**
     * Test method for {@link com.github.rtgill82.libs.jdictclient.Database#setDatabaseInfo(java.lang.String)}.
     */
    @Test
    public void testSetDatabaseInfo() {
        Database db = new Database(DATABASE, DESCRIPTION);
        assertNull(db.getInfo());
        db.setDatabaseInfo("info\ninfo");
        assertEquals("info\ninfo", db.getInfo());
    }
}
