/*
 * Created:  Fri 21 Dec 2012 11:34:24 PM PST
 * Modified: Mon 04 Feb 2013 03:19:43 PM PST
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
 * Simple class that represents a DICT dictionary database.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Dictionary extends Object {
    /** Name used to identify dictionary database */
    private String _database;

    /** Long description of dictionary database */
    private String _description;

    /** Database source, copyright and licensing information */
    private String _databaseInfo;

    /**
     * Construct a new Dictionary.
     *
     * @param database    the dictionary database name
     * @param description description of the dictionary database
     */
    Dictionary(String database, String description)
    {
        super();
        _database = database;
        _description = description;
        _databaseInfo = null;
    }

    /**
     * Get Dictionary database name.
     *
     */
    public String getDatabase()
    {
        return _database;
    }

    /**
     * Get Dictionary database description.
     *
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Get Dictionary database information.
     * Source, copyright, licensing information, etc.
     *
     */
    public String getDatabaseInfo()
    {
        return _databaseInfo;
    }

    /**
     * Set Dictionary database information.
     *
     */
    void setDatabaseInfo(String databaseInfo)
    {
        _databaseInfo = databaseInfo;
    }

    public Dictionary clone()
    {
        Dictionary dict = new Dictionary(
                this.getDatabase(),
                this.getDescription()
            );
        dict.setDatabaseInfo(this.getDatabaseInfo());

        return dict;
    }

    public String toString()
    {
        return _database + " \"" + _description + '"';
    }
}
