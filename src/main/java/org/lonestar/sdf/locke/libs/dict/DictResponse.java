/*
 * Created:  Sun 02 Dec 2012 07:06:10 PM PST
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
 * Modified: Sun 27 Jan 2013 05:31:58 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lonestar.sdf.locke.libs.dict.Definition;
import org.lonestar.sdf.locke.libs.dict.Dictionary;
import org.lonestar.sdf.locke.libs.dict.Strategy;

/**
 * Parses and returns a DICT protocol response.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictResponse {
	/** REGEX matches a single line returned by the SHOW DATABASES command */
	private static final String DATABASE_REGEX   = "^([a-zA-Z_0-9-]+) \"(.+)\"$";

	/** REGEX matches a 151 status line preceeding definition text */
	private static final String DEFINITION_REGEX = "^151 \"(.+)\" ([a-zA-Z_0-9-]+) \"(.+)\"$";

	/** REGEX matches a single line returned by the SHOW STRATEGIES command */
	private static final String STRATEGY_REGEX   = DATABASE_REGEX;

	/** Response status code */
	private int _status;

	/** Response message */
	private String _message;

	/** Class containing response data */
	private Class _dataClass;

	/** Data returned in response, if any */
	private Object _data;

	/**
	 * Read response data from response buffer.
	 *
	 * @param responseBuffer buffer with response data from the DICT server
	 *
	 * @return new DictResponse
	 */
	static DictResponse read(BufferedReader responseBuffer)
		throws IOException
	{
		return new DictResponse(responseBuffer);
	}

	/**
	 * Construct a new DictResponse.
	 *
	 * @param responseBuffer buffer with response data from the DICT server
	 */
	DictResponse(BufferedReader responseBuffer)
		throws IOException
	{
		_status = 0;
		_message = null;
		_dataClass = null;
		_data = null;
		readStatusLine(responseBuffer);
		parseResponse(responseBuffer);
	}

	/**
	 * Check status set by readStatusLine and parse response data.
	 *
	 * @param responseBuffer buffer with response data from the DICT server
	 */
	private void parseResponse(BufferedReader responseBuffer)
		throws IOException
	{
		switch (_status) {

		/* Connection banner */
		case 220:
			_data = DictBanner.parse(_message);
			_dataClass = DictBanner.class;
			break;

		/* SHOW DATABASES response */
		case 110:
			_data = readDatabases(responseBuffer);
			_dataClass = List.class;
			readStatusLine(responseBuffer);
			break;

		/* SHOW STRATEGIES response */
		case 111:
			_data = readStrategies(responseBuffer);
			_dataClass = List.class;
			readStatusLine(responseBuffer);
			break;

		/* Information commands */
		case 112: // SHOW INFO response
		case 113: // HELP response *not implemented*
		case 114: // SHOW SERVER response
			_data = readInfo(responseBuffer);
			_dataClass = String.class;
			readStatusLine(responseBuffer);
			break;

		/* Authentication */
		case 130:
			/* Not implemented */
			break;

		/* DEFINE response; list of definitions follows */
		case 150:
			_data = new ArrayList();
			_dataClass = List.class;

			/* Followed immediately by a 151 response */
			if (readStatusLine(responseBuffer))
				parseResponse(responseBuffer);

			break;

		/* DEFINE response; definition text follows */
		case 151:
			((ArrayList) _data).add(readDefinition(responseBuffer));
			if (readStatusLine(responseBuffer))
				parseResponse(responseBuffer);

			break;

		/* MATCH response; list of matches follows */
		case 152:
			/* Not implemented */
			break;

		default:
			// nothing
		}
	}

	/**
	 * Get the response status code from the last command.
	 *
	 * @return DICT response status code.
	 */
	int getStatus()
	{
		return _status;
	}

	/**
	 * Get the full response string from the last command.
	 *
	 * @return full response string.
	 */
	String getMessage()
	{
		return _message;
	}

	/**
	 * Get data type of response data.
	 *
	 * @return class of data returned by getData()
	 */
	Class getDataClass()
	{
		return _dataClass;
	}

	/**
	 * Get response data returned by the last command.
	 *
	 * @return response data of type returned by getDataClass()
	 */
	Object getData()
	{
		return _data;
	}

	@Override
	public String toString()
	{
		return _message;
	}

	/**
	 * Read status code and message from a line in the buffer.
	 *
	 * If the line is a status line, _status and _message are set and the
	 * method returns true.  If the line is not a status line, the buffer is
	 * reset and the method returns false.
	 *
	 * @param responseBuffer the buffer from which to read the status line
	 *
	 * @return true if a status line was successfully read
	 */
	private boolean readStatusLine(BufferedReader responseBuffer)
		throws IOException
	{
		String line;

		responseBuffer.mark(512);

		try {
			line = responseBuffer.readLine();
			_status = Integer.parseInt(line.substring(0, 3));
			_message = line;

			return true;

		} catch (NumberFormatException e) {
			responseBuffer.reset();
			return false;
		}
	}

	/**
	 * Read a definition returned by the DEFINE command.
	 *
	 * Reads a single definition returned by the DEFINE command following a 151 response.
	 *
	 * @param responseBuffer the buffer from which to read the definition
	 *
	 * @return dictionary Definition
	 */
	private Definition readDefinition(BufferedReader responseBuffer)
		throws IOException
	{
		Matcher matcher;
		Pattern pattern = Pattern.compile(DEFINITION_REGEX);
		Definition definition = null;

		matcher = pattern.matcher(_message);

		if (matcher.find()) {
			String word = _message.substring(matcher.start(1), matcher.end(1));
			String dict = _message.substring(matcher.start(2), matcher.end(2));
			String desc = _message.substring(matcher.start(3), matcher.end(3));
			Dictionary dictionary = new Dictionary(dict, desc);

			String defstr = new String();
			String line = responseBuffer.readLine();
			while (!line.equals(".")) {
				defstr += line + "\n";
				line = responseBuffer.readLine();
			}

			definition = new Definition(word, dictionary, defstr);
		} else {
			/* FIXME: RAISE ERROR */
		}

		return definition;
	}

	/**
	 * Read dictionary databases returned by the SHOW DATABASES command.
	 *
	 * @param responseBuffer the buffer from which to read the database list
	 *
	 * @return list of dictionary databases
	 */
	private List readDatabases(BufferedReader responseBuffer)
		throws IOException
	{
		Matcher matcher;
		String database;
		String description;
		ArrayList<Dictionary> arrayList = new ArrayList();

		Pattern pattern = Pattern.compile(DATABASE_REGEX);
		String line = responseBuffer.readLine();
		while (!line.equals(".")) {
			matcher = pattern.matcher(line);

			if (matcher.find()) {
				database = line.substring(matcher.start(1), matcher.end(1));
				description = line.substring(matcher.start(2), matcher.end(2));
				arrayList.add(new Dictionary(database, description));
			}
			line = responseBuffer.readLine();
		}

		return arrayList;
	}

	/**
	 * Read match strategies returned by the SHOW STRATEGIES command.
	 *
	 * @param responseBuffer the buffer from which to read the strategy list
	 *
	 * @return list of match strategies
	 */
	private List readStrategies(BufferedReader responseBuffer)
		throws IOException
	{
		Matcher matcher;
		String name;
		String description;
		ArrayList<Strategy> arrayList = new ArrayList();

		Pattern pattern = Pattern.compile(STRATEGY_REGEX);
		String line = responseBuffer.readLine();
		while (!line.equals(".")) {
			matcher = pattern.matcher(line);

			if (matcher.find()) {
				name = line.substring(matcher.start(1), matcher.end(1));
				description = line.substring(matcher.start(2), matcher.end(2));
				arrayList.add(new Strategy(name, description));
			}
			line = responseBuffer.readLine();
		}

		return arrayList;
	}

	/**
	 * Read text returned by an information command.
	 *
	 * Information commands, such as SHOW INFO, HELP, and SHOW SERVER, return
	 * a long string of unstructured text. Read that text and return it as a
	 * String.
	 *
	 * @param responseBuffer the buffer from which to read the information
	 *
	 * @return information string
	 */
	private String readInfo(BufferedReader responseBuffer)
		throws IOException
	{
		String info;

		info = new String();
		String line = responseBuffer.readLine();
		while (!line.equals(".")) {
			info += line + "\n";
			line = responseBuffer.readLine();
		}

		return info;
	}
}
