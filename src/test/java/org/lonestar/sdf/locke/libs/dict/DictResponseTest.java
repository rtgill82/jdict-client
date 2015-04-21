/*
 * Created:  Mon 10 Dec 2012 01:09:20 AM PST
 * Modified: Mon 20 Apr 2015 08:03:48 PM PDT
 * Copyright © 2013 Robert Gill <locke@sdf.lonestar.org>
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
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.lonestar.sdf.locke.libs.dict.DictResponse;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictResponseTest {

    /* SHOW DATABASES response */
    private final String DATABASES =
        "110 1 databases present - text follows\n" +
        "foldoc \"The Free On-line Dictionary of Computing (26 July 2010)\"" +
        "\n.\n250 ok";

    /* SHOW STRATEGIES response */
    private final String STRATEGIES =
        "111 1 strategies present\n" +
        "exact \"Match headwords exactly\"" +
        "\n.\n250 ok";

    /* SHOW HELP response */
    private final String HELP =
        "113 help text follows\n" +
        "DEFINE database word         -- look up word in database\n" +
        "\n.\n250 ok";

    /* SHOW INFO response */
    private final String DATABASE_INFO =
        "112 database information follows\n" +
        "Some random information describing a database.\n" +
        "\n.\n250 ok";

    /* SHOW SERVER response */
    private final String SERVER_INFO =
        "114 server information follows\n" +
        "Some random information describing the server.\n" +
        "\n.\n250 ok";

    /* DEFINE response */
    private final String DEFINITION =
        "150 1 definitions retrieved\n" +
        "151 \"word\" database \"Database Description\"\n" +
        "This word is defined as a word with meaning.\n" +
        "\n.\n250 ok";

    /* MATCH response */
    private final String MATCH =
        "152 21 matches found\n" +
        "wn \"cat\"" +
        "wn \"cat and mouse\"" +
        "\n.\n250 ok [d/m/c = 0/1/323; 0.000r 0.000u 0.000s]";

    /**
     * Test reading of successful status.
     */
    @Test
    public void testDictResponse()
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        BufferedReader buf_reader = stringBuffer("250 ok");
        try {
            DictResponse resp = new DictResponse(buf_reader);
            assertEquals(250, resp.getStatus());
            assertEquals("250 ok", resp.getMessage());
            assertNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of SHOW DATABASES response.
     */
    @Test
    public void testDatabaseResponse()
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        BufferedReader buf_reader = stringBuffer(DATABASES);
        try {
            DictResponse resp = DictResponse.read(buf_reader);
            assertEquals(250, resp.getStatus());
            assertEquals(List.class, resp.getDataClass());
            assertNotNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of SHOW STRATEGIES response.
     */
    @Test
    public void testStrategiesResponse()
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        BufferedReader buf_reader = stringBuffer(STRATEGIES);
        try {
            DictResponse resp = DictResponse.read(buf_reader);
            assertEquals(250, resp.getStatus());
            assertEquals(List.class, resp.getDataClass());
            assertNotNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of HELP response.
     */
    @Test
    public void testHelpResponse()
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        BufferedReader buf_reader = stringBuffer(DATABASE_INFO);
        try {
            DictResponse resp = DictResponse.read(buf_reader);
            assertEquals(250, resp.getStatus());
            assertEquals(String.class, resp.getDataClass());
            assertNotNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }


    /**
     * Test reading of SHOW INFO response.
     */
    @Test
    public void testDatabaseInfoResponse()
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        BufferedReader buf_reader = stringBuffer(DATABASE_INFO);
        try {
            DictResponse resp = DictResponse.read(buf_reader);
            assertEquals(250, resp.getStatus());
            assertEquals(String.class, resp.getDataClass());
            assertNotNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of SHOW SERVER response.
     */
    @Test
    public void testServerInfoResponse()
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        BufferedReader buf_reader = stringBuffer(SERVER_INFO);
        try {
            DictResponse resp = DictResponse.read(buf_reader);
            assertEquals(250, resp.getStatus());
            assertEquals(String.class, resp.getDataClass());
            assertNotNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of DEFINE response.
     */
    @Test
    public void testDefineResponse()
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        BufferedReader buf_reader = stringBuffer(DEFINITION);
        try {
            DictResponse resp = DictResponse.read(buf_reader);
            assertEquals(250, resp.getStatus());
            assertEquals(List.class, resp.getDataClass());
            assertNotNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Test reading of MATCH response.
     */
    @Test
    public void testMatchResponse()
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        BufferedReader buf_reader = stringBuffer(MATCH);
        try {
            DictResponse resp = DictResponse.read(buf_reader);
            assertEquals(250, resp.getStatus());
            assertEquals(List.class, resp.getDataClass());
            assertNotNull(resp.getData());
        } catch (IOException e) {
            fail("IOException: " + e.getMessage());
        }
    }

    /**
     * Create buffered reader for String.
     */
    private BufferedReader stringBuffer(String str)
    {
        StringReader sreader = new StringReader(str);
        BufferedReader breader = new BufferedReader(sreader);
        return breader;
    }
}
