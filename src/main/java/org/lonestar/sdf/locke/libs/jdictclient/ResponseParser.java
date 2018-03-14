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
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and returns a DICT protocol response.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class ResponseParser {
    /**
     * An array of response codes that return data.
     */
    private final int[] DATA_RESPONSES = {
        110, // SHOW DATABASES response
        111, // SHOW STRATEGIES response
        112, // SHOW INFO response
        113, // HELP response
        114, // SHOW SERVER response
        151, // DEFINE response; definition text follows
        152  // MATCH response; list of matches follows
    };

    /**
     * REGEX matches a single line matching 'key "value"'
     */
    private final String ELEMENT_REGEX =
        "^([^\000-\037 '\"\\\\]+) \"(.+)\"$";

    /**
     * REGEX matches a 151 status line preceeding definition text
     */
    private final String DEFINITION_REGEX =
        "^151 \"(.+)\" ([^\000-\037 '\"\\\\]+) \"(.+)\"$";

    private Connection connection;
    private BufferedReader responseBuffer;

    public ResponseParser(Connection connection) {
        this.connection = connection;
        this.responseBuffer = connection.getInputReader();
    }

    /**
     * Parse a response from the DICT server.
     */
    Response parse() throws DictConnectionException, IOException {
        Status status = readStatusLine();
        String rawData = readData(status);
        Object data = parseData(status, rawData);
        return new Response(
            status.getCode(),
            status.getMessage(),
            rawData, data
          );
    }

    /**
     * Read status code and message from a line in the buffer.
     *
     * If the line is a status line, a Status object is created and returned.
     * If the line is not a status line, the buffer is reset and the method
     * returns null.
     *
     * @return a Status object if status was successfully read
     */
    private Status readStatusLine()
          throws DictConnectionException, IOException {
        String line;
        int code;
        String message;

        responseBuffer.mark(512);
        try {
            line = responseBuffer.readLine();
            if (line == null)
              throw new DictConnectionException();
            code = Integer.parseInt(line.substring(0, 3));
            message = line;
            return new Status(code, message);
        } catch (NumberFormatException e) {
            responseBuffer.reset();
            return null;
        }
    }

    private String readData(Status status)
          throws IOException {
        for (int i : DATA_RESPONSES) {
            if (status.getCode() == i) {
                String data = new String();
                String line = responseBuffer.readLine();
                while (!line.equals(".")) {
                    data += line + "\n";
                    line = responseBuffer.readLine();
                }
                return data;
            }
        }
        return null;
    }

    private Object parseData(Status status, String rawData)
          throws IOException {
        Object data = null;
        Class cl = status.getResponseDataClass();
        if (cl != null && Element.class.isAssignableFrom(cl)) {
            data = readElements(status, stringBuffer(rawData));
        } else if (cl == Definition.class) {
            data = readDefinition(status, rawData);
        } else if (cl == String.class) {
            data = rawData;
        }
        return data;
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
     */
    private List<Element> readElements(Status status, BufferedReader buffer)
          throws IOException {
        String key, value;
        ArrayList<Element> arrayList;

        try {
            Constructor<? extends Element> con;
            Class<? extends Element> cl = status.getResponseDataClass();
            con = cl.getConstructor(String.class, String.class);

            arrayList = new ArrayList<Element>();
            Pattern pattern = Pattern.compile(ELEMENT_REGEX);
            String line = buffer.readLine();
            while (line != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    key = line.substring(matcher.start(1), matcher.end(1));
                    value = line.substring(matcher.start(2), matcher.end(2));
                    arrayList.add((Element) con.newInstance(key, value));
                }
                line = buffer.readLine();
            }
        } catch (ReflectiveOperationException e) {
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
     * @return dictionary Definition
     */
    private Definition readDefinition(Status status, String rawData) {
        Pattern pattern = Pattern.compile(DEFINITION_REGEX);
        String message = status.getMessage();
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find()) {
            throw new RuntimeException(
                "DEFINE response message does not match expected format: " +
                message);
        }

        String word = message.substring(matcher.start(1), matcher.end(1));
        String dict = message.substring(matcher.start(2), matcher.end(2));
        String desc = message.substring(matcher.start(3), matcher.end(3));
        Dictionary dictionary = new Dictionary(dict, desc);
        return new Definition(word, dictionary, rawData);
    }

    /**
     * Create buffered reader for String.
     */
    private BufferedReader stringBuffer(String str) {
        StringReader sreader = new StringReader(str);
        BufferedReader breader = new BufferedReader(sreader);
        return breader;
    }

    private class Status {
        private int code;
        private String message;

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        int getCode() {
            return code;
        }

        String getMessage() {
            return message;
        }

        Class getResponseDataClass() {
            Class cl = null;
            switch (code) {
              case 112: // SHOW INFO response
              case 113: // HELP response
              case 114: // SHOW SERVER response
                cl = String.class;
                break;

              case 110: // SHOW DATABASES response
                cl = Dictionary.class;
                break;

              case 111: // SHOW STRATEGIES response
                cl = Strategy.class;
                break;

              case 151: // DEFINE definition response
                cl = Definition.class;
                break;

              case 152: // MATCH response
                cl = Match.class;
                break;
            }
            return cl;
        }
    }
}
