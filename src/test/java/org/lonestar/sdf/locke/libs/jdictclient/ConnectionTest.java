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

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.lonestar.sdf.locke.libs.jdictclient.Mocks.*;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class ConnectionTest {
    /* Connection banner response */
    private final String BANNER = "220 dictd 1.12 <auth.mime> <100@dictd.org>";

    /* Server unavailable response */
    private final String UNAVAILABLE = "420 Server temporarily unavailable";

    /* Server shutting down response */
    private final String SHUTDOWN = "421 Server shutting down at operator request";

    /* Server access denied response */
    private final String DENIED = "530 Access denied";

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

    @Test
    public void testSuccessfulConnection() {
        Connection connection = mockConnection(BANNER);
        try {
            connection.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(BANNER, connection.getBanner().message);
        assertEquals("<100@dictd.org>", connection.getId());
        List capabilities = connection.getCapabilities();
        assertTrue(capabilities.contains("auth"));
        assertTrue(capabilities.contains("mime"));
    }

    @Test
    public void testServerUnavailable() {
        Connection connection = mockConnection(UNAVAILABLE);
        try {
            connection.connect();
        } catch (IOException e) {
            assertEquals(DictServerException.class, e.getClass());
            assertEquals(420, ((DictServerException) e).getStatus());
        }
    }

    @Test
    public void testServerShuttingDown() {
        Connection connection = mockConnection(SHUTDOWN);
        try {
            connection.connect();
        } catch (IOException e) {
            assertEquals(DictServerException.class, e.getClass());
            assertEquals(421, ((DictServerException) e).getStatus());
        }
    }

    @Test
    public void testServerAccessDenied() {
        Connection connection = mockConnection(DENIED);
        try {
            connection.connect();
        } catch (IOException e) {
            assertEquals(DictServerException.class, e.getClass());
            assertEquals(530, ((DictServerException) e).getStatus());
        }
    }
}
