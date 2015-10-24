/*
 * Created:  Sun 02 Dec 2012 07:06:10 PM PST
 * Modified: Tue 20 Oct 2015 03:05:11 PM PDT
 * Copyright © 2013 Robert Gill <locke@sdf.lonestar.org>
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
package org.lonestar.sdf.locke.libs.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lonestar.sdf.locke.libs.dict.Definition;
import org.lonestar.sdf.locke.libs.dict.Dictionary;
import org.lonestar.sdf.locke.libs.dict.Strategy;

/**
 * Parses and returns a DICT protocol response.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictResponse {
    /** REGEX matches a single line matching 'key "value"' */
    private static final String DICTITEM_REGEX   = "^([a-zA-Z_0-9-]+) \"(.+)\"$";

    /** REGEX matches a 151 status line preceeding definition text */
    private static final String DEFINITION_REGEX = "^151 \"(.+)\" ([a-zA-Z_0-9-]+) \"(.+)\"$";

    /** Response status code */
    private int _status;

    /** Response message */
    private String _message;

    /** Class containing response data */
    private Class _dataClass;

    /** Data returned in response, if any */
    private Object _data;

    /**
     * Read response data from response buffer.
     *
     * @param responseBuffer buffer with response data from the DICT server
     *
     * @return new DictResponse
     */
    static DictResponse read(BufferedReader responseBuffer)
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        return new DictResponse(responseBuffer);
    }

    /**
     * Construct a new DictResponse.
     *
     * @param responseBuffer buffer with response data from the DICT server
     */
    DictResponse(BufferedReader responseBuffer)
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        _status = 0;
        _message = null;
        _dataClass = null;
        _data = null;
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
                          IllegalAccessException, InvocationTargetException
    {
        switch (_status) {

        /* Connection banner */
        case 220:
            _data = DictBanner.parse(_message);
            _dataClass = DictBanner.class;
            break;

        /* SHOW DATABASES response */
        case 110:
            _data = readDictItems(responseBuffer, Dictionary.class);
            _dataClass = List.class;
            readStatusLine(responseBuffer);
            break;

        /* SHOW STRATEGIES response */
        case 111:
            _data = readDictItems(responseBuffer, Strategy.class);
            _dataClass = List.class;
            readStatusLine(responseBuffer);
            break;

        /* Information commands */
        case 112: // SHOW INFO response
        case 113: // HELP response
        case 114: // SHOW SERVER response
            _data = readInfo(responseBuffer);
            _dataClass = String.class;
            readStatusLine(responseBuffer);
            break;

        /* DEFINE response; list of definitions follows */
        case 150:
            _data = new ArrayList();
            _dataClass = List.class;

            /* Followed immediately by a 151 response */
            if (readStatusLine(responseBuffer))
                parseResponse(responseBuffer);

            break;

        /* DEFINE response; definition text follows */
        case 151:
            ((ArrayList) _data).add(readDefinition(responseBuffer));
            if (readStatusLine(responseBuffer))
                parseResponse(responseBuffer);

            break;

        /* MATCH response; list of matches follows */
        case 152:
            _data = readDictItems(responseBuffer, Match.class);
            _dataClass = List.class;
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
    int getStatus()
    {
        return _status;
    }

    /**
     * Get the full response string from the last command.
     *
     * @return full response string.
     */
    String getMessage()
    {
        return _message;
    }

    /**
     * Get data type of response data.
     *
     * @return class of data returned by getData()
     */
    Class getDataClass()
    {
        return _dataClass;
    }

    /**
     * Get response data returned by the last command.
     *
     * @return response data of type returned by getDataClass()
     */
    Object getData()
    {
        return _data;
    }

    @Override
    public String toString()
    {
        return _message;
    }

    /**
     * Read status code and message from a line in the buffer.
     *
     * If the line is a status line, _status and _message are set and the
     * method returns true.  If the line is not a status line, the buffer is
     * reset and the method returns false.
     *
     * @param responseBuffer the buffer from which to read the status line
     *
     * @return true if a status line was successfully read
     */
    private boolean readStatusLine(BufferedReader responseBuffer)
        throws IOException
    {
        String line;

        responseBuffer.mark(512);

        try {
            line = responseBuffer.readLine();
            _status = Integer.parseInt(line.substring(0, 3));
            _message = line;

            return true;

        } catch (NumberFormatException e) {
            responseBuffer.reset();
            return false;
        }
    }

    /**
     * Read a definition returned by the DEFINE command.
     *
     * Reads a single definition returned by the DEFINE command following a 151 response.
     *
     * @param responseBuffer the buffer from which to read the definition
     *
     * @return dictionary Definition
     */
    private Definition readDefinition(BufferedReader responseBuffer)
        throws IOException
    {
        Matcher matcher;
        Pattern pattern = Pattern.compile(DEFINITION_REGEX);
        Definition definition = null;

        matcher = pattern.matcher(_message);

        if (matcher.find()) {
            String word = _message.substring(matcher.start(1), matcher.end(1));
            String dict = _message.substring(matcher.start(2), matcher.end(2));
            String desc = _message.substring(matcher.start(3), matcher.end(3));
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
     *
     * This also includes matches returned by the MATCH command.
     *
     * @param responseBuffer the buffer from which to read the list of items
     *
     * @return list of DictItems
     */
    private List<DictItem> readDictItems(
            BufferedReader responseBuffer,
            Class cl
        )
        throws IOException, NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        Matcher matcher;
        String key;
        String value;

        Constructor con = null;
        if (DictItem.class.isAssignableFrom(cl)) {
            con = cl.getConstructor(String.class, String.class);
        } else {
            throw new NoSuchMethodException();
        }

        ArrayList<DictItem> arrayList = new ArrayList();
        Pattern pattern = Pattern.compile(DICTITEM_REGEX);
        String line = responseBuffer.readLine();
        while (!line.equals(".")) {
            matcher = pattern.matcher(line);

            if (matcher.find()) {
                key = line.substring(matcher.start(1), matcher.end(1));
                value = line.substring(matcher.start(2), matcher.end(2));
                arrayList.add((DictItem) con.newInstance(key, value));
            }
            line = responseBuffer.readLine();
        }

        return arrayList;
    }

    /**
     * Read text returned by an information command.
     *
     * Information commands, such as SHOW INFO, HELP, and SHOW SERVER, return
     * a long string of unstructured text. Read that text and return it as a
     * String.
     *
     * @param responseBuffer the buffer from which to read the information
     *
     * @return information string
     */
    private String readInfo(BufferedReader responseBuffer)
        throws IOException
    {
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
