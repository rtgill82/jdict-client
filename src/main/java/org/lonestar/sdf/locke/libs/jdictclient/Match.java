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

/**
 * Simple class that represents a DICT match.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Match extends Element {
    /**
     * Construct a new Match.
     *
     * @param database the database the match was found in
     * @param word     the word matching the query
     *
     */
    public Match(String database, String word) {
        super(database, word);
    }

    /**
     * Construct a new Match from an Element.
     *
     * @param element the Element to convert into a Match
     *
     */
    public Match(Element element) {
        super(element.getKey(), element.getValue());
    }

    /**
     * Get match database name
     * <p>
     * An alias for getKey().
     *
     * @return the database with the matching word
     *
     */
    public String getDatabase() {
        return getKey();
    }

    /**
     * Get matching word
     * <p>
     * An alias for getValue().
     *
     * @return the matching word
     *
     */
    public String getWord() {
        return getValue();
    }
}
