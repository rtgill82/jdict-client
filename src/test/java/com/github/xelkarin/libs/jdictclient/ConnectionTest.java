/*
 * Copyright (C) 2018 Robert Gill <locke@sdf.lonestar.org>
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

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import static org.junit.Assert.*;
import static com.github.xelkarin.libs.jdictclient.Mocks.*;
import static com.github.xelkarin.libs.jdictclient.ResponseStrings.*;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
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

    @Test
    public void testSuccessfulConnection() {
        Connection connection = mockConnection(BANNER);
        try {
            connection.connect();
            assertConnectionBanner(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSuccessfulSocketConnection() {
        Socket socket = mockSocket(BANNER);
        try {
            Connection connection = new Connection(socket);
            connection.connect();
            assertConnectionBanner(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testServerUnavailable() {
        Connection connection = mockConnection(UNAVAILABLE);
        try {
            connection.connect();
        } catch (IOException e) {
            assertEquals(DictServerException.class, e.getClass());
            assertEquals(new Integer(420),
                         ((DictServerException) e).getStatus());
        }
    }

    @Test
    public void testServerShuttingDown() {
        Connection connection = mockConnection(SHUTDOWN);
        try {
            connection.connect();
        } catch (IOException e) {
            assertEquals(DictServerException.class, e.getClass());
            assertEquals(new Integer(421),
                         ((DictServerException) e).getStatus());
        }
    }

    @Test
    public void testServerAccessDenied() {
        Connection connection = mockConnection(ACCESS_DENIED);
        try {
            connection.connect();
        } catch (IOException e) {
            assertEquals(DictServerException.class, e.getClass());
            assertEquals(new Integer(530),
                         ((DictServerException) e).getStatus());
        }
    }

    @Test
    public void testUnconnectedSocket() {
        Socket socket = new Socket();
        try {
            Connection connection = new Connection(socket);
            connection.connect();
        } catch (IOException e) {
            assertEquals(SocketException.class, e.getClass());
            assertEquals(e.getMessage(), "Socket is not connected.");
        }
    }

    void assertConnectionBanner(Connection connection) {
        assertEquals(BANNER, connection.getBanner().message);
        assertEquals("<100@dictd.org>", connection.getId());
        List capabilities = connection.getCapabilities();
        assertTrue(capabilities.contains("auth"));
        assertTrue(capabilities.contains("mime"));
    }
}
