/*
 * Created:  Sun 02 Dec 2012 06:48:16 PM PST
 * Modified: Fri 25 Nov 2016 04:13:24 PM PST
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

/**
 * Signals that an invalid command has been sent to the remote DICT server.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictSyntaxException extends DictException
{
  /**
   * Construct a new DictSyntaxException.
   *
   * @param host    the remote host name
   * @param status  the status code returned
   * @param message the entire response string
   */
  DictSyntaxException(String host, int status, String message)
  {
    super(host, status, message);
  }
}
