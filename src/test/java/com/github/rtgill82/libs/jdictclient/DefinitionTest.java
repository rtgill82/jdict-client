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
public class DefinitionTest {
    private final String DATABASE = "wn";
    private final String DESCRIPTION = "WordNet (r) 3.0 (2006)";
    private final String WORD = "dictionary";

    private final Database DICTDB = new Database(DATABASE, DESCRIPTION);

    private final String DEFINITION =
            "n 1: a reference book containing an alphabetical list of words\n" +
            "     with information about them [syn: {dictionary}, {lexicon}]";

    /**
     * Test method for {@link com.github.rtgill82.libs.jdictclient.Definition#Definition(java.lang.String, com.github.rtgill82.libs.jdictclient.Database, java.lang.String)}.
     */
    @Test
    public void testDefinition() {
        Definition def = new Definition(WORD, DICTDB, DEFINITION);
        assertEquals(WORD, def.getWord());
        assertEquals(DICTDB, def.getDatabase());
        assertEquals(DEFINITION, def.getDefinition());
    }
}
