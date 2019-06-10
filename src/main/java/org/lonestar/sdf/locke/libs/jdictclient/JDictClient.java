/*
 * Copyright (C) 2016-2018 Robert Gill <locke@sdf.lonestar.org>
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import org.lonestar.sdf.locke.libs.jdictclient.Command.Type;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.*;

/**
 * JDictClient: <a href="http://dict.org">DICT</a> dictionary client for Java.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class JDictClient {
    public static final int DEFAULT_PORT = Connection.DEFAULT_PORT;
    public static final int DEFAULT_TIMEOUT = Connection.DEFAULT_TIMEOUT;

    private static final String sLibraryName = getResource("library.name");
    private static final String sLibraryVendor = getResource("library.vendor");
    private static final String sLibraryVersion = getResource("library.version");
    private static String sClientString;

    private Connection mConnection;

    /**
     * Construct a new JDictClient.
     *
     * @param host DICT host
     * @param port port number
     * @param timeout connection timeout
     *
     */
    public JDictClient(String host, int port, int timeout) {
        this(new Connection(host, port, timeout));
    }

    /**
     * Construct a new JDictClient using an already initialized Connection.
     *
     * @param connection the Connection to use
     *
     */
    public JDictClient(Connection connection) {
        mConnection = connection;
        if (sClientString == null) {
            setClientString(sLibraryName + " " + sLibraryVersion);
        }
    }

    /**
     * Create a new JDictClient object and connect to specified host.
     *
     * @param host DICT host to connect to
     * @throws IOException from associated Connection Socket
     * @return new JDictClient instance
     *
     */
    public static JDictClient connect(String host) throws IOException {
        return connect(host, DEFAULT_PORT, DEFAULT_TIMEOUT);
    }

    /**
     * Create a new JDictClient object and connect to specified host and port.
     *
     * @param host DICT host to connect to
     * @param port port number to connect to
     * @throws IOException from associated Connection Socket
     * @return new JDictClient instance
     *
     */
    public static JDictClient connect(String host, int port)
          throws IOException {
        return connect(host, port, DEFAULT_TIMEOUT);
    }

    /**
     * Create a new JDictClient object and connect to specified host and port.
     *
     * @param host DICT host to connect to
     * @param port port number to connect to
     * @param timeout connection socket timeout
     * @throws IOException from associated Connection Socket
     * @return new JDictClient instance
     *
     */
    public static JDictClient connect(String host, int port, int timeout)
          throws IOException {
        JDictClient client = new JDictClient(host, port, timeout);
        client.connect();
        return client;
    }

    /**
     * Open connection to the DICT server.
     * <p>
     * If this instance is not currently connected, attempt to open a
     * connection to the server previously specified.
     *
     * @throws IOException from associated Connection Socket
     *
     */
    public void connect() throws IOException {
        mConnection.connect();
        sendClient();
    }

    /**
     * Close connection to the DICT server.
     *
     * @throws IOException from associated Connection Socket
     *
     */
    public void close() throws IOException {
        Response resp = quit();
        if (resp.getStatus() != 221) {
            throw new DictException(mConnection.getHost(),
                                    resp.getStatus(),
                                    resp.getMessage());
        }
        mConnection.close();
    }

    /**
     * Get library name.
     *
     * @return library name String
     *
     */
    public static String getLibraryName() {
        return sLibraryName;
    }

    /**
     * Get library version.
     *
     * @return library version String
     *
     */
    public static String getLibraryVersion() {
        return sLibraryVersion;
    }

    /**
     * Get library developer/vendor name.
     *
     * @return library vendor String
     *
     */
    public static String getLibraryVendor() {
        return sLibraryVendor;
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
     *
     */
    public static void setClientString(String clientString) {
        sClientString = clientString;
    }

    /**
     * Get the client connection.
     *
     * @return the current connection
     *
     */
    public Connection getConnection() {
        return mConnection;
    }

    /**
     * Send client information to DICT server.
     * <p>
     * This method is called by the connect() method. It's not necessary to use
     * it in normal practice.
     *
     * @throws IOException from associated Connection Socket
     *
     */
    private void sendClient() throws IOException {
        Command command = commandBuilder(CLIENT)
                            .setParamString(sClientString).build();
        List<Response> responses = command.execute(mConnection);
        Response resp = responses.get(0);
        if (resp.getStatus() != 250) {
            throw new DictException(mConnection.getHost(),
                                    resp.getStatus(),
                                    resp.getMessage());
        }
    }

    /**
     * Get the connection banner for the currently connected server.
     *
     * @return Banner or null if not connected
     *
     */
    public Banner getBanner() {
        return mConnection.getBanner();
    }

    /**
     * Get the server information as written by the database administrator.
     *
     * @throws IOException from associated Connection Socket
     * @return server information string
     *
     */
    public String getServerInfo() throws IOException {
        Command.Builder builder = commandBuilder(SHOW_SERVER);
        Command command = builder.build();
        List<Response> responses = command.execute(mConnection);
        return responses.get(0).getRawData();
    }

    /**
     * Get summary of server commands.
     *
     * @throws IOException from associated Connection Socket
     * @return server help string
     *
     */
    public String getHelp() throws IOException {
        Command.Builder builder = commandBuilder(HELP);
        Command command = builder.build();
        List<Response> responses = command.execute(mConnection);
        return responses.get(0).getRawData();
    }

    /**
     * Authenticate with the server.
     *
     * @param username authenticate with username
     * @param secret authenticate with password
     * @throws IOException from associated Connection Socket
     * @return true on success, false on failure
     *
     */
    public boolean authenticate(String username, String secret)
          throws IOException {
        boolean rv = false;
        Command command = commandBuilder(AUTH)
                            .setUsername(username)
                            .setPassword(secret)
                            .build();
        List<Response> responses = command.execute(mConnection);
        if (responses.get(0).getStatus() == 230)
          rv = true;
        return rv;
    }

    /**
     * Get list of available databases from the server.
     *
     * FIXME: Allow requesting that database info be set while querying server.
     *
     * @throws IOException from associated Connection Socket
     * @return list of dictionaries
     *
     */
    public List<Database> getDatabases() throws IOException {
        Command command = commandBuilder(SHOW_DATABASES).build();
        List<Response> responses = command.execute(mConnection);
        return (List<Database>) responses.get(0).getData();
    }

    /**
     * Get detailed database info for the specified database.
     *
     * @param database the database for which to get information
     * @throws IOException from associated Connection Socket
     * @return database info string
     *
     */
    public String getDatabaseInfo(String database) throws IOException {
        Command command = commandBuilder(SHOW_INFO)
                            .setDatabase(database)
                            .build();
        List<Response> responses = command.execute(mConnection);
        return responses.get(0).getRawData();
    }

    /**
     * Get detailed database info for the specified database.
     *
     * @param database the database for which to get information
     * @throws IOException from associated Connection Socket
     * @return database info string
     *
     */
    public String getDatabaseInfo(Database database) throws IOException {
        Command command = commandBuilder(SHOW_INFO)
                            .setDatabase(database.getName())
                            .build();
        List<Response> responses = command.execute(mConnection);
        return responses.get(0).getRawData();
    }

    /**
     * Get list of available match strategies from the server.
     *
     * @throws IOException from associated Connection Socket
     * @return list of strategies
     *
     */
    public List<Strategy> getStrategies() throws IOException {
        Command command = commandBuilder(SHOW_STRATEGIES).build();
        List<Response> responses = command.execute(mConnection);
        return (List<Strategy>) responses.get(0).getData();
    }

    /**
     * Get definition for word from DICT server.
     *
     * @param word the word to define
     * @throws IOException from associated Connection Socket
     * @return a list of definitions for word or null if no word found
     *
     */
    public List<Definition> define(String word) throws IOException {
        Command command = commandBuilder(DEFINE)
                            .setParamString(word)
                            .build();
        List<Response> responses = command.execute(mConnection);
        if (responses.get(0).getStatus() == 552) return null;
        return collect_definitions(responses);
    }

    /**
     * Get definition for word from DICT server.
     *
     * @param word the word to define
     * @param database the database in which to find the definition
     * @throws IOException from associated Connection Socket
     * @return a list of definitions for word or null if no word found
     *
     */
    public List<Definition> define(String word, String database)
          throws IOException {
        Command command = commandBuilder(DEFINE)
                            .setParamString(word)
                            .setDatabase(database)
                            .build();
        List<Response> responses = command.execute(mConnection);
        if (responses.get(0).getStatus() == 552) return null;
        return collect_definitions(responses);
    }

    /**
     * Match word using requested strategy.
     *
     * @param word the word to match
     * @param strategy the strategy to use for matching
     * @throws IOException from associated Connection Socket
     * @return a list of matching words and the dictionaries they are found in
     *
     */
    public List<Match> match(String word, String strategy) throws IOException {
        return match(word, strategy, null);
    }

    /**
     * Match word using requested strategy.
     *
     * @param word the word to match
     * @param strategy the strategy to use for matching
     * @param database the database to search
     * @throws IOException from associated Connection Socket
     * @return a list of matching words and the dictionaries they are found in
     *         or null if no matches found
     *
     */
    public List<Match> match(String word, String strategy, String database)
          throws IOException {
        Command command = commandBuilder(MATCH)
                            .setParamString(word)
                            .setStrategy(strategy)
                            .setDatabase(database)
                            .build();
        List<Response> responses = command.execute(mConnection);
        return (List<Match>) responses.get(0).getData();
    }

    /**
     * Send QUIT command to server.
     *
     * @throws IOException from associated Connection Socket
     *
     */
    private Response quit() throws IOException {
        Command command = commandBuilder(QUIT).build();
        List<Response> responses = command.execute(mConnection);
        return responses.get(0);
    }

    private static String getResource(String name) {
        String packageName = JDictClient.class.getPackage().getName();
        ResourceBundle rb = ResourceBundle.getBundle(packageName + ".library");
        return rb.getString(name);
    }

    private ArrayList<Definition> collect_definitions(List<Response> responses)
    {
        ListIterator<Response> itr = responses.listIterator(1);
        ArrayList<Definition> definitions = new ArrayList<>();
        while (itr.hasNext()) {
            Response response = itr.next();
            if (response.getStatus() == 151)
              definitions.add((Definition) response.getData());
        }
        return definitions;
    }

    private Command.Builder commandBuilder(Type commandType) {
        return new Command.Builder(commandType)
                          .setResponseHandler(new ThrowExceptionHandler());
    }

    private class ThrowExceptionHandler implements ResponseHandler {
        @Override
        public boolean handle(Response response) throws DictException {
            switch (response.getStatus()) {

                /*
                 * Throw server exceptions.
                 *
                 * 420 - Server temporarily unavailable
                 *
                 * 421 - Server shutting down at operator request
                 *
                 * 502 - Command not implemented
                 *
                 * 503 - Command parameter not implemented
                 *
                 * 530 - Access denied
                 *
                 */
              case 420: case 421: case 502: case 503: case 530:
                throw new DictServerException(mConnection.getHost(),
                                              response.getStatus(),
                                              response.getMessage());

                /*
                 * Throw syntax exceptions.
                 *
                 * 500 - Syntax error, command not recognized
                 *
                 * 501 - Syntax error, illegal parameters
                 *
                 * 550 - Invalid database
                 *
                 * 551 - Invalid strategy
                 *
                 */
              case 500: case 501: case 550: case 551:
                throw new DictSyntaxException(mConnection.getHost(),
                                              response.getStatus(),
                                              response.getMessage());
            }
            return true;
        }
    }
}
