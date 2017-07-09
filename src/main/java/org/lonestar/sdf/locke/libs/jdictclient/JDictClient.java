/*
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
package org.lonestar.sdf.locke.libs.jdictclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * JDictClient: <a href="http://dict.org">DICT</a> dictionary client for Java.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class JDictClient {
    public static final int DEFAULT_PORT = 2628;
    public static final int DEFAULT_TIMEOUT = 10000;

    private static String libraryName;
    private static String libraryVendor;
    private static String libraryVersion;

    private String clientString;
    private String host;
    private int port;
    private int timeout;
    private DictBanner banner;
    private DictResponse resp;

    private Socket dictSocket;
    private PrintWriter out;
    private BufferedReader in;

    private String serverInfo;
    private String capabilities;
    private String connectionId;

    /**
     * Construct a new JDictClient.
     *
     * @param host DICT host
     * @param port port number
     */
    public JDictClient(String host, int port, int timeout) {
        setClientString(getLibraryName() + " " + getLibraryVersion());

        this.host = host;
        this.port = port;
        this.timeout = timeout;
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
            InvocationTargetException {
        JDictClient dictClient =
                JDictClient.connect(host, DEFAULT_PORT, DEFAULT_TIMEOUT);
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
            InvocationTargetException {
        JDictClient dictClient = new JDictClient(host, port, DEFAULT_TIMEOUT);
        dictClient.connect();
        return dictClient;
    }

    public static JDictClient connect(String host, int port, int timeout)
            throws UnknownHostException, IOException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        JDictClient dictClient = new JDictClient(host, port, timeout);
        dictClient.connect();
        return dictClient;
    }

    /**
     * Open connection to the DICT server.
     * <p>
     * If this instance is not currently connected, attempt to open a
     * connection to the server previously specified.
     */
    public void connect()
            throws UnknownHostException, IOException, DictException,
            NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        DictResponse resp;

        if (dictSocket == null) {
            dictSocket = new Socket();
            dictSocket.connect(new InetSocketAddress(host, port), timeout);
            out = new PrintWriter(dictSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    dictSocket.getInputStream()));

            // Save connect response so it can be requested by applications
            // using this library.
            resp = DictResponse.read(in);
            banner = (DictBanner) resp.getData();
            if (resp.getStatus() != 220 || resp.getData() == null) {
                throw new DictException(host, resp.getStatus(),
                        "Connection failed: " + resp.getMessage());
            }

            // I don't think anyone should care about the sendClient()
            // response.
            sendClient();
            resp = DictResponse.read(in);
            if (resp.getStatus() != 250) {
                throw new DictException(host, resp.getStatus(),
                        resp.getMessage());
            }
        }
    }

    /**
     * Close connection to the DICT server.
     *
     * @return true if successful, false if closed by remote
     */
    public boolean close()
            throws DictException, IOException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        boolean rv = true;

        try {
            quit();
            resp = DictResponse.read(in);
            if (resp.getStatus() != 221) {
                throw new DictException(host, resp.getStatus(),
                        resp.getMessage());
            }
        } catch (DictConnectionException e) {
            rv = false;
        }

        dictSocket.close();
        dictSocket = null;
        in = null;
        out = null;
        banner = null;

        return rv;
    }

    /**
     * Get library name.
     *
     * @return library name String
     */
    public static String getLibraryName() {
        if (libraryName == null) {
            String packageName = JDictClient.class.getPackage().getName();
            ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
            libraryName = rb.getString("library.name");
        }
        return libraryName;
    }

    /**
     * Get library version.
     *
     * @return library version String
     */
    public static String getLibraryVersion() {
        if (libraryVersion == null) {
            String packageName = JDictClient.class.getPackage().getName();
            ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
            libraryVersion = rb.getString("library.version");
        }
        return libraryVersion;
    }

    /**
     * Get library developer/vendor name.
     *
     * @return library vendor String
     */
    public static String getLibraryVendor() {
        if (libraryVendor == null) {
            String packageName = JDictClient.class.getPackage().getName();
            ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
            libraryVendor = rb.getString("library.vendor");
        }
        return libraryVendor;
    }

    /**
     * Set the client string sent to the server.
     * <p>
     * Identify the client to the server with the provided string. It is
     * usually the application name and version number. This method must be
     * called before any instances of the JDictClient class are created. If
     * this method is not called, the library name and version will be used.
     *
     * @param clientString client string to send to DICT server
     */
    public void setClientString(String clientString) {
        if (clientString == null)
            clientString = clientString;
    }

    /**
     * Get the response for the last command sent.
     * <p>
     * It's overwritten by subsequent commands.
     *
     * @return the response for the last command.
     */
    public DictResponse getResponse() {
        return resp;
    }

    /**
     * Send client information to DICT server.
     * <p>
     * This method is called by the connect() method. It's not necessary to use
     * it in normal practice.
     */
    private void sendClient() {
        out.println("CLIENT " + clientString);
    }

    /**
     * Get the connection banner for the currently connected server.
     *
     * @return DictBanner or null if not connected
     */
    public DictBanner getBanner() {
        return banner;
    }

    /**
     * Get the server information as written by the database administrator.
     *
     * @return server information string
     */
    public String getServerInfo()
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        out.println("SHOW SERVER");
        resp = DictResponse.read(in);
        return (String) resp.getData();
    }

    /**
     * Get summary of server commands.
     *
     * @return server help string
     */
    public String getHelp()
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        out.println("HELP");
        resp = DictResponse.read(in);
        return (String) resp.getData();
    }

    /**
     * Authenticate with the server.
     *
     * @param username authenticate with username
     * @param secret   authenticate with password
     * @return true on success, false on failure
     */
    public boolean authenticate(String username, String secret)
            throws IOException, NoSuchAlgorithmException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        boolean rv = false;

        MessageDigest authdigest = MessageDigest.getInstance("MD5");
        String authstring = banner.connectionId + secret;
        authdigest.update(authstring.getBytes(Charset.forName("UTF-8")));
        String md5str = (new HexBinaryAdapter()).marshal(authdigest.digest());
        out.println("AUTH " + username + " " + md5str);

        resp = DictResponse.read(in);
        if (resp.getStatus() == 230) rv = true;

        return rv;
    }

    /**
     * Get list of available dictionary databases from the server.
     *
     * @return list of dictionaries
     */
    public List<Dictionary> getDictionaries()
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        out.println("SHOW DATABASES");
        resp = DictResponse.read(in);
        return (List<Dictionary>) resp.getData();
    }

    /**
     * Get detailed dictionary info for the specified dictionary.
     *
     * @param dictionary the dictionary for which to get information
     * @return dictionary info string
     */
    public String getDictionaryInfo(String dictionary)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        out.println("SHOW INFO " + dictionary);
        resp = DictResponse.read(in);
        return (String) resp.getData();
    }

    /**
     * Get detailed dictionary info for the specified dictionary.
     *
     * @param dictionary the dictionary for which to get information
     * @return same dictionary instance with detailed info set
     */
    public Dictionary getDictionaryInfo(Dictionary dictionary)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        String info;

        info = getDictionaryInfo(dictionary.getDatabase());
        dictionary.setDatabaseInfo(info);
        return dictionary;
    }

    /**
     * Get list of available match strategies from the server.
     *
     * @return list of strategies
     */
    public List<Strategy> getStrategies()
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        out.println("SHOW STRATEGIES");
        resp = DictResponse.read(in);
        return (List<Strategy>) resp.getData();
    }

    /**
     * Get definition for word from DICT server.
     *
     * @param word the word to define
     * @return a list of definitions for word
     */
    public List<Definition> define(String word)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List definitions;

        out.println("DEFINE * \"" + word + "\"");
        resp = DictResponse.read(in);
        return (List<Definition>) resp.getData();
    }

    /**
     * Get definition for word from DICT server.
     *
     * @param dictionary the dictionary in which to find the definition
     * @param word       the word to define
     * @return a list of definitions for word
     */
    public List<Definition> define(String dictionary, String word)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List definitions;

        out.println("DEFINE \"" + dictionary + "\" \"" + word + "\"");
        resp = DictResponse.read(in);
        return (List<Definition>) resp.getData();
    }

    /**
     * Match word using requested strategy.
     *
     * @param word     the word to match
     * @param strategy the strategy to use for matching
     * @return a list of matching words and the dictionaries they are found in
     */
    public List<Match> match(String strategy, String word)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        out.println("MATCH * \"" + strategy + "\" \"" + word + "\"");
        resp = DictResponse.read(in);
        return (List<Match>) resp.getData();
    }

    /**
     * Send QUIT command to server.
     */
    private void quit() {
        out.println("QUIT");
    }
}
