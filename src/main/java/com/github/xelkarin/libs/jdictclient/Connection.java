/*
 * Copyright (C) 2018 Robert Gill <rtgill82@gmail.com>
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * A connection to a DICT host.
 *
 * @author Robert Gill &lt;rtgill82@gmail.com&gt;
 *
 */
public class Connection {
    /** The default connection port. */
    public static final int DEFAULT_PORT = 2628;

    /** The default connection timeout in milliseconds. */
    public static final int DEFAULT_TIMEOUT = 10000;

    private final String mHost;
    private final int mPort;
    private final int mTimeout;

    private Socket mSocket;
    private Banner mBanner;

    private BufferedReader mIn;
    private PrintWriter mOut;

    /**
     * Construct a new connection.
     *
     * @param host DICT host
     * @param port port number
     * @param timeout connection timeout
     *
     */
    public Connection(String host, int port, int timeout) {
        mHost = host;
        mPort = port;
        mTimeout = timeout;
        mSocket = new Socket();
    }

    /**
     * Construct a new connection.
     *
     * @param host DICT host
     *
     */
    public Connection(String host) {
        this(host, DEFAULT_PORT, DEFAULT_TIMEOUT);
    }

    /**
     * Construct a new connection.
     *
     * @param host DICT host
     * @param port port number
     *
     */
    public Connection(String host, int port) {
        this(host, port, DEFAULT_TIMEOUT);
    }

    /**
     * Construct a new connection using an already initialized socket.
     *
     * @param socket an already initialized socket
     *
     */
    public Connection(Socket socket) throws IOException {
        if (!socket.isConnected())
          throw new SocketException("Socket is not connected.");

        mHost = socket.getInetAddress().getHostName();
        mPort = socket.getPort();
        mTimeout = socket.getSoTimeout();
        mSocket = socket;
        readBanner();
    }

    /**
     * Establish a connection to the DICT host.
     *
     * @throws IOException from associated Socket
     *
     */
    public void connect() throws IOException {
        if (!mSocket.isConnected()) {
            mSocket.connect(new InetSocketAddress(mHost, mPort), mTimeout);
            readBanner();
        }
    }

    /**
     * Close the connection to the DICT host.
     *
     * @throws IOException from associated Socket
     *
     */
    public void close() throws IOException {
        mSocket.close();
    }

    /**
     * Get the initial connection banner for the host.
     *
     * @return Banner containing banner message, connection ID, etc.
     *
     */
    public Banner getBanner() {
        return mBanner;
    }

    /**
     * Get the connection ID.
     * <p>
     * Returns the connection ID for the current Connection as returned by the
     * host upon initial connection. Returns null if a connection has not been
     * established yet.
     *
     * @return connection ID string
     *
     */
    public String getId() {
        if (mBanner != null)
          return mBanner.connectionId;
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
     *
     */
    public List<String> getCapabilities() {
        if (mBanner != null)
          return mBanner.capabilities;
        return null;
    }

    /**
     * Send a command to the current host.
     *
     * @param command the Command to be sent
     * @return List of Responses for Command
     *
     * @throws IOException from associated Socket
     *
     */
    public List<Response> sendCommand(Command command)
          throws IOException {
        return command.execute(this);
    }

    @Override
    public String toString() {
        if (mPort != DEFAULT_PORT)
          return mHost + ":" + mPort;
        else
          return mHost;
    }

    public String getHost() {
        return mHost;
    }

    public int getPort() {
        return mPort;
    }

    public int getTimeout() {
        return mTimeout;
    }

    public boolean isConnected() {
        return mSocket.isConnected();
    }

    void readBanner() throws IOException {
        mIn = new BufferedReader(new InputStreamReader(getInputStream()));
        mOut = new PrintWriter(getOutputStream(), true);
        Response response = ResponseParser.parse(this);
        switch (response.getStatus()) {
          case 220: /* Connection banner */
            mBanner = (Banner) response.getData();
            break;

          case 420: /* Server temporarily unavailable */
          case 421: /* Server shutting down at operator request */
          case 530: /* Access denied */
            throw new DictServerException(
                                          mHost, response.getStatus(),
                                          response.getMessage()
            );

          default:
            throw new DictException(
                                    mHost, response.getStatus(),
                                    "Connection banner expected, received: "
                                    + response.getMessage()
            );
        }
    }

    InputStream getInputStream() throws IOException {
        return mSocket.getInputStream();
    }

    OutputStream getOutputStream() throws IOException {
        return mSocket.getOutputStream();
    }

    BufferedReader getInputReader() {
        return mIn;
    }

    PrintWriter getOutputWriter() {
        return mOut;
    }
}
