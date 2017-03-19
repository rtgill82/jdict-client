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

/**
 * Simple class that represents a DICT dictionary database.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Dictionary extends DictItem
{
  /** Database source, copyright and licensing information */
  private String databaseInfo;

  /**
   * Construct a new Dictionary.
   *
   * @param database    the dictionary database name
   * @param description description of the dictionary database
   */
  public Dictionary(String database, String description)
  {
    super(database, description);
  }

  /**
   * Construct a new Dictionary with Database Info.
   *
   * @param database     the dictionary database name
   * @param description  description of the dictionary database
   * @param databaseInfo string describing full database information
   */
  public Dictionary(String database, String description, String databaseInfo)
  {
    super(database, description);
    this.databaseInfo = databaseInfo;
  }

  /**
   * Construct a new Dictionary from a DictItem.
   *
   * @param dictItem the DictItem to convert into a Dictionary
   */
  public Dictionary(DictItem dictItem)
  {
    super(dictItem.getKey(), dictItem.getValue());
  }

  /**
   * Get Dictionary database name.
   *
   */
  public String getDatabase()
  {
    return getKey();
  }

  /**
   * Get Dictionary database description.
   *
   */
  public String getDescription()
  {
    return getValue();
  }

  /**
   * Get Dictionary database information.
   * Source, copyright, licensing information, etc.
   *
   */
  public String getDatabaseInfo()
  {
    return databaseInfo;
  }

  /**
   * Set Dictionary database information.
   *
   */
  void setDatabaseInfo(String databaseInfo)
  {
    this.databaseInfo = databaseInfo;
  }

  @Override
  public Dictionary clone()
  {
    Dictionary dict = new Dictionary(
      getKey(),
      getValue()
    );

    dict.setDatabaseInfo(this.getDatabaseInfo());
    return dict;
  }
}
