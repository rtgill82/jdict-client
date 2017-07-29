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
 * Simple class that represents a DICT match.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class Match extends Element {
    /**
     * Construct a new Match.
     *
     * @param dictionary the dictionary the match was found in
     * @param word       the word matching the query
     */
    public Match(String dictionary, String word) {
        super(dictionary, word);
    }

    /**
     * Construct a new Match from an Element.
     *
     * @param element the Element to convert into a Match
     */
    public Match(Element element) {
        super(element.getKey(), element.getValue());
    }

    /**
     * Get match dictionary name
     * <p>
     * An alias for getKey().
     */
    public String getDictionary() {
        return getKey();
    }

    /**
     * Get matching word
     * <p>
     * An alias for getValue().
     */
    public String getWord() {
        return getValue();
    }

    @Override
    public Match clone() {
        Match match = new Match(
            getKey(),
            getValue()
        );

        return match;
    }
}
