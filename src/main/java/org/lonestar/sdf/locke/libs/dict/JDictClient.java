/*
 * Created:  Sun 02 Dec 2012 07:06:50 PM PST
 * Modified: Sun 08 Nov 2015 06:54:44 PM PST
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
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.lonestar.sdf.locke.libs.dict.DictBanner;

/**
 * JDictClient: <a href="http://dict.org">DICT</a> dictionary client for Java.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class JDictClient {
    public static final int DEFAULT_PORT = 2628;

    private static String _libraryName = null;
    private static String _libraryVendor = null;
    private static String _libraryVersion = null;

    private String _clientString = null;
    private String _host = null;
    private int _port = 0;
    private DictBanner _banner = null;
    private DictResponse _resp;

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
        setClientString(getLibraryName() + " " + getLibraryVersion());

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
        throws UnknownHostException, IOException, NoSuchMethodException,
                          InstantiationException, IllegalAccessException,
                          InvocationTargetException
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
        throws UnknownHostException, IOException, NoSuchMethodException,
                          InstantiationException, IllegalAccessException,
                          InvocationTargetException
    {
        JDictClient dictClient = new JDictClient(host, port);
        dictClient.connect();
        return dictClient;
    }

    /**
     * Open connection to the DICT server.
     *
     * If this instance is not currently connected, attempt to open a
     * connection to the server previously specified.
     */
    public void connect()
        throws UnknownHostException, IOException, DictException,
                          NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        DictResponse resp;

        if (_dictSocket == null) {
            _dictSocket = new Socket(_host, _port);
            _out = new PrintWriter(_dictSocket.getOutputStream(), true);
            _in  = new BufferedReader(new InputStreamReader(
                        _dictSocket.getInputStream()));

            // Save connect response so it can be requested by applications
            // using this library.
            _resp = DictResponse.read(_in);
            _banner = (DictBanner) _resp.getData();
            if (_resp.getStatus() != 220 || _resp.getData() == null) {
                throw new DictException(_host, _resp.getStatus(),
                        "Connection failed: " + _resp.getMessage());
            }

            // I don't think anyone should care about the sendClient()
            // response.
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
        throws DictException, IOException, NoSuchMethodException,
                          InstantiationException, IllegalAccessException,
                          InvocationTargetException
    {
        quit();
        _resp = DictResponse.read(_in);

        _dictSocket.close();
        _dictSocket = null;
        _in = null;
        _out = null;
        _banner = null;

        if (_resp.getStatus() != 221) {
            throw new DictException(_host, _resp.getStatus(),
                    _resp.getMessage());
        }
    }

    /**
     * Get library name.
     *
     * @return library name String
     */
    public static String getLibraryName()
    {
        if (_libraryName == null) {
            String packageName = JDictClient.class.getPackage().getName();
            ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
            _libraryName = rb.getString("library.name");
        }
        return _libraryName;
    }

    /**
     * Get library version.
     *
     * @return library version String
     */
    public static String getLibraryVersion()
    {
        if (_libraryVersion == null) {
            String packageName = JDictClient.class.getPackage().getName();
            ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
            _libraryVersion = rb.getString("library.version");
        }
        return _libraryVersion;
    }

    /**
     * Get library developer/vendor name.
     *
     * @return library vendor String
     */
    public static String getLibraryVendor()
    {
        if (_libraryVendor == null) {
            String packageName = JDictClient.class.getPackage().getName();
            ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
            _libraryVendor = rb.getString("library.vendor");
        }
        return _libraryVendor;
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
    public void setClientString(String clientString)
    {
        if (_clientString == null)
            _clientString = clientString;
    }

    /**
     * Get the response for the last command sent.
     *
     * It's overwritten by subsequent commands.
     *
     * @return the response for the last command.
     */
    public DictResponse getResponse()
    {
        return _resp;
    }

    /**
     * Send client information to DICT server.
     *
     * This method is called by the connect() method. It's not necessary to use
     * it in normal practice.
     */
    private void sendClient()
    {
        _out.println("CLIENT " + _clientString);
    }

    /**
     * Get the connection banner for the currently connected server.
     *
     * @return DictBanner or null if not connected
     */
    public DictBanner getBanner()
    {
        return _banner;
    }

    /**
     * Get the server information as written by the database administrator.
     *
     * @return server information string
     */
    public String getServerInfo()
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        _out.println("SHOW SERVER");
        _resp = DictResponse.read(_in);
        return (String) _resp.getData();
    }

    /**
     * Get summary of server commands.
     *
     * @return server help string
     */
    public String getHelp()
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        _out.println("HELP");
        _resp = DictResponse.read(_in);
        return (String) _resp.getData();
    }

    /**
     * Authenticate with the server.
     *
     * @param username authenticate with username
     * @param secret authenticate with password
     *
     * @return true on success, false on failure
     */
    public boolean authenticate(String username, String secret)
        throws IOException, NoSuchAlgorithmException, NoSuchMethodException,
                          InstantiationException, IllegalAccessException,
                          InvocationTargetException
    {
        boolean rv = false;

        MessageDigest authdigest = MessageDigest.getInstance("MD5");
        String authstring = _banner.connectionId + secret;
        authdigest.update(authstring.getBytes(Charset.forName("UTF-8")));
        String md5str = (new HexBinaryAdapter()).marshal(authdigest.digest());
        _out.println("AUTH " + username + " " + md5str);

        _resp = DictResponse.read(_in);
        if (_resp.getStatus() == 230) rv = true;

        return rv;
    }

    /**
     * Get list of available dictionary databases from the server.
     *
     * @return list of dictionaries
     */
    public List<Dictionary> getDictionaries()
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        _out.println("SHOW DATABASES");
        _resp = DictResponse.read(_in);
        return (List<Dictionary>) _resp.getData();
    }

    /**
     * Get detailed dictionary info for the specified dictionary.
     *
     * @param dictionary the dictionary for which to get information
     *
     * @return dictionary info string
     */
    public String getDictionaryInfo(String dictionary)
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        _out.println("SHOW INFO " + dictionary);
        _resp = DictResponse.read(_in);
        return (String) _resp.getData();
    }

    /**
     * Get detailed dictionary info for the specified dictionary.
     *
     * @param dictionary the dictionary for which to get information
     *
     * @return same dictionary instance with detailed info set
     */
    public Dictionary getDictionaryInfo(Dictionary dictionary)
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        String info;

        info = getDictionaryInfo(dictionary.getDatabase());
        dictionary.setDatabaseInfo(info);
        return dictionary;
    }

    /**
     * Get list of available match strategies from the server.
     *
     * @return list of strategies
     *
     */
    public List<Strategy> getStrategies()
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        _out.println("SHOW STRATEGIES");
        _resp = DictResponse.read(_in);
        return (List<Strategy>) _resp.getData();
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
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        List definitions;

        _out.println("DEFINE * \"" + word + "\"");
        _resp = DictResponse.read(_in);
        return (List<Definition>) _resp.getData();
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
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        List definitions;

        _out.println("DEFINE \"" + dictionary + "\" \"" + word + "\"");
        _resp = DictResponse.read(_in);
        return (List<Definition>) _resp.getData();
    }

    /**
     * Match word using requested strategy.
     *
     * @param word the word to match
     * @param strategy the strategy to use for matching
     *
     * @return a list of matching words and the dictionaries they are found in
     *
     */
    public List<Match> match(String strategy, String word)
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        _out.println("MATCH * \"" + strategy + "\" \"" + word + "\"");
        _resp = DictResponse.read(_in);
        return (List<Match>) _resp.getData();
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
