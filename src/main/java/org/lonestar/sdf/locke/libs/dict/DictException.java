/*
 * Created:  Sun 02 Dec 2012 06:00:55 PM PST
 * Modified: Sat 23 Feb 2013 10:17:30 PM PST
 * Copyright © 2013 Robert Gill <locke@sdf.lonestar.org>
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
	private int _status = 0;

	/** Entire DICT protocol response message */
	private String _message = null;

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
		_status  = status;
		_message = message;
	}

	/**
	 * Returns the status code of this DictException
	 *
	 * @return the status code of this DictException instance.
	 */
	public int getStatus()
	{
		return _status;
	}

	@Override
	public String getMessage()
	{
		return String.format("%s: %s", super.getMessage(), _message);
	}
}
