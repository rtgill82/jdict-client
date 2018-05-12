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

import java.io.IOException;

/**
 * Signals that a remote DICT server has unexpectedly closed the connection.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictConnectionException extends IOException {
    private static final String MESSAGE =
            "The connection has been closed by the remote host.";

    /**
     * Construct a new DictConnectionException.
     *
     */
    DictConnectionException() {
        super(MESSAGE);
    }

    /**
     * Construct a new DictConnectionException with message.
     *
     * @param message the custom exception message
     *
     */
    DictConnectionException(String message) {
        super(message);
    }
}
