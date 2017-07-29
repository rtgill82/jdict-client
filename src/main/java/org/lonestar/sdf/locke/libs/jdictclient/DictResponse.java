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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and returns a DICT protocol response.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 */
public class DictResponse {
    /**
     * REGEX matches a single line matching 'key "value"'
     */
    private static final String ELEMENT_REGEX =
            "^([^\000-\037 '\"\\\\]+) \"(.+)\"$";

    /**
     * REGEX matches a 151 status line preceeding definition text
     */
    private static final String DEFINITION_REGEX =
            "^151 \"(.+)\" ([^\000-\037 '\"\\\\]+) \"(.+)\"$";

    /**
     * Response status code
     */
    private int status;

    /**
     * Response message
     */
    private String message;

    /**
     * Data returned in response, if any
     */
    private Object data;

    /**
     * Read response data from response buffer.
     *
     * @param responseBuffer buffer with response data from the DICT server
     * @return new DictResponse
     */
    static DictResponse read(BufferedReader responseBuffer)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        return new DictResponse(responseBuffer);
    }

    /**
     * Construct a new DictResponse.
     *
     * @param responseBuffer buffer with response data from the DICT server
     */
    DictResponse(BufferedReader responseBuffer)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        readStatusLine(responseBuffer);
        parseResponse(responseBuffer);
    }

    /**
     * Check status set by readStatusLine and parse response data.
     *
     * @param responseBuffer buffer with response data from the DICT server
     */
    private void parseResponse(BufferedReader responseBuffer)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        switch (status) {
      /* Connection banner */
            case 220:
                data = Banner.parse(message);
                break;

      /* SHOW DATABASES response */
            case 110:
                data = readElements(responseBuffer, Dictionary.class);
                readStatusLine(responseBuffer);
                break;

      /* SHOW STRATEGIES response */
            case 111:
                data = readElements(responseBuffer, Strategy.class);
                readStatusLine(responseBuffer);
                break;

      /* Information commands */
            case 112: // SHOW INFO response
            case 113: // HELP response
            case 114: // SHOW SERVER response
                data = readInfo(responseBuffer);
                readStatusLine(responseBuffer);
                break;

      /* DEFINE response; list of definitions follows */
            case 150:
                data = new ArrayList();

        /* Followed immediately by a 151 response */
                if (readStatusLine(responseBuffer))
                    parseResponse(responseBuffer);

                break;

      /* DEFINE response; definition text follows */
            case 151:
                ((ArrayList) data).add(readDefinition(responseBuffer));
                if (readStatusLine(responseBuffer))
                    parseResponse(responseBuffer);

                break;

      /* MATCH response; list of matches follows */
            case 152:
                data = readElements(responseBuffer, Match.class);
                readStatusLine(responseBuffer);
                break;

      /* The following cases return DictResponse with no further processing. */
            case 230: /* AUTH success */
            case 531: /* AUTH failure */
                break;

            default:
                // nothing
        }
    }

    /**
     * Get the response status code from the last command.
     *
     * @return DICT response status code.
     */
    int getStatus() {
        return status;
    }

    /**
     * Get the full response string from the last command.
     *
     * @return full response string.
     */
    String getMessage() {
        return message;
    }

    /**
     * Get response data returned by the last command.
     *
     * @return response data
     */
    Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return message;
    }

    /**
     * Read status code and message from a line in the buffer.
     * <p>
     * If the line is a status line, status and message are set and the
     * method returns true.  If the line is not a status line, the buffer is
     * reset and the method returns false.
     *
     * @param responseBuffer the buffer from which to read the status line
     * @return true if a status line was successfully read
     */
    private boolean readStatusLine(BufferedReader responseBuffer)
            throws DictConnectionException, IOException {
        String line;

        responseBuffer.mark(512);

        try {
            line = responseBuffer.readLine();
            if (line == null) throw new DictConnectionException();
            status = Integer.parseInt(line.substring(0, 3));
            message = line;
            return true;
        } catch (NumberFormatException e) {
            responseBuffer.reset();
            return false;
        }
    }

    /**
     * Read a definition returned by the DEFINE command.
     * <p>
     * Reads a single definition returned by the DEFINE command following a 151 response.
     *
     * @param responseBuffer the buffer from which to read the definition
     * @return dictionary Definition
     */
    private Definition readDefinition(BufferedReader responseBuffer)
            throws IOException {
        Matcher matcher;
        Pattern pattern = Pattern.compile(DEFINITION_REGEX);
        Definition definition = null;

        matcher = pattern.matcher(message);

        if (matcher.find()) {
            String word = message.substring(matcher.start(1), matcher.end(1));
            String dict = message.substring(matcher.start(2), matcher.end(2));
            String desc = message.substring(matcher.start(3), matcher.end(3));
            Dictionary dictionary = new Dictionary(dict, desc);

            String defstr = new String();
            String line = responseBuffer.readLine();
            while (!line.equals(".")) {
                defstr += line + "\n";
                line = responseBuffer.readLine();
            }

            definition = new Definition(word, dictionary, defstr);
        } else {
        /* FIXME: RAISE ERROR */
        }

        return definition;
    }

    /**
     * Read items returned by commands such as SHOW DATABASES and SHOW
     * STRATEGIES.
     * <p>
     * This also includes matches returned by the MATCH command.
     *
     * @param responseBuffer the buffer from which to read the list of items
     * @return list of Elements
     */
    private List<Element> readElements(BufferedReader responseBuffer,
                                       Class<? extends Element> cl)
            throws IOException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Matcher matcher;
        String key;
        String value;

        Constructor<? extends Element> con = null;
        if (Element.class.isAssignableFrom(cl)) {
            con = cl.getConstructor(String.class, String.class);
        } else {
            throw new NoSuchMethodException();
        }

        ArrayList<Element> arrayList = new ArrayList<Element>();
        Pattern pattern = Pattern.compile(ELEMENT_REGEX);
        String line = responseBuffer.readLine();
        while (!line.equals(".")) {
            matcher = pattern.matcher(line);

            if (matcher.find()) {
                key = line.substring(matcher.start(1), matcher.end(1));
                value = line.substring(matcher.start(2), matcher.end(2));
                arrayList.add((Element) con.newInstance(key, value));
            }
            line = responseBuffer.readLine();
        }

        return arrayList;
    }

    /**
     * Read text returned by an information command.
     * <p>
     * Information commands, such as SHOW INFO, HELP, and SHOW SERVER, return
     * a long string of unstructured text. Read that text and return it as a
     * String.
     *
     * @param responseBuffer the buffer from which to read the information
     * @return information string
     */
    private String readInfo(BufferedReader responseBuffer)
            throws IOException {
        String info;

        info = new String();
        String line = responseBuffer.readLine();
        while (!line.equals(".")) {
            info += line + "\n";
            line = responseBuffer.readLine();
        }

        return info;
    }
}