/*
 * Created:  Sun 10 Mar 2013 04:19:18 PM PDT
 * Modified: Sun 10 Mar 2013 04:24:16 PM PDT
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
public class Strategy extends Object {
	/** Name used to identify match strategy */
	private String _name;

	/** Long description of match strategy */
	private String _description;

	/**
	 * Construct a new Strategy.
	 *
	 * @param name        the match strategy name
	 * @param description description of the match strategy
	 */
	public Strategy(String name, String description)
	{
		super();
		_name = name;
		_description = description;
	}

	/**
	 * Get match strategy name.
	 *
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Get match strategy description.
	 *
	 */
	public String getDescription()
	{
		return _description;
	}

	@Override
	public Strategy clone()
	{
		Strategy strat = new Strategy(
				this.getName(),
				this.getDescription()
			);

		return strat;
	}

	@Override
	public String toString()
	{
		return _name + " \"" + _description + '"';
	}
}
