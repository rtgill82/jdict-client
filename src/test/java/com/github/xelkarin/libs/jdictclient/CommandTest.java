/*
 * Copyright (C) 2018 Robert Gill <rtgill82@gmail.com>
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

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static com.github.rtgill82.libs.jdictclient.Command.Type.*;
import static com.github.rtgill82.libs.jdictclient.Mocks.*;
import static com.github.rtgill82.libs.jdictclient.ResponseStrings.*;

/**
 * @author Robert Gill &lt;rtgill82@gmail.com&gt;
 *
 */
public class CommandTest {
    @Test
    public void testCommand() {
        Connection connection = mockConnection(DATABASES);
        try {
            Command command = new Command.Builder(SHOW_DATABASES)
                                         .build();
            List<Response> responses = command.execute(connection);
            assertEquals(2, responses.size());
            assertNotNull(responses.get(0).getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    @Test
    public void testResponseHandler() {
        Connection connection = mockConnection(NOT_IMPLEMENTED);
        try {
            Command command =
              new Command.Builder(SHOW_DATABASES)
                         .setResponseHandler(new ThrowExceptionHandler())
                         .build();
            List<Response> responses = command.execute(connection);
            fail("DictServerException expected");
        } catch (DictServerException e) {
            assertEquals(new Integer(502), e.getStatus());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    private class ThrowExceptionHandler implements ResponseHandler {
        @Override
        public boolean handle(Response response) throws DictServerException {
            if (response.getStatus() == 502) {
                throw new DictServerException("localhost",
                                              response.getStatus(),
                                              response.getMessage());
            }
            return true;
        }
    }
}
