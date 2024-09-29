/*
 * Copyright (C) 2016 Robert Gill <rtgill82@gmail.com>
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

import static org.junit.Assert.assertEquals;

/**
 * @author Robert Gill &lt;rtgill82@gmail.com&gt;
 */
public class DictExceptionTest {
    private final String HOST = "test.dict.org";
    private final Integer STATUS = 500;
    private final String RESPONSE = "500 Syntax error, command not recognized";
    private final String MESSAGE = HOST + ": " + RESPONSE;

    /**
     * Test method for {@link com.github.rtgill82.libs.jdictclient.DictException#DictException(java.lang.String, int, java.lang.String)}.
     */
    @Test
    public void testDictException() {
        DictException exception = new DictException(HOST, STATUS, RESPONSE);
        assertEquals(HOST, exception.getHost());
        assertEquals(STATUS, exception.getStatus());
        assertEquals(MESSAGE, exception.getMessage());
    }
}
