/*
 * Copyright (C) 2018 Robert Gill <locke@sdf.lonestar.org>
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
 * Class containing a response from the server.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Response {
    /** The status code of the response.  */
    private int status;

    /** The text of the response.  */
    private String text;

    /** The full response message (including initial status code).  */
    private String message;

    /** The raw text data following the response message, if applicable.  */
    private String rawData;

    /** Parsed response data.  */
    private Object data;

    /**
     * Construct a new Response.
     *
     * @param status response status code
     * @param message full response status message
     * @param text response text
     * @param rawData raw text response data
     * @param data parsed response data
     *
     */
    Response(int status, String message, String text,
             String rawData, Object data) {
        this.status = status;
        this.message = message;
        this.text = text;
        this.rawData = rawData;
        this.data = data;
    }

    /**
     * Construct a new Response.
     *
     * @param status response status code
     * @param message full response status message
     * @param text response text
     *
     */
    Response(int status, String message, String text) {
        this(status, message, text, null, null);
    }

    /**
     * Get the response status code.
     *
     * @return an int representing the response status result
     *
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get the response text.
     *
     * @return the response text
     *
     */
    public String getText() {
        return text;
    }

    /**
     * Get the response status message.
     *
     * @return the full response status result message (including status code)
     *
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the raw text data associated with the response.
     *
     * @return a String containing the response data
     *
     */
    public String getRawData() {
        return rawData;
    }

    /**
     * Get the data associated with the response.
     *
     * @return an Object representing the response data
     *
     */
    public Object getData() {
        return data;
    }
}
