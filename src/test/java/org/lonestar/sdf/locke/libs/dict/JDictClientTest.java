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
package org.lonestar.sdf.locke.libs.dict;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ResourceBundle;

import org.lonestar.sdf.locke.libs.dict.JDictClient;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class JDictClientTest
{
  private static String LIBRARY_VERSION;
  private static String LIBRARY_NAME    = "JDictClient";
  private static String LIBRARY_VENDOR  =
    "Robert Gill <locke@sdf.lonestar.org>";

  @BeforeClass
  public static void oneTimeSetUp()
  {
    String packageName = JDictClient.class.getPackage().getName();
    ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
    LIBRARY_VERSION = rb.getString("library.version");
  }

  /**
   * Test method for {@link org.lonestar.sdf.locke.libs.dict.JDictClient#getLibraryName()}.
   */
  @Test
  public void testLibraryName()
  {
    assertEquals(LIBRARY_NAME, JDictClient.getLibraryName());
  }

  /**
   * Test method for {@link org.lonestar.sdf.locke.libs.dict.JDictClient#getLibraryVersion()}.
   */
  @Test
  public void testLibraryVersion()
  {
    assertEquals(LIBRARY_VERSION, JDictClient.getLibraryVersion());
  }

  /**
   * Test method for {@link org.lonestar.sdf.locke.libs.dict.JDictClient#getLibraryVendor()}.
   */
  @Test
  public void testLibraryVendor()
  {
    assertEquals(LIBRARY_VENDOR, JDictClient.getLibraryVendor());
  }
}
