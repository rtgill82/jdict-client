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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the DICT protocol connection banner.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
class Banner {
    /**
     * Regex used to match DICT protocol banner
     */
    private static final String BANNER_REGEX = "^220 (.*) <((\\w+\\.?)+)> (<.*>)$";

    /**
     * Entire DICT protocol banner
     */
    public final String banner;

    /**
     * DICT protocol banner message
     */
    public final String message;

    /**
     * Array of capabilities the remote DICT server supports
     */
    public final ArrayList<String> capabilities;

    /**
     * The remote DICT server's connection ID for this session
     */
    public final String connectionId;

    /**
     * Parse DICT protocol banner string
     *
     * @param banner the banner string returned by the remote DICT server
     * @return a new Banner object or null
     */
    static Banner parse(String bannerString) {
        Banner banner = new Banner(bannerString);
        if (banner.connectionId == null)
            return null;
        else
            return banner;
    }

    /**
     * Construct a new Banner.
     *
     * @param banner the banner string returned by the remote DICT server
     */
    Banner(String banner) {
        String capstring = null;
        String[] caparray = null;
        Pattern pattern = Pattern.compile(BANNER_REGEX);
        Matcher matcher = pattern.matcher(banner);

        if (matcher.find()) {
            this.banner = banner;
            message = banner.substring(matcher.start(1), matcher.end(1));
            connectionId = banner.substring(matcher.start(4), matcher.end(4));

            capabilities = new ArrayList<String>();
            capstring = banner.substring(matcher.start(2), matcher.end(2));
            caparray = capstring.split("\\.");
            for (int i = 0; i < caparray.length; i++)
                capabilities.add(caparray[i]);
        } else {
            // can only be assigned once, yet still need to be assigned
            this.banner = null;
            message = null;
            capabilities = null;
            connectionId = null;
        }
    }

    @Override
    public String toString() {
        return banner;
    }
}