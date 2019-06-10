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
package org.lonestar.sdf.locke.libs.jdictclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
class Mocks {
    @Mock Socket socket;
    @Mock InetAddress inetAddress;
    @Mock InputStream input;
    @Mock OutputStream output;
    @Mock PrintWriter printWriter;

    @InjectMocks
    Connection connection = spy(new Connection("localhost"));

    public Mocks() {
        MockitoAnnotations.initMocks(this);
        try {
            when(inetAddress.getHostName()).thenReturn("localhost");
            when(socket.getInputStream()).thenReturn(input);
            when(socket.getOutputStream()).thenReturn(output);
            when(socket.getInetAddress()).thenReturn(inetAddress);
            when(socket.getPort()).thenReturn(Connection.DEFAULT_PORT);
            when(socket.getSoTimeout()).thenReturn(Connection.DEFAULT_TIMEOUT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create mock Connection.
     *
     * @param str the String to return when reading from the connection.
     */
    public static Connection mockConnection(String str) {
        return new Mocks().connection(str);
    }

    /**
     *  Create a mock Socket.
     *
     *  @param str the String to return when reading from the socket.
     */
    public static Socket mockSocket(String str) {
        return new Mocks().socket(str);
    }

    private Connection connection(String str) {
        when(connection.getInputReader()).thenReturn(stringReader(str));
        when(connection.getOutputWriter()).thenReturn(printWriter);
        return connection;
    }

    private Socket socket(String str) {
        try {
            when(socket.getInputStream()).thenReturn(stringStream(str));
            when(socket.isConnected()).thenReturn(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }

    /**
     * Create BufferedReader for a String.
     */
    private BufferedReader stringReader(String str) {
        StringReader sreader = new StringReader(str);
        BufferedReader breader = new BufferedReader(sreader);
        return breader;
    }

    /**
     * Create InputStream for a String.
     */
    private InputStream stringStream(String str) {
        try {
            return new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
