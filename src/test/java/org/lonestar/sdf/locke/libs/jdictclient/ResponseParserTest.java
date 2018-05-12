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

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.lonestar.sdf.locke.libs.jdictclient.Mocks.*;
import static org.lonestar.sdf.locke.libs.jdictclient.ResponseStrings.*;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class ResponseParserTest {
    /**
     * Test reading of successful status.
     */
    @Test
    public void testResponseParser() {
        Connection connection = mockConnection("250 ok");
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(250, resp.getStatus());
            assertEquals("250 ok", resp.getMessage());
            assertEquals("ok", resp.getText());
            assertNull(resp.getRawData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of invalid response.
     */
    @Test
    public void testInvalidResponse() {
        Exception exception = null;
        Connection connection = mockConnection(INVALID_RESPONSE);
        try {
            Response resp = ResponseParser.parse(connection);
        } catch (DictException e) {
            exception = e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(exception.getClass(), DictException.class);
    }

    /**
     * Test reading of connection banner.
     */
    @Test
    public void testConnectionBanner() {
        Connection connection = mockConnection(BANNER);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(220, resp.getStatus());
            assertNotNull(resp.getData());
            Banner banner = (Banner) resp.getData();
            assertEquals("dictd 1.12", banner.text);
            assertEquals("<100@dictd.org>", banner.connectionId);
            assertEquals(2, banner.capabilities.size());
            assertEquals(true, banner.capabilities.contains("auth"));
            assertEquals(true, banner.capabilities.contains("mime"));
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test handling of invalid connection banner.
     */
    public void testInvalidConnectionBanner() {
        Connection connection = mockConnection(INVALID_BANNER);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(220, resp.getStatus());
            assertNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of SHOW DATABASES response.
     */
    @Test
    public void testDatabaseResponse() {
        Connection connection = mockConnection(DATABASES);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(110, resp.getStatus());
            assertNotNull(resp.getRawData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of SHOW STRATEGIES response.
     */
    @Test
    public void testStrategiesResponse() {
        Connection connection = mockConnection(STRATEGIES);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(111, resp.getStatus());
            assertNotNull(resp.getRawData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of HELP response.
     */
    @Test
    public void testHelpResponse() {
        Connection connection = mockConnection(HELP);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(113, resp.getStatus());
            assertNotNull(resp.getRawData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of SHOW INFO response.
     */
    @Test
    public void testDatabaseInfoResponse() {
        Connection connection = mockConnection(DATABASE_INFO);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(112, resp.getStatus());
            assertNotNull(resp.getRawData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of SHOW SERVER response.
     */
    @Test
    public void testServerInfoResponse() {
        Connection connection = mockConnection(SERVER_INFO);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(114, resp.getStatus());
            assertNotNull(resp.getRawData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of DEFINE response.
     */
    @Test
    public void testDefineResponse() {
        Connection connection = mockConnection(DEFINITION);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(150, resp.getStatus());
            resp = ResponseParser.parse(connection);
            assertEquals(151, resp.getStatus());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of MATCH response.
     */
    @Test
    public void testMatchResponse() {
        Connection connection = mockConnection(MATCH);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(152, resp.getStatus());
            assertNotNull(resp.getRawData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of successful AUTH response.
     */
    @Test
    public void testAuthSuccess() {
        Connection connection = mockConnection(AUTH_SUCCESS);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(230, resp.getStatus());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of failed AUTH response.
     */
    @Test
    public void testAuthFail() {
        Connection connection = mockConnection(AUTH_FAIL);
        try {
            Response resp = ResponseParser.parse(connection);
            assertEquals(531, resp.getStatus());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    @Test
    public void testPipelining() {
        Connection connection = mockConnection(PIPELINE_RESPONSE);
        try {
            ResponseParser parser = new ResponseParser(connection, 3);
            int count = 0;
            while (parser.hasNext()) {
                Response resp = parser.parse();
                count += 1;
            }
            assertEquals(6, count);
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }
}
