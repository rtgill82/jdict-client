/*
 * Created:  Sun 19 Apr 2015 08:56:55 PM PDT
 * Modified: Sun 19 Apr 2015 09:55:14 PM PDT
 * Copyright © 2015 Robert Gill <locke@sdf.lonestar.org>
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
package org.lonestar.sdf.locke.libs.dict;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ResourceBundle;

import org.lonestar.sdf.locke.libs.dict.JDictClient;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class JDictClientTest {

    private static String LIBRARY_VERSION = null;
    private static String LIBRARY_NAME    = "JDictClient";
    private static String LIBRARY_VENDOR  = "Robert Gill <locke@sdf.lonestar.org>";

    @BeforeClass
    public static void oneTimeSetUp()
    {
        String packageName = JDictClient.class.getPackage().getName();
        ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
        LIBRARY_VERSION = rb.getString("library.version");
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.JDictClient#getLibraryName()}.
     */
    @Test
    public void testLibraryName()
    {
        JDictClient jdictclient = new JDictClient(null, 0);
        assertEquals(LIBRARY_NAME, jdictclient.getLibraryName());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.JDictClient#getLibraryVersion()}.
     */
    @Test
    public void testLibraryVersion()
    {
        JDictClient jdictclient = new JDictClient(null, 0);
        assertEquals(LIBRARY_VERSION, jdictclient.getLibraryVersion());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.JDictClient#getLibraryVendor()}.
     */
    @Test
    public void testLibraryVendor()
    {
        JDictClient jdictclient = new JDictClient(null, 0);
        assertEquals(LIBRARY_VENDOR, jdictclient.getLibraryVendor());
    }
}
