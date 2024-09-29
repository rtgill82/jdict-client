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

/**
 * Signals that the requested DICT server is unavailable for some reason.
 *
 * @author Robert Gill &lt;rtgill82@gmail.com&gt;
 *
 */
public class DictServerException extends DictException {
    /**
     * Construct a new DictServerException.
     *
     * @param host the remote host name
     * @param status the status code returned
     * @param message the entire response string
     *
     */
    DictServerException(String host, Integer status, String message) {
        super(host, status, message);
    }
}
