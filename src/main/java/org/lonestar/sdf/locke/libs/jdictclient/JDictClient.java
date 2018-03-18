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
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.CLIENT;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.DEFINE;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.HELP;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.MATCH;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.QUIT;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.SHOW_DATABASES;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.SHOW_INFO;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.SHOW_SERVER;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.SHOW_STRATEGIES;

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
    private Response resp;

    private Connection connection;
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
    public static JDictClient connect(String host) throws IOException {
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
          throws IOException  {
        JDictClient dictClient = new JDictClient(host, port, DEFAULT_TIMEOUT);
        dictClient.connect();
        return dictClient;
    }

    public static JDictClient connect(String host, int port, int timeout)
          throws IOException {
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
    public void connect() throws IOException {
        if (connection == null) {
            connection = new Connection(host, port, timeout);
            connection.connect();
            sendClient();
        }
    }

    /**
     * Close connection to the DICT server.
     *
     * @return true if successful, false if closed by remote
     */
    public boolean close() throws IOException {
        boolean rv = true;

        Response resp = quit();
        if (resp.getStatus() != 221)
          throw new DictException(host, resp.getStatus(), resp.getMessage());

        connection.close();
        in = null;
        out = null;

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
        if (this.clientString == null)
          this.clientString = clientString;
    }

    /**
     * Get the response for the last command sent.
     * <p>
     * It's overwritten by subsequent commands.
     *
     * @return the response for the last command.
     */
    public Response getResponse() {
        return resp;
    }

    /**
     * Send client information to DICT server.
     * <p>
     * This method is called by the connect() method. It's not necessary to use
     * it in normal practice.
     */
    private void sendClient() throws IOException {
        Command command = new Command.Builder(CLIENT)
                                     .setParamString(clientString).build();
        List<Response> responses = command.execute(connection);
        Response resp = responses.get(0);
        if (resp.getStatus() != 250)
          throw new DictException(host, resp.getStatus(), resp.getMessage());
    }

    /**
     * Get the connection banner for the currently connected server.
     *
     * @return Banner or null if not connected
     */
    public Banner getBanner() {
        return connection.getBanner();
    }

    /**
     * Get the server information as written by the database administrator.
     *
     * @return server information string
     */
    public String getServerInfo() throws IOException {
        Command.Builder builder = new Command.Builder(SHOW_SERVER);
        Command command = builder.build();
        List<Response> responses = command.execute(connection);
        return responses.get(0).getRawData();
    }

    /**
     * Get summary of server commands.
     *
     * @return server help string
     */
    public String getHelp() throws IOException {
        Command.Builder builder = new Command.Builder(HELP);
        Command command = builder.build();
        List<Response> responses = command.execute(connection);
        return responses.get(0).getRawData();
    }

    /**
     * FIXME: Implement
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

        /*
        MessageDigest authdigest = MessageDigest.getInstance("MD5");
        String authstring = connection.getId() + secret;
        authdigest.update(authstring.getBytes(Charset.forName("UTF-8")));
        String md5str = (new HexBinaryAdapter()).marshal(authdigest.digest());
        out.println("AUTH " + username + " " + md5str);

        resp = ResponseParser.parse(in);
        if (resp.getStatus() == 230) rv = true;
        */

        return rv;
    }

    /**
     * Get list of available dictionary databases from the server.
     *
     * FIXME: Allow requesting that dictionary info be set while querying
     * server.
     *
     * @return list of dictionaries
     */
    public List<Dictionary> getDictionaries() throws IOException {
        Command command = new Command.Builder(SHOW_DATABASES).build();
        List<Response> responses = command.execute(connection);
        return (List<Dictionary>) responses.get(0).getData();
    }

    /**
     * Get detailed dictionary info for the specified dictionary.
     *
     * @param dictionary the dictionary for which to get information
     * @return dictionary info string
     */
    public String getDictionaryInfo(String dictionary) throws IOException {
        Command command = new Command.Builder(SHOW_INFO)
                                     .setDatabase(dictionary)
                                     .build();
        List<Response> responses = command.execute(connection);
        return responses.get(0).getRawData();
    }

    /**
     * Get detailed dictionary info for the specified dictionary.
     *
     * @param dictionary the dictionary for which to get information
     * @return dictionary info string
     */
    public String getDictionaryInfo(Dictionary dictionary) throws IOException {
        Command command = new Command.Builder(SHOW_INFO)
                                     .setDatabase(dictionary.getDatabase())
                                     .build();
        List<Response> responses = command.execute(connection);
        return responses.get(0).getRawData();
    }

    /**
     * Get list of available match strategies from the server.
     *
     * @return list of strategies
     */
    public List<Strategy> getStrategies() throws IOException {
        Command command = new Command.Builder(SHOW_STRATEGIES).build();
        List<Response> responses = command.execute(connection);
        return (List<Strategy>) responses.get(0).getData();
    }

    /**
     * Get definition for word from DICT server.
     *
     * @param word the word to define
     * @return a list of definitions for word
     */
    public List<Definition> define(String word) throws IOException {
        Command command = new Command.Builder(DEFINE)
                                     .setParamString(word)
                                     .build();
        List<Response> responses = command.execute(connection);
        return collect_definitions(responses);
    }

    /**
     * Get definition for word from DICT server.
     *
     * @param dictionary the dictionary in which to find the definition
     * @param word       the word to define
     * @return a list of definitions for word
     */
    public List<Definition> define(String dictionary, String word)
          throws IOException {
        Command command = new Command.Builder(DEFINE)
                                     .setDatabase(dictionary)
                                     .setParamString(word)
                                     .build();
        List<Response> responses = command.execute(connection);
        return collect_definitions(responses);
    }

    /**
     * Match word using requested strategy.
     *
     * @param strategy the strategy to use for matching
     * @param word     the word to match
     * @return a list of matching words and the dictionaries they are found in
     */
    public List<Match> match(String strategy, String word) throws IOException {
        Command command = new Command.Builder(MATCH)
                                     .setStrategy(strategy)
                                     .setParamString(word)
                                     .build();
        List<Response> responses = command.execute(connection);
        return (List<Match>) responses.get(0).getData();
    }

    /**
     * Match word using requested strategy.
     *
     * @param dictionary the dictionary to search
     * @param strategy   the strategy to use for matching
     * @param word       the word to match
     * @return a list of matching words and the dictionaries they are found in
     */
    public List<Match> match(String dictionary, String strategy, String word)
          throws IOException {
        Command command = new Command.Builder(MATCH)
                                     .setDatabase(dictionary)
                                     .setStrategy(strategy)
                                     .setParamString(word)
                                     .build();
        List<Response> responses = command.execute(connection);
        return (List<Match>) responses.get(0).getData();
    }

    /**
     * Send QUIT command to server.
     */
    private Response quit() throws IOException {
        Command command = new Command.Builder(QUIT).build();
        List<Response> responses = command.execute(connection);
        return responses.get(0);
    }

    private ArrayList<Definition> collect_definitions(List<Response> responses)
    {
        ListIterator<Response> itr = responses.listIterator(1);
        ArrayList<Definition> definitions = new ArrayList<Definition>();
        while (itr.hasNext()) {
            Response response = itr.next();
            if (response.getStatus() == 151)
              definitions.add((Definition) response.getData());
        }
        return definitions;
    }
}
