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
public class DefinitionTest {
    private final String DATABASE = "wn";
    private final String DESCRIPTION = "WordNet (r) 3.0 (2006)";
    private final String WORD = "dictionary";

    private final Database DICTDB = new Database(DATABASE, DESCRIPTION);

    private final String DEFINITION =
            "n 1: a reference book containing an alphabetical list of words\n" +
            "     with information about them [syn: {dictionary}, {lexicon}]";

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Definition#Definition(java.lang.String, org.lonestar.sdf.locke.libs.jdictclient.Database, java.lang.String)}.
     */
    @Test
    public void testDefinition() {
        Definition def = new Definition(WORD, DICTDB, DEFINITION);
        assertEquals(WORD, def.getWord());
        assertEquals(DICTDB, def.getDatabase());
        assertEquals(DEFINITION, def.getDefinition());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Definition#clone()}.
     */
    @Test
    public void testDefinitionClone() {
        Definition def1, def2;
        def1 = new Definition(WORD, DICTDB, DEFINITION);
        def2 = def1.clone();
        assertNotSame(def1, def2);
        assertEquals(WORD, def2.getWord());
        assertEquals(DICTDB, def2.getDatabase());
        assertEquals(DEFINITION, def2.getDefinition());
    }
}
