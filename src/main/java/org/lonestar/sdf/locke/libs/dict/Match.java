/*
 * Created:  Sun 10 Mar 2013 04:19:18 PM PDT
 * Modified: Sat 16 Mar 2013 05:56:47 PM PDT
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
 * Simpe class that represents a DICT match.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Match extends DictItem {
	/**
	 * Construct a new Match.
	 *
	 * @param dictionary the dictionary the match was found in
	 * @param word       the word matching the query
	 */
	public Match(String dictionary, String word)
	{
		super(dictionary, word);
	}

	/**
	 * Construct a new Match from a DictItem.
	 *
	 * @param dictItem the DictItem to convert into a Match
	 */
	public Match(DictItem dictItem)
	{
		super(dictItem.getKey(), dictItem.getValue());
	}

	/**
	 * Get match dictionary name
	 *
	 * An alias for getKey().
	 *
	 */
	public String getDictionary()
	{
		return getKey();
	}

	/**
	 * Get matching word
	 *
	 * An alias for getValue().
	 *
	 */
	public String getWord()
	{
		return getValue();
	}

	@Override
	public Match clone()
	{
		Match match = new Match(
				this.getKey(),
				this.getValue()
			);

		return match;
	}
}
