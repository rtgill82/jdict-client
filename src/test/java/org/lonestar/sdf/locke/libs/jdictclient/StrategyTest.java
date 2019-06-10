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

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class StrategyTest {
    private final String NAME = "regexp";
    private final String DESCRIPTION = "Old (basic) regular expressions";

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Strategy#Strategy(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testStrategy() {
        Strategy strat = new Strategy(NAME, DESCRIPTION);
        assertEquals(NAME, strat.getName());
        assertEquals(DESCRIPTION, strat.getDescription());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Strategy#Strategy(org.lonestar.sdf.locke.libs.jdictclient.Element)}.
     */
    @Test
    public void testConvertElement() {
        Element element = new Element(NAME, DESCRIPTION);
        Strategy strat = new Strategy(element);
        assertEquals(NAME, strat.getName());
        assertEquals(DESCRIPTION, strat.getDescription());
    }
}
