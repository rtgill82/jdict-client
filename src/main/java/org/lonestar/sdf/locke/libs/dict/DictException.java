/*
 * Created:  Sun 02 Dec 2012 06:00:55 PM PST
 * Modified: Fri 25 Nov 2016 03:06:45 PM PST
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
package org.lonestar.sdf.locke.libs.dict;
import java.net.ProtocolException;

/**
 * Signals that an error occurred while communicating with the remote DICT
 * server.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictException extends ProtocolException {

    /** DICT protocol response status code */
    private int status;

    /** Entire DICT protocol response message */
    private String message;

    /**
     * Construct a new DictException.
     *
     * @param host    the remote host name
     * @param status  the status code returned
     * @param message the entire response string
     */
    DictException(String host, int status, String message)
    {
        super(host);
        this.status  = status;
        this.message = message;
    }

    /**
     * Returns the remote host name where the exception occurred.
     *
     * @return the host name name of this DictException instance.
     */
    public String getHost()
    {
        return super.getMessage();
    }

    /**
     * Returns the status code of this DictException.
     *
     * @return the status code of this DictException instance.
     */
    public int getStatus()
    {
        return status;
    }

    @Override
    public String getMessage()
    {
        return String.format("%s: %s", super.getMessage(), message);
    }
}
