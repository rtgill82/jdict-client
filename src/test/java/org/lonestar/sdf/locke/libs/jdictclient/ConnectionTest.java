/*
 * Copyright (C) 2018 Robert Gill <locke@sdf.lonestar.org>
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class ConnectionTest {
    @Test
    public void testConnectionDefaultPort() {
        Connection connection = new Connection("dict.org");
        assertEquals("dict.org", connection.toString());
    }

    @Test
    public void testConnectionAltPort() {
        Connection connection = new Connection("dict.org", 20000);
        assertEquals("dict.org:" + 20000, connection.toString());
    }
}
