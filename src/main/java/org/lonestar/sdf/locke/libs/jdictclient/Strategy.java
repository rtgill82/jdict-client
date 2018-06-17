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
 * Simple class that represents a DICT match strategy.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Strategy extends Element {
    /**
     * Construct a new Strategy.
     *
     * @param name the match strategy name
     * @param description description of the match strategy
     *
     */
    public Strategy(String name, String description) {
        super(name, description);
    }

    /**
     * Construct a new Strategy from an Element.
     *
     * @param element the Element to convert into a Strategy
     *
     */
    public Strategy(Element element) {
        super(element.getKey(), element.getValue());
    }

    /**
     * Get match strategy name.
     * <p>
     * An alias for getKey().
     *
     * @return Strategy name
     *
     */
    public String getName() {
        return getKey();
    }

    /**
     * Get match strategy description.
     * <p>
     * An alias for getValue().
     *
     * @return Strategy description
     *
     */
    public String getDescription() {
        return getValue();
    }

    @Override
    public Strategy clone() {
        return new Strategy(getKey(), getValue());
    }
}
