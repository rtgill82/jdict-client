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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and returns a DICT protocol response.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class ResponseParser implements Iterator<Response> {
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
     * Regex used to match DICT protocol banner
     */
    private final String BANNER_REGEX =
        "^220 (.*) <((\\w+\\.?)+)> (<.*>)$";

    /**
     * REGEX matches a 151 status line preceding definition text
     */
    private final String DEFINITION_REGEX =
        "^151 \"(.+)\" ([^\000-\037 '\"\\\\]+) \"(.+)\"$";

    /**
     * REGEX matches a single line matching 'key "value"'
     */
    private final String ELEMENT_REGEX =
        "^([^\000-\037 '\"\\\\]+) \"(.+)\"$";

    private Connection connection;
    private BufferedReader responseBuffer;
    private Status status;

    /**
     * Construct a new ResponseParser.
     *
     * @param connection the connection to read responses from
     */
    public ResponseParser(Connection connection) {
        this.connection = connection;
        this.responseBuffer = connection.getInputReader();
    }

    /**
     * Parse a response from the DICT server.
     * <p>
     * Convenience method to parse a single Response from the server without
     * having to manually allocate a persistent ResponseParser.
     *
     * @param connection the connection to read the response from
     */
    public static Response parse(Connection connection)
          throws DictConnectionException, IOException {
        return new ResponseParser(connection).parse();
    }

    /**
     * Parse a response from the DICT server.
     */
    public Response parse() throws DictConnectionException, IOException {
        status = readStatusLine();
        String rawData = readData(status);
        Object data = parseData(status, rawData);
        return new Response(
            status.code,
            status.message,
            status.text,
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
        if (status != null)
          return status.hasNext();
        return true;
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
        responseBuffer.mark(512);
        try {
            String line = responseBuffer.readLine();
            if (line == null)
              throw new DictConnectionException();
            int code = Integer.parseInt(line.substring(0, 3));
            String text = line.substring(4);
            return new Status(code, text, line);
        } catch (NumberFormatException e) {
            responseBuffer.reset();
            return null;
        }
    }

    private String readData(Status status)
          throws IOException {
        for (int i : DATA_RESPONSES) {
            if (status.code == i) {
                String data = new String();
                String line = responseBuffer.readLine();
                while (!line.equals(".")) {
                    if (line.equals(".."))
                      line = ".";
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
        } else if (cl == Banner.class) {
            data = readBanner(status);
        } else if (cl == Definition.class) {
            data = readDefinition(status, rawData);
        } else if (cl == String.class) {
            data = rawData;
        }
        return data;
    }

    private Banner readBanner(Status status) {
        Banner banner = null;
        String message = status.message;
        Pattern pattern = Pattern.compile(BANNER_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String text = message.substring(matcher.start(1), matcher.end(1));
            String id = message.substring(matcher.start(4), matcher.end(4));
            ArrayList<String> capabilities = new ArrayList<String>();
            String capstring =
              message.substring(matcher.start(2), matcher.end(2));
            String[] caparray = capstring.split("\\.");
            for (int i = 0; i < caparray.length; i++)
              capabilities.add(caparray[i]);
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
        String message = status.message;
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
        private final int code;
        private final String text;
        private final String message;

        Status(int code, String text, String message) {
            this.code = code;
            this.text = text;
            this.message = message;
        }

        boolean hasNext() {
            if (code >= 200)
              return false;
            return true;
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

              case 220: // Connection banner
                cl = Banner.class;
                break;
            }
            return cl;
        }
    }
}
