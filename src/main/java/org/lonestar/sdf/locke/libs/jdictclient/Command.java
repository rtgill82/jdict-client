/*
 * Copyright (C) 2018 Robert Gill <locke@sdf.lonestar.org>
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
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * A DICT protocol command.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Command {
    /** The Type of Command to be sent. */
    public enum Type {
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
    private String param;
    private String database;
    private String strategy;
    private String command;
    private int numCommands;
    private ResponseHandler handler;

    private String username;
    private String secret;

    public Command(Type type) {
        this.type = type;
        this.numCommands = 1;

        if (type == Type.DEFINE || type == Type.MATCH)
          database = "*";
    }

    public List<Response> execute(Connection connection) throws IOException {
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
            out.println(command);
            break;

          default:
            throw new RuntimeException("Invalid command type: " + type);
        }

        return readResponses(connection);
    }

    public void setResponseHandler(ResponseHandler handler) {
        this.handler = handler;
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

    private List<Response> readResponses(Connection connection)
          throws DictConnectionException, IOException {
        ResponseParser responseParser =
          new ResponseParser(connection, numCommands);
        LinkedList<Response> responses = new LinkedList<Response>();
        while (responseParser.hasNext()) {
            boolean rv = true;
            Response response = responseParser.parse();
            if (handler != null)
              rv = handler.handle(response);
            if (rv)
              responses.add(response);
        }
        return responses;
    }

    /**
     * Constructs and initializes an instance of Command.
     *
     */
    public static class Builder {
        private Command command;

        /**
         * Construct a new Command.Builder.
         *
         * @param type the Type of Command to build.
         *
         */
        public Builder(Type type) {
            command = new Command(type);
        }

        /**
         * Set the command string for Command Type.OTHER.
         * The entire string will be sent to the server unmodified as a raw
         * command.
         *
         * @param command the raw command string to send to the server
         * @return the command builder in progress
         *
         */
        public Builder setCommandString(String command) {
            if (this.command.type != Type.OTHER)
              throw new RuntimeException(
                  "Command string parameter only valid for OTHER commands."
                );

            this.command.command = command.trim();
            this.command.numCommands = command.split("\r\n|\r|\n").length;
            return this;
        }

        /**
         * Set the parameter for a command that accepts a parameter.
         * For example, the DEFINE command accepts a word as a parameter.
         *
         * @param param the parameter to pass along with the command
         * @return the command builder in progress
         *
         */
        public Builder setParamString(String param) {
            command.param = param;
            return this;
        }

        /**
         * An alias for setParamString().
         * <p>
         * The DEFINE command passes a parameter to the server as the word to
         * be defined. This provides a more convenient alias for setting that
         * parameter.
         *
         * @param word the word to use as a parameter
         * @return the command builder in progress
         *
         */
        public Builder setWord(String word) {
            return setParamString(word);
        }

        /**
         * Set the database for a DEFINE, MATCH, or SHOW_INFO Command.
         *
         * @param database the database the command will query
         * @return the command builder in progress
         *
         */
        public Builder setDatabase(String database) {
            if (command.type != Type.DEFINE && command.type != Type.MATCH
                && command.type != Type.SHOW_INFO)
              throw new RuntimeException(
                  "Database parameter only valid for " +
                  "DEFINE, MATCH, and SHOW_INFO commands."
                );

            if (database != null)
              command.database = database;
            return this;
        }

        /**
         * Set the strategy for a MATCH Command.
         *
         * @param strategy the strategy used to match
         * @return the command builder in progress
         *
         */
        public Builder setStrategy(String strategy) {
            if (command.type != Type.MATCH)
              throw new RuntimeException(
                  "Strategy parameter only valid for MATCH commands."
                );

            command.strategy = strategy;
            return this;
        }

        /**
         * Set the username for an AUTH Command.
         *
         * @param username the authentication user name
         * @return the command builder in progress
         *
         */
        public Builder setUsername(String username) {
            if (command.type != Type.AUTH)
              throw new RuntimeException(
                  "Username parameter only valid for AUTH commands."
                );

            command.username = username;
            return this;
        }

        /**
         * Set the password for an AUTH Command.
         *
         * @param password the authenticaton password
         * @return the command builder in progress
         *
         */
        public Builder setPassword(String password) {
            if (command.type != Type.AUTH)
              throw new RuntimeException(
                  "Password parameter only valid for AUTH commands."
                );

            command.secret = password;
            return this;
        }

        public Builder setResponseHandler(ResponseHandler handler) {
            command.handler = handler;
            return this;
        }

        /**
         * Return the built Command instance.
         *
         * @return the Command instance that was built
         *
         */
        public Command build() {
            if (command.type == Type.CLIENT && command.param == null) {
                throw new RuntimeException(
                            "CLIENT command requires a parameter string."
                          );
            } else if (command.type == Type.DEFINE && command.param == null) {
                throw new RuntimeException(
                            "DEFINE command requires a word or parameter."
                          );
            } else if (command.type == Type.MATCH
                       && command.strategy == null) {
                throw new RuntimeException(
                            "MATCH command requires a strategy."
                          );
            } else if (command.type == Type.MATCH && command.param == null) {
                throw new RuntimeException(
                            "MATCH command requires a parameter."
                          );
            } else if (command.type == Type.AUTH && command.username == null
                       && command.secret == null) {
                throw new RuntimeException(
                            "AUTH command requires a username and password."
                          );
            } else if (command.type == Type.SHOW_INFO
                       && command.database == null) {
                throw new RuntimeException(
                            "SHOW_INFO command requires a database."
                          );
            } else if (command.type == Type.OTHER
                       && command.command == null) {
                throw new RuntimeException(
                            "OTHER command requires a raw command string."
                          );
            }

            return command;
        }
    }
}
