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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the DICT protocol connection banner.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
class Banner {
    /** DICT protocol banner message */
    public final String message;

    /** The text portion of the banner message */
    public final String text;

    /** The remote DICT server's connection ID for this session */
    public final String connectionId;

    /** Array of capabilities the remote DICT server supports */
    public final List<String> capabilities;

    /**
     * Construct a new Banner.
     *
     * @param message the full banner message
     * @param text the text portion of the banner message
     * @param connectionId the server's connection ID
     * @param capabilities a list of capabilities the server supports
     *
     */
    public Banner(String message, String text,
                  String connectionId, ArrayList<String> capabilities) {
        this.message = message;
        this.text = text;
        this.connectionId = connectionId;
        this.capabilities = Collections.unmodifiableList(capabilities);
    }

    @Override
    public String toString() {
        return message;
    }
}
