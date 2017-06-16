/*
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
package org.lonestar.sdf.locke.libs.jdictclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictionaryTest
{
  private final String DATABASE    = "wn";
  private final String DESCRIPTION = "WordNet (r) 3.0 (2006)";

  /**
   * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Dictionary#Dictionary(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testDictionary()
  {
    Dictionary dict = new Dictionary(DATABASE, DESCRIPTION);
    assertEquals(DATABASE, dict.getDatabase());
    assertEquals(DESCRIPTION, dict.getDescription());
    assertNull(dict.getDatabaseInfo());
  }

  /**
   * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Dictionary#Dictionary(org.lonestar.sdf.locke.libs.jdictclient.DictItem)}.
   */
  @Test
  public void testConvertDictItem()
  {
    DictItem item = new DictItem(DATABASE, DESCRIPTION);
    Dictionary dict = new Dictionary(item);
    assertEquals(DATABASE, dict.getDatabase());
    assertEquals(DESCRIPTION, dict.getDescription());
    assertNull(dict.getDatabaseInfo());
  }

  /**
   * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Dictionary#setDatabaseInfo(java.lang.String)}.
   */
  @Test
  public void testSetDatabaseInfo()
  {
    Dictionary dict = new Dictionary(DATABASE, DESCRIPTION);
    assertNull(dict.getDatabaseInfo());
    dict.setDatabaseInfo("info\ninfo");
    assertEquals("info\ninfo", dict.getDatabaseInfo());
  }

  /**
   * Test method for {@link org.lonestar.sdf.locke.libs.jdictclient.Dictionary#clone()}.
   */
  @Test
  public void testDictionaryClone()
  {
    Dictionary dict1;
    Dictionary dict2;

    dict1 = new Dictionary(DATABASE, DESCRIPTION);
    dict2 = dict1.clone();
    assertNotSame(dict1, dict2);
    assertEquals(DATABASE, dict2.getDatabase());
    assertEquals(DESCRIPTION, dict2.getDescription());
    assertNull(dict2.getDatabaseInfo());
  }
}
