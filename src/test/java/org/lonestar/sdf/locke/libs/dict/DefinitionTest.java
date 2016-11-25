/*
 * Created:  Mon 04 Feb 2013 08:01:36 PM PST
 * Modified: Fri 25 Nov 2016 03:27:53 PM PST
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
import org.lonestar.sdf.locke.libs.dict.Definition;
import org.lonestar.sdf.locke.libs.dict.Dictionary;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DefinitionTest {

    private final String DATABASE    = "wn";
    private final String DESCRIPTION = "WordNet (r) 3.0 (2006)";

    private final String WORD           = "dictionary";
    private final Dictionary DICTIONARY = new Dictionary(DATABASE, DESCRIPTION);
    private final String DEFINITION     =
        "n 1: a reference book containing an alphabetical list of words\n" +
        "     with information about them [syn: {dictionary}, {lexicon}]";

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.Definition#Definition(java.lang.String, org.lonestar.sdf.locke.libs.dict.Dictionary, java.lang.String)}.
     */
    @Test
    public void testDefinition()
    {
        Definition def = new Definition(WORD, DICTIONARY, DEFINITION);
        assertEquals(WORD, def.getWord());
        assertEquals(DICTIONARY, def.getDictionary());
        assertEquals(DEFINITION, def.getDefinition());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.Definition#clone()}.
     */
    @Test
    public void testDefinitionClone()
    {
        Definition def1;
        Definition def2;

        def1 = new Definition(WORD, DICTIONARY, DEFINITION);
        def2 = def1.clone();
        assertNotSame(def1, def2);
        assertEquals(WORD, def2.getWord());
        assertEquals(DICTIONARY, def2.getDictionary());
        assertEquals(DEFINITION, def2.getDefinition());
    }
}
