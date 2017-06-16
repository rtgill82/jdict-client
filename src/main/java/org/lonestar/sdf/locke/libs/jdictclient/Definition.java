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

/**
 * Simple class that represents a DICT dictionary definition.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Definition extends Object
{
  /** The word being defined */
  private String word;

  /** The dictionary database the definition was retrieved from */
  private Dictionary dictionary;

  /** The definition provided dictionary */
  private String definition;

  /**
   * Construct a new Definition.
   *
   * @param word       the word being defined
   * @param dictionary the dictionary the definition was retrieved from
   * @param definition the definition provided by dictionary
   */
  Definition(String word, Dictionary dictionary, String definition)
  {
    super();
    this.word = word;
    this.dictionary = dictionary;
    this.definition = definition;
  }

  /**
   * Get defined word.
   *
   */
  public String getWord()
  {
    return word;
  }

  /**
   * Get dictionary that defined the word.
   *
   */
  public Dictionary getDictionary()
  {
    return dictionary;
  }

  /**
   * Get word definition.
   *
   */
  public String getDefinition()
  {
    return definition;
  }

  @Override
  public Definition clone()
  {
    Definition definition = new Definition(
      getWord(),
      getDictionary(),
      getDefinition()
    );

    return definition;
  }

  @Override
  public String toString()
  {
    return dictionary + "\n" + definition;
  }
}
