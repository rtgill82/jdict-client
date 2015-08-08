/*
 * Created:  Sun 10 Mar 2013 05:27:14 PM PDT
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

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictItemTest {

    private final String KEY   = "item-key";
    private final String VALUE = "This is the item value.";
    private final String ITEMSTRING = KEY + " \"" + VALUE + '"';

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.DictItem#DictItem(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testDictItem()
    {
        DictItem item = new DictItem(KEY, VALUE);
        assertEquals(KEY,   item.getKey());
        assertEquals(VALUE, item.getValue());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.DictItem#clone()}.
     */
    @Test
    public void testDictItemClone()
    {
        DictItem item1;
        DictItem item2;

        item1 = new DictItem(KEY, VALUE);
        item2 = item1.clone();
        assertNotSame(item1, item2);
        assertEquals(KEY,   item2.getKey());
        assertEquals(VALUE, item2.getValue());
    }

    /**
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.DictItem#toString()}.
     */
    @Test
    public void testDictItemToString()
    {
        DictItem item = new DictItem(KEY, VALUE);
        assertEquals(ITEMSTRING, item.toString());
    }
}