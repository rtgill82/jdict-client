/*
 * Copyright (C) 2018 Robert Gill <locke@sdf.lonestar.org>
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * A connection to a DICT host.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class Connection {
    /** The default connection port. */
    public static final int DEFAULT_PORT = 2628;

    /** The default connection timeout in milliseconds. */
    public static final int DEFAULT_TIMEOUT = 10000;

    private String host;
    private int port;
    private int timeout;

    private Socket socket;
    private Banner banner;

    private BufferedReader in;
    private PrintWriter out;

    /**
     * Construct a new connection.
     *
     * @param host DICT host
     * @param port port number
     * @param timeout connection timeout
     */
    public Connection(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * Construct a new connection.
     *
     * @param host DICT host
     */
    public Connection(String host) {
        this(host, DEFAULT_PORT, DEFAULT_TIMEOUT);
    }

    /**
     * Construct a new connection.
     *
     * @param host DICT host
     * @param port port number
     */
    public Connection(String host, int port) {
        this(host, port, DEFAULT_TIMEOUT);
    }

    /**
     * Establish a connection to the DICT host.
     *
     */
    public void connect() throws IOException {
        if (socket == null) {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            in = new BufferedReader(new InputStreamReader(getInputStream()));
            out = new PrintWriter(getOutputStream(), true);
            Response response = ResponseParser.parse(this);
            if (response.getStatus() != 220) {
                throw new DictException(
                    host, response.getStatus(),
                    "Connection banner expected, received: "
                    + response.getMessage()
                  );
            }
            banner = (Banner) response.getData();
        }
    }

    /**
     * Close the connection to the DICT host.
     *
     */
    public void close() throws IOException {
        if (socket != null)
          socket.close();
    }

    /**
     * Get the initial connection banner for the host.
     *
     * @return Banner containing banner message, connection ID, etc.
     */
    public Banner getBanner() {
        return banner;
    }

    /**
     * Get the connection ID.
     * <p>
     * Returns the connection ID for the current Connection as returned by the
     * host upon initial connection. Returns null if a connection has not been
     * established yet.
     *
     * @return connection ID string
     */
    public String getId() {
        if (banner != null)
          return banner.connectionId;
        return null;
    }

    /**
     * Get connection capabilities.
     * <p>
     * Returns a list of capabilities the current host supports as returned by
     * the host upon initial connection. Returns null if a connection has not
     * been established yet.
     *
     * @return List of capabilities supported by host
     */
    public List<String> getCapabilities() {
        if (banner != null)
          return banner.capabilities;
        return null;
    }

    /**
     * Send a command request to the current host.
     *
     * @param request the Request to be sent
     * @return List of Responses for Request
     */
    public List<Response> sendRequest(Request request)
          throws IOException {
        return request.execute(this);
    }

    @Override
    public String toString() {
        if (port != DEFAULT_PORT)
          return host + ":" + port;
        else
          return host;
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    BufferedReader getInputReader() {
        return in;
    }

    PrintWriter getOutputWriter() {
        return out;
    }
}
