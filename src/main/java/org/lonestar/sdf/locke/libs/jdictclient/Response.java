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
 * Class containing a response from the server.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class Response {
    /**
     * The status code of the response.
     */
    private int status;

    /**
     * The full response message (including initial status code).
     */
    private String message;

    /**
     * The raw text data following the response message, if applicable.
     */
    private String rawData;

    /**
     * Parsed response data.
     */
    private Object data;

    /**
     * Construct a new Response.
     *
     * @param status response status code
     * @param message response status message
     * @param rawData raw text response data
     * @param data parsed response data
     */
    Response(int status, String message, String rawData, Object data) {
        this.status = status;
        this.message = message;
        this.rawData = rawData;
        this.data = data;
    }

    /**
     * Construct a new Response.
     *
     * @param status response status code
     * @param message response status message
     */
    Response(int status, String message) {
        this(status, message, null, null);
    }

    /**
     * Get the response status code.
     *
     * @return an int representing the response status result
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get the response status message.
     *
     * @return the response status result message (including status code)
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the raw text data associated with the response.
     *
     * @return a String containing the response data
     */
    public String getRawData() {
        return rawData;
    }

    /**
     * Get the data associated with the response.
     *
     * @return an Object representing the response data
     */
    public Object getData() {
        return data;
    }
}
