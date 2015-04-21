/*
 * Created:  Sun 10 Mar 2013 04:19:18 PM PDT
 * Modified: Mon 20 Apr 2015 08:04:38 PM PDT
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

/**
 * Simple class that represents a DICT match strategy.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Strategy extends DictItem {
    /**
     * Construct a new Strategy.
     *
     * @param name        the match strategy name
     * @param description description of the match strategy
     */
    public Strategy(String name, String description)
    {
        super(name, description);
    }

    /**
     * Construct a new Strategy from a DictItem.
     *
     * @param dictItem the DictItem to convert into a Strategy
     */
    public Strategy(DictItem dictItem)
    {
        super(dictItem.getKey(), dictItem.getValue());
    }

    /**
     * Get match strategy name.
     *
     * An alias for getKey().
     *
     */
    public String getName()
    {
        return getKey();
    }

    /**
     * Get match strategy description.
     *
     * An alias for getValue().
     *
     */
    public String getDescription()
    {
        return getValue();
    }

    @Override
    public Strategy clone()
    {
        Strategy strat = new Strategy(
                this.getKey(),
                this.getValue()
            );

        return strat;
    }
}
