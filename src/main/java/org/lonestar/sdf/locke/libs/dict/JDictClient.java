/*
 * Created:  Sun 02 Dec 2012 07:06:50 PM PST
 * Modified: Sun 10 Mar 2013 04:59:02 PM PDT
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.lonestar.sdf.locke.libs.dict.DictBanner;

/**
 * JDictClient: <a href="http://dict.org">DICT</a> dictionary client for Java.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class JDictClient {
	private static final int DEFAULT_PORT = 2628;

	private static String _clientString = null;

	private String _host = null;
	private int _port = 0;

	private String _libraryName;
	private String _libraryVersion;
	private String _libraryVendor;

	private Socket _dictSocket = null;
	private PrintWriter _out = null;
	private BufferedReader _in = null;

	private String _serverInfo = null;
	private String _capabilities = null;
	private String _connectionId = null;

	/**
	 * Construct a new JDictClient.
	 *
	 * @param host DICT host
	 * @param port port number
	 */
	public JDictClient(String host, int port)
	{
		String packageName = this.getClass().getPackage().getName();
		ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");

		_libraryName = rb.getString("library.name");
		_libraryVersion = rb.getString("library.version");
		_libraryVendor = rb.getString("library.vendor");
		setClientString(_libraryName + " " + _libraryVersion);

		_host = host;
		_port = port;
	}

	/**
	 * Create a new JDictClient object and connect to specified host.
	 *
	 * @param host DICT host to connect to
	 * @return new JDictClient instance
	 */
	public static JDictClient connect(String host)
		throws UnknownHostException, IOException
	{
		JDictClient dictClient = JDictClient.connect(host, DEFAULT_PORT);
		return dictClient;
	}

	/**
	 * Create a new JDictClient object and connect to specified host and port.
	 *
	 * @param host DICT host to connect to
	 * @param port port number to connect to
	 * @return new JDictClient instance
	 */
	public static JDictClient connect(String host, int port)
		throws UnknownHostException, IOException
	{
		JDictClient dictClient = new JDictClient(host, port);
		dictClient.connect();
		return dictClient;
	}

	/**
	 * Set the client string sent to the server.
	 *
	 * Identify the client to the server with the provided string. It is
	 * usually the application name and version number. This method must be
	 * called before any instances of the JDictClient class are created. If
	 * this method is not called, the library name and version will be used.
	 *
	 * @param clientString client string to send to DICT server
	 */
	public static void setClientString(String clientString)
	{
		if (_clientString == null)
			_clientString = clientString;
	}

	/**
	 * Open connection to the DICT server.
	 *
	 * If this instance is not currently connected, attempt to open a
	 * connection to the server previously specified.
	 */
	public void connect()
		throws UnknownHostException, IOException, DictException
	{
		DictResponse resp;

		if (_dictSocket == null) {
			_dictSocket = new Socket(_host, _port);
			_out = new PrintWriter(_dictSocket.getOutputStream(), true);
			_in  = new BufferedReader(new InputStreamReader(
						_dictSocket.getInputStream()));

			resp = DictResponse.read(_in);
			if (resp.getStatus() != 220 || resp.getData() == null) {
				throw new DictException(_host, resp.getStatus(),
						"Connection failed: " + resp.getMessage());
			}

			sendClient();
			resp = DictResponse.read(_in);
			if (resp.getStatus() != 250) {
				throw new DictException(_host, resp.getStatus(),
						resp.getMessage());
			}
		}
	}

	/**
	 * Close connection to the DICT server.
	 *
	 */
	public void close()
		throws DictException, IOException
	{
		DictResponse resp;

		quit();
		resp = DictResponse.read(_in);

		_dictSocket.close();
		_dictSocket = null;
		_in = null;
		_out = null;

		if (resp.getStatus() != 221) {
			throw new DictException(_host, resp.getStatus(),
					resp.getMessage());
		}
	}

	/**
	 * Get the server information as written by the database administrator.
	 *
	 * @return server information string
	 */
	public String getServerInfo()
		throws IOException
	{
		DictResponse resp;
		_out.println("SHOW SERVER");
		resp = DictResponse.read(_in);
		return (String) resp.getData();
	}

	/**
	 * Get summary of server commands.
	 *
	 * @return server help string
	 */
	public String getHelp()
		throws IOException
	{
		DictResponse resp;
		_out.println("HELP");
		resp = DictResponse.read(_in);
		return (String) resp.getData();
	}

	/**
	 * Get list of available dictionary databases from the server.
	 *
	 * @return list of dictionaries
	 */
	public List<Dictionary> getDictionaries()
		throws IOException
	{
		DictResponse resp;
		_out.println("SHOW DATABASES");
		resp = DictResponse.read(_in);
		return (List<Dictionary>) resp.getData();
	}

	/**
	 * Get detailed dictionary info for the specified dictionary.
	 *
	 * @param dictionary the dictionary for which to get information
	 *
	 * @return dictionary info string
	 */
	public String getDictionaryInfo(String dictionary)
		throws IOException
	{
		DictResponse resp;
		_out.println("SHOW INFO " + dictionary);
		resp = DictResponse.read(_in);
		return (String) resp.getData();
	}

	/**
	 * Get list of available match strategies from the server.
	 *
	 * @return list of strategies
	 *
	 */
	public List<Strategy> getStrategies()
		throws IOException
	{
		DictResponse resp;
		_out.println("SHOW STRATEGIES");
		resp = DictResponse.read(_in);
		return (List<Strategy>) resp.getData();
	}

	/**
	 * Get detailed dictionary info for the specified dictionary.
	 *
	 * @param dictionary the dictionary for which to get information
	 *
	 * @return same dictionary instance with detailed info set
	 */
	public Dictionary getDictionaryInfo(Dictionary dictionary)
		throws IOException
	{
		String info;

		info = getDictionaryInfo(dictionary.getDatabase());
		dictionary.setDatabaseInfo(info);
		return dictionary;
	}

	/**
	 * Get definition for word from DICT server.
	 *
	 * @param word the word to define
	 *
	 * @return a list of definitions for word
	 *
	 */
	public List<Definition> define(String word)
		throws IOException
	{
		DictResponse resp;
		List definitions;

		_out.println("DEFINE * \"" + word + "\"");
		resp = DictResponse.read(_in);
		return (List<Definition>) resp.getData();
	}

	/**
	 * Get definition for word from DICT server.
	 *
	 * @param dictionary the dictionary in which to find the definition
	 * @param word the word to define
	 *
	 * @return a list of definitions for word
	 *
	 */
	public List<Definition> define(String dictionary, String word)
		throws IOException
	{
		DictResponse resp;
		List definitions;

		_out.println("DEFINE \"" + dictionary + "\" \"" + word + "\"");
		resp = DictResponse.read(_in);
		return (List<Definition>) resp.getData();
	}

	/**
	 * Send client information to DICT server.
	 *
	 */
	private void sendClient()
	{
		_out.println("CLIENT " + _clientString);
	}

	/**
	 * Send QUIT command to server.
	 *
	 */
	private void quit()
	{
		_out.println("QUIT");
	}
}
