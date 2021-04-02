/*
 * Copyright (C) 2016 Robert Gill <rtgill82@gmail.com>
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
package com.github.rtgill82.libs.jdictclient;

import java.net.ProtocolException;

/**
 * Signals that an error occurred while communicating with the remote DICT
 * server.
 *
 * @author Robert Gill &lt;rtgill82@gmail.com&gt;
 *
 */
public class DictException extends ProtocolException {
    /** DICT protocol response status code */
    private final Integer mStatus;

    /** Entire DICT protocol response message */
    private final String mMessage;

    /**
     * Construct a new DictException.
     *
     * @param host    the remote host name
     * @param status  the status code returned
     * @param message the entire response string
     *
     */
    DictException(String host, Integer status, String message) {
        super(host);
        mStatus = status;
        mMessage = message;
    }

    /**
     * Returns the remote host name where the exception occurred.
     *
     * @return the host name name of this DictException instance.
     *
     */
    public String getHost() {
        return super.getMessage();
    }

    /**
     * Returns the status code of this DictException.
     *
     * @return the status code of this DictException instance.
     *
     */
    public Integer getStatus() {
        return mStatus;
    }

    @Override
    public String getMessage() {
        return String.format("%s: %s", super.getMessage(), mMessage);
    }
}
