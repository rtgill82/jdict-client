/*
 * Created:  Mon 20 Apr 2015 07:38:35 PM PDT
 * Modified: Mon 20 Apr 2015 08:03:47 PM PDT
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
import org.junit.Test;
import org.lonestar.sdf.locke.libs.dict.DictException;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictExceptionTest {

    private final String HOST     = "test.dict.org";
    private final int STATUS      = 500;
    private final String RESPONSE = "500 Syntax error, command not recognized";
    private final String MESSAGE  = HOST + ": " + RESPONSE;

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.DictException#DictException(java.lang.String, int, java.lang.String)}.
     */
    @Test
    public void testDictException()
    {
        DictException exception = new DictException(HOST, STATUS, RESPONSE);
        assertEquals(HOST, exception.getHost());
        assertEquals(STATUS, exception.getStatus());
        assertEquals(MESSAGE, exception.getMessage());
    }
}
