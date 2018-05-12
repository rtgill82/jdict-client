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
package org.lonestar.sdf.locke.libs.jdictclient;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class JDictClientTest {
    private static String LIBRARY_VERSION;
    private static String LIBRARY_NAME = "jdict-client";
    private static String LIBRARY_VENDOR =
            "Robert Gill <locke@sdf.lonestar.org>";

    @BeforeClass
    public static void oneTimeSetUp() {
        String packageName = JDictClient.class.getPackage().getName();
        ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
        LIBRARY_VERSION = rb.getString("library.version");
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.JDictClient#getLibraryName()}.
     */
    @Test
    public void testLibraryName() {
        assertEquals(LIBRARY_NAME, JDictClient.getLibraryName());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.JDictClient#getLibraryVersion()}.
     */
    @Test
    public void testLibraryVersion() {
        assertEquals(LIBRARY_VERSION, JDictClient.getLibraryVersion());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.JDictClient#getLibraryVendor()}.
     */
    @Test
    public void testLibraryVendor() {
        assertEquals(LIBRARY_VENDOR, JDictClient.getLibraryVendor());
    }
}
