/*
 * Copyright (C) 2018 Robert Gill <locke@sdf.lonestar.org>
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
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
class ResponseStrings {
    /* Connection banner response */
    public static final
      String BANNER ="220 dictd 1.12 <auth.mime> <100@dictd.org>";

    /* Invalid connection banner */
    public static final
      String INVALID_BANNER = "220 invalid banner";

    public static final
      String INVALID_RESPONSE = "apoidfj;alskjdfijealisdjf;lasdjf";

    /* Server unavailable response */
    public static final
      String UNAVAILABLE = "420 Server temporarily unavailable";

    /* Server shutting down response */
    public static final
      String SHUTDOWN = "421 Server shutting down at operator request";

    /* Command not implemented */
    public static final
      String NOT_IMPLEMENTED = "502 Command not implemented";

    /* Server access denied response */
    public static final
      String ACCESS_DENIED = "530 Access denied";

    /* SHOW DATABASES response */
    public static final
      String DATABASES = "110 1 databases present - text follows\n" +
        "foldoc \"The Free On-line Dictionary of Computing (26 July 2010)\"" +
        "\n.\n250 ok";

    /* SHOW STRATEGIES response */
    public static final
      String STRATEGIES = "111 1 strategies present\n" +
        "exact \"Match headwords exactly\"" +
        "\n.\n250 ok";

    /* SHOW HELP response */
    public static final
      String HELP = "113 help text follows\n" +
        "DEFINE database word         -- look up word in database\n" +
        "\n.\n250 ok";

    /* SHOW INFO response */
    public static final
      String DATABASE_INFO = "112 database information follows\n" +
        "Some random information describing a database.\n" +
        "\n.\n250 ok";

    /* SHOW SERVER response */
    public static final
      String SERVER_INFO = "114 server information follows\n" +
        "Some random information describing the server.\n" +
        "\n.\n250 ok";

    /* DEFINE response */
    public static final
      String DEFINITION = "150 1 definitions retrieved\n" +
        "151 \"word\" database \"Database Description\"\n" +
        "This word is defined as a word with meaning.\n" +
        "\n.\n250 ok";

    /* MATCH response */
    public static final
      String MATCH = "152 21 matches found\n" +
        "wn \"cat\"" +
        "wn \"cat and mouse\"" +
        "\n.\n250 ok [d/m/c = 0/1/323; 0.000r 0.000u 0.000s]";

    /* AUTH success response */
    public static final
      String AUTH_SUCCESS = "230 Authentication Successful\n";

    /* AUTH fail response */
    public static final
      String AUTH_FAIL = "531 Access denied\n";

    public static final
      String PIPELINE_RESPONSE = BANNER + "\n" +
                                 SERVER_INFO + "\n" +
                                 DEFINITION;
}
