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
 * Simple class that represents a DICT database.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Database extends Element {
    /** Database source, copyright and licensing information */
    private String mInfo;

    /**
     * Construct a new Database.
     *
     * @param database    the dictionary database name
     * @param description description of the dictionary database
     *
     */
    public Database(String database, String description) {
        super(database, description);
    }

    /**
     * Construct a new Database with database information.
     *
     * @param database     the dictionary database name
     * @param description  description of the dictionary database
     * @param databaseInfo string describing full database information
     *
     */
    public Database(String database, String description, String databaseInfo) {
        super(database, description);
        mInfo = databaseInfo;
    }

    /**
     * Construct a new Database from an Element.
     *
     * @param element the Element to convert into a Database
     *
     */
    public Database(Element element) {
        super(element.getKey(), element.getValue());
    }

    /**
     * Get Database name.
     *
     * @return the Database name
     *
     */
    public String getName() {
        return getKey();
    }

    /**
     * Get Database description.
     *
     * @return Database description
     *
     */
    public String getDescription() {
        return getValue();
    }

    /**
     * Get Database information.
     * <p>
     * Source, copyright, licensing information, etc.
     *
     * @return Database information
     *
     */
    public String getInfo() {
        return mInfo;
    }

    /**
     * Set Database information.
     *
     * @param databaseInfo database information string
     *
     */
    void setDatabaseInfo(String databaseInfo) {
        mInfo = databaseInfo;
    }
}
