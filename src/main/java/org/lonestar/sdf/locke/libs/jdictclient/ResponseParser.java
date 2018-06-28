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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.NoSuchMethodException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses and returns a DICT protocol response.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class ResponseParser implements Iterator<Response> {
    /** An array of response codes that return data.  */
    private final int[] DATA_RESPONSES = {
        110, // SHOW DATABASES response
        111, // SHOW STRATEGIES response
        112, // SHOW INFO response
        113, // HELP response
        114, // SHOW SERVER response
        151, // DEFINE response; definition text follows
        152  // MATCH response; list of matches follows
    };

    private final Connection mConnection;
    private final BufferedReader mResponseBuffer;
    private int mNumCommands;
    private Status mStatus;

    /**
     * Construct a new ResponseParser.
     *
     * @param connection the connection to read responses from
     *
     */
    public ResponseParser(Connection connection) {
        this(connection, 1);
    }

    public ResponseParser(Connection connection, int numCommands) {
        mConnection = connection;
        mNumCommands = numCommands - 1;
        mResponseBuffer = connection.getInputReader();
    }

    /**
     * Parse a response from the DICT server.
     * <p>
     * Convenience method to parse a single Response from the server without
     * having to manually allocate a persistent ResponseParser.
     *
     * @param connection the connection to read the response from
     * @throws DictConnectionException when the connection is unexpectedly
     *         closed by the server
     * @throws IOException from associated Connection Socket
     * @return the parsed server Response
     *
     */
    public static Response parse(Connection connection)
          throws IOException {
        return new ResponseParser(connection).parse();
    }

    /**
     * Parse a response from the DICT server.
     *
     * @throws DictConnectionException when the connection is unexpectedly
     *         closed by the server
     * @throws IOException from associated Connection Socket
     * @return the parsed server Response for the most recent Command
     *
     */
    public Response parse() throws IOException {
        mStatus = readStatusLine();
        String rawData = readData(mStatus);
        Object data = parseData(mStatus, rawData);
        return new Response(
            mStatus.code,
            mStatus.message,
            mStatus.text,
            rawData, data
          );
    }

    @Override
    public Response next() {
        Response response;
        try {
            response = parse();
        } catch (IOException e) {
            throw new NoSuchElementException(e.toString());
        }
        return response;
    }

    @Override
    public boolean hasNext() {
        return mStatus == null || mStatus.hasNext();
    }

    /**
     * Read status code and message from a line in the buffer.
     *
     * If the line is a status line, a Status object is created and returned.
     * If the line is not a status line, the buffer is reset and the method
     * returns null.
     *
     * @throws DictConnectionException when the connection is unexpectedly
     *         closed by the server
     * @throws IOException from associated Connection Socket
     * @return a Status object if status was successfully read
     *
     */
    private Status readStatusLine()
          throws IOException {
        String line = mResponseBuffer.readLine();
        if (line == null)
          throw new DictConnectionException();

        try {
            int code = Integer.parseInt(line.substring(0, 3));
            String text = line.substring(4);
            return new Status(code, text, line);
        } catch (NumberFormatException e) {
            throw new DictException(mConnection.getHost(), null,
                                    "Invalid status line: " + line);
        }
    }

    private String readData(Status status)
          throws IOException {
        for (int i : DATA_RESPONSES) {
            if (status.code == i) {
                StringBuilder stringBuilder = new StringBuilder();
                String line = mResponseBuffer.readLine();
                while (!line.equals(".")) {
                    if (line.equals(".."))
                      line = ".";
                    stringBuilder.append(line).append("\n");
                    line = mResponseBuffer.readLine();
                }
                return stringBuilder.toString();
            }
        }
        return null;
    }

    private Object parseData(Status status, String rawData)
          throws IOException {
        Object data = null;
        Class clazz = status.getResponseDataClass();
        if (clazz != null && Element.class.isAssignableFrom(clazz)) {
            data = readElements(status, stringBuffer(rawData));
        } else if (clazz == Banner.class) {
            data = readBanner(status);
        } else if (clazz == Definition.class) {
            data = readDefinition(status, rawData);
        } else if (clazz == String.class) {
            data = rawData;
        }
        return data;
    }

    private Banner readBanner(Status status) {
        /* Regex used to match DICT protocol banner */
        String BANNER_REGEX = "^220 (.*) <((\\w+\\.?)+)> (<.*>)$";

        Banner banner = null;
        String message = status.message;
        Pattern pattern = Pattern.compile(BANNER_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String text = message.substring(matcher.start(1), matcher.end(1));
            String id = message.substring(matcher.start(4), matcher.end(4));
            String capString =
              message.substring(matcher.start(2), matcher.end(2));
            String[] capArray = capString.split("\\.");
            ArrayList<String> capabilities =
              new ArrayList<>(Arrays.asList(capArray));
            banner = new Banner(message, text, id, capabilities);
        }
        return banner;
    }

    /**
     * Read items returned by commands such as SHOW DATABASES and SHOW
     * STRATEGIES.
     *
     * This also includes matches returned by the MATCH command.
     *
     * @param status the response status
     * @param buffer the buffer from which to read the list of items
     * @return list of Elements
     *
     */
    private List<Element> readElements(Status status, BufferedReader buffer)
          throws IOException {
        /* REGEX matches a single line matching 'key "value"' */
        String ELEMENT_REGEX = "^([^\000-\037 '\"\\\\]+) \"(.+)\"$";

        String key, value;
        ArrayList<Element> arrayList;
        try {
            Constructor<? extends Element> con;
            Class<? extends Element> clazz = status.getResponseDataClass();
            con = clazz.getConstructor(String.class, String.class);

            arrayList = new ArrayList<>();
            Pattern pattern = Pattern.compile(ELEMENT_REGEX);
            String line = buffer.readLine();
            while (line != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    key = line.substring(matcher.start(1), matcher.end(1));
                    value = line.substring(matcher.start(2), matcher.end(2));
                    arrayList.add(con.newInstance(key, value));
                }
                line = buffer.readLine();
            }
        } catch (IllegalAccessException | InstantiationException
                 | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return arrayList;
    }


    /**
     * Read a definition returned by the DEFINE command.
     *
     * Reads a single definition returned by the DEFINE command following a 151
     * response.
     *
     * @param status the response status (151 expected)
     * @param rawData the definition string
     * @return database Definition
     *
     */
    private Definition readDefinition(Status status, String rawData) {
        /* REGEX matches a 151 status line preceding definition text */
        String DEFINITION_REGEX =
          "^151 \"(.+)\" ([^\000-\037 '\"\\\\]+) \"(.+)\"$";

        Pattern pattern = Pattern.compile(DEFINITION_REGEX);
        String message = status.message;
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find()) {
            throw new RuntimeException(
                "DEFINE response message does not match expected format: " +
                message);
        }

        String word = message.substring(matcher.start(1), matcher.end(1));
        String db = message.substring(matcher.start(2), matcher.end(2));
        String desc = message.substring(matcher.start(3), matcher.end(3));
        Database database = new Database(db, desc);
        return new Definition(word, database, rawData);
    }

    /**
     * Create buffered reader for String.
     *
     */
    private BufferedReader stringBuffer(String str) {
        StringReader stringReader = new StringReader(str);
        return new BufferedReader(stringReader);
    }

    private class Status {
        private final int code;
        private final String text;
        private final String message;

        Status(int code, String text, String message) {
            this.code = code;
            this.text = text;
            this.message = message;
        }

        boolean hasNext() {
            if (code >= 200) {
                if (mNumCommands > 0) {
                    mNumCommands -= 1;
                    return true;
                }
                return false;
            }
            return true;
        }

        Class getResponseDataClass() {
            Class clazz = null;
            switch (code) {
              case 112: // SHOW INFO response
              case 113: // HELP response
              case 114: // SHOW SERVER response
                clazz = String.class;
                break;

              case 110: // SHOW DATABASES response
                clazz = Database.class;
                break;

              case 111: // SHOW STRATEGIES response
                clazz = Strategy.class;
                break;

              case 151: // DEFINE definition response
                clazz = Definition.class;
                break;

              case 152: // MATCH response
                clazz = Match.class;
                break;

              case 220: // Connection banner
                clazz = Banner.class;
                break;
            }
            return clazz;
        }
    }
}
