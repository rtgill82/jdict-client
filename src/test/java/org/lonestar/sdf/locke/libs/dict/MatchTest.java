/*
 * Created:  Sat 16 Mar 2013 05:57:38 PM PDT
 * Modified: Fri 25 Nov 2016 03:29:53 PM PST
 * Copyright (C) 2016 Robert Gill <locke@sdf.lonestar.org>
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
import org.lonestar.sdf.locke.libs.dict.DictItem;
import org.lonestar.sdf.locke.libs.dict.Match;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class MatchTest {

    private final String DICTIONARY = "foldoc";
    private final String WORD       = "linux";

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.Match#Match(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testMatch()
    {
        Match match = new Match(DICTIONARY, WORD);
        assertEquals(DICTIONARY, match.getDictionary());
        assertEquals(WORD, match.getWord());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.Match#Match(org.lonestar.sdf.locke.libs.dict.DictItem)}.
     */
    @Test
    public void testConvertDictItem()
    {
        DictItem item = new DictItem(DICTIONARY, WORD);
        Match match = new Match(item);
        assertEquals(DICTIONARY, match.getDictionary());
        assertEquals(WORD, match.getWord());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.Match#clone()}.
     */
    @Test
    public void testMatchClone()
    {
        Match match1;
        Match match2;

        match1 = new Match(DICTIONARY, WORD);
        match2 = match1.clone();
        assertNotSame(match1, match2);
        assertEquals(DICTIONARY, match2.getDictionary());
        assertEquals(WORD, match2.getWord());
    }
}
