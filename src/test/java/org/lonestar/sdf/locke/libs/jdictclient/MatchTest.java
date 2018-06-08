/*
 * Copyright (C) 2016 Robert Gill <locke@sdf.lonestar.org>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class MatchTest {
    private final String DATABASE = "foldoc";
    private final String WORD = "linux";

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Match#Match(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testMatch() {
        Match match = new Match(DATABASE, WORD);
        assertEquals(DATABASE, match.getDatabase());
        assertEquals(WORD, match.getWord());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Match#Match(org.lonestar.sdf.locke.libs.jdictclient.Element)}.
     */
    @Test
    public void testConvertElement() {
        Element element = new Element(DATABASE, WORD);
        Match match = new Match(element);
        assertEquals(DATABASE, match.getDatabase());
        assertEquals(WORD, match.getWord());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Match#clone()}.
     */
    @Test
    public void testMatchClone() {
        Match match1, match2;
        match1 = new Match(DATABASE, WORD);
        match2 = match1.clone();
        assertNotSame(match1, match2);
        assertEquals(DATABASE, match2.getDatabase());
        assertEquals(WORD, match2.getWord());
    }
}
