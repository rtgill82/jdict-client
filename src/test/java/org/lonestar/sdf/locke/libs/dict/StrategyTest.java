/*
 * Created:  Sun 10 Mar 2013 04:31:32 PM PDT
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
import org.lonestar.sdf.locke.libs.dict.DictItem;
import org.lonestar.sdf.locke.libs.dict.Strategy;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class StrategyTest {

    private final String NAME        = "regexp";
    private final String DESCRIPTION = "Old (basic) regular expressions";

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.Strategy#Strategy(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testStrategy()
    {
        Strategy strat = new Strategy(NAME, DESCRIPTION);
        assertEquals(NAME, strat.getName());
        assertEquals(DESCRIPTION, strat.getDescription());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.Strategy#Strategy(org.lonestar.sdf.locke.libs.dict.DictItem)}.
     */
    @Test
    public void testConvertDictItem()
    {
        DictItem item = new DictItem(NAME, DESCRIPTION);
        Strategy strat = new Strategy(item);
        assertEquals(NAME, strat.getName());
        assertEquals(DESCRIPTION, strat.getDescription());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.Strategy#clone()}.
     */
    @Test
    public void testStrategyClone()
    {
        Strategy strat1;
        Strategy strat2;

        strat1 = new Strategy(NAME, DESCRIPTION);
        strat2 = strat1.clone();
        assertNotSame(strat1, strat2);
        assertEquals(NAME, strat2.getName());
        assertEquals(DESCRIPTION, strat2.getDescription());
    }
}
