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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.Socket;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
class Mocks {
    @Mock
    Socket socket;

    @InjectMocks
    Connection connection = mock(Connection.class, CALLS_REAL_METHODS);

    public Mocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Create mock Connection.
     *
     * @param str the string to return when reading from connection.
     */
    public static Connection mockConnection(String str) {
        return new Mocks().connection(str);
    }

    private Connection connection(String str) {
        when(connection.getInputReader()).thenReturn(stringBuffer(str));
        return connection;
    }

    /**
     * Create buffered reader for String.
     */
    private BufferedReader stringBuffer(String str) {
        StringReader sreader = new StringReader(str);
        BufferedReader breader = new BufferedReader(sreader);
        return breader;
    }
}
