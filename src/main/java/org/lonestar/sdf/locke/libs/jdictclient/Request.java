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

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import static org.lonestar.sdf.locke.libs.jdictclient.Request.Type.AUTH;
import static org.lonestar.sdf.locke.libs.jdictclient.Request.Type.CLIENT;
import static org.lonestar.sdf.locke.libs.jdictclient.Request.Type.DEFINE;
import static org.lonestar.sdf.locke.libs.jdictclient.Request.Type.MATCH;
import static org.lonestar.sdf.locke.libs.jdictclient.Request.Type.OTHER;
import static org.lonestar.sdf.locke.libs.jdictclient.Request.Type.SHOW_INFO;

/**
 * A DICT protocol command request.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class Request {
    /** The Type of Request to be sent. */
    enum Type {
        /** Send client name and version information to remote host. */
        CLIENT,
        /** Get remote host server information. */
        SHOW_SERVER,
        /** Get help on available commands from remote host. */
        HELP,
        /** Authenticate with remote host. */
        AUTH,
        /** List available databases on remote host. */
        SHOW_DATABASES,
        /** Show information on requested database. */
        SHOW_INFO,
        /** Show available MATCH strategies on remote host. */
        SHOW_STRATEGIES,
        /** DEFINE the requested word. */
        DEFINE,
        /** Find a word using one of the available MATCH strategies. */
        MATCH,
        /** Politely disconnect from the remote host. */
        QUIT,
        /** Send a custom command the remote host. */
        OTHER
    }

    private Type type;
    private String command;
    private String param;
    private String database;
    private String strategy;

    private String username;
    private String secret;

    public Request(Type type) {
        this.type = type;

        if (type == DEFINE || type == MATCH)
          database = "*";
    }

    List<Response> execute(Connection connection) throws IOException {
        PrintWriter out = connection.getOutputWriter();
        switch(type) {
          case CLIENT:
            out.println("CLIENT " + param);
            break;

          case SHOW_SERVER:
            out.println("SHOW SERVER");
            break;

          case HELP:
            out.println("HELP");
            break;

          case AUTH:
            out.println("AUTH " + username + " " +
                                  digest_secret(connection, secret));
            break;

          case SHOW_DATABASES:
            out.println("SHOW DATABASES");
            break;

          case SHOW_INFO:
            out.println("SHOW INFO " + database);
            break;

          case SHOW_STRATEGIES:
            out.println("SHOW STRATEGIES");
            break;

          case DEFINE:
            out.println("DEFINE " + database + " \"" + param + "\"");
            break;

          case MATCH:
            out.println("MATCH " + database + " " +
                                   strategy + " \"" + param + "\"");
            break;

          case QUIT:
            out.println("QUIT");
            break;

          case OTHER:
            String cmd = command;
            if (param != null)
              cmd += " " + param;
            out.println(cmd);
            break;

          default:
            throw new RuntimeException("Invalid request type: " + type);
        }

        return parseResponse(connection);
    }

    private String digest_secret(Connection connection, String secret) {
        try {
            MessageDigest authdigest = MessageDigest.getInstance("MD5");
            String authstring = connection.getId() + secret;
            String md5str = (new HexBinaryAdapter()).marshal(authdigest.digest());
            return md5str;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Response> parseResponse(Connection connection)
          throws DictConnectionException, IOException {
        ResponseParser responseParser = new ResponseParser(connection);
        LinkedList<Response> responses = new LinkedList<Response>();
        while (true) {
            Response resp = responseParser.parse();
            responses.add(resp);
            if (resp.getStatus() == 250 || resp.getStatus() == 221)
              break;
        }
        return responses;
    }

    public static class Builder {
        private Request request;

        public Builder(Type type) {
            request = new Request(type);
        }

        public Builder setCommandString(String command) {
            if (request.type != OTHER)
              throw new RuntimeException(
                  "Command string parameter only valid for OTHER."
                );

            request.command = command;
            return this;
        }

        public Builder setParamString(String param) {
            request.param = param;
            return this;
        }

        public Builder setDatabase(String database) {
            if (request.type != DEFINE && request.type != MATCH
                && request.type != SHOW_INFO)
              throw new RuntimeException(
                  "Database parameter only valid for DEFINE, MATCH, and SHOW_INFO."
                );

            request.database = database;
            return this;
        }

        public Builder setStrategy(String strategy) {
            if (request.type != MATCH)
              throw new RuntimeException(
                  "Strategy parameter only valid for MATCH."
                );

            request.strategy = strategy;
            return this;
        }

        public Builder setUsername(String username) {
            if (request.type != AUTH)
              throw new RuntimeException(
                  "Username parameter only valid for AUTH."
                );

            request.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            if (request.type != AUTH)
              throw new RuntimeException(
                  "Password parameter only valid for AUTH."
                );

            request.secret = password;
            return this;
        }

        public Request build() {
            if (request.type == CLIENT && request.param == null)
              throw new RuntimeException("CLIENT requires a parameter string.");
            else if (request.type == AUTH && request.username == null
                     && request.secret == null)
              throw new RuntimeException("AUTH requires a username and password.");
            else if (request.type == SHOW_INFO && request.database == null)
              throw new RuntimeException("SHOW_INFO requires a database.");
            else if (request.type == MATCH && request.strategy == null)
              throw new RuntimeException("MATCH requires a strategy.");

            return request;
        }
    }
}
