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

/**
 * Simple class that represents a DICT definition.
 *
 * @author Robert Gill &lt;rtgill82@gmail.com&gt;
 *
 */
public class Definition {
    /** The word being defined */
    private final String mWord;

    /** The database the definition was retrieved from */
    private final Database mDatabase;

    /** The definition provided by the database */
    private final String mDefinition;

    /**
     * Construct a new Definition.
     *
     * @param word the word being defined
     * @param database the database the definition was retrieved from
     * @param definition the definition provided by database
     *
     */
    Definition(String word, Database database, String definition) {
        mWord = word;
        mDatabase = database;
        mDefinition = definition;
    }

    /**
     * Get defined word.
     *
     * @return the word being defined
     *
     */
    public String getWord() {
        return mWord;
    }

    /**
     * Get database that defined the word.
     *
     * @return the Database where the definition was found
     *
     */
    public Database getDatabase() {
        return mDatabase;
    }

    /**
     * Get word definition.
     *
     * @return definition for the word
     *
     */
    public String getDefinition() {
        return mDefinition;
    }

    @Override
    public String toString() {
        return mDatabase + "\n" + mDefinition;
    }
}
