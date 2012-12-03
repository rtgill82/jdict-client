/**
 * Created:  Sun 02 Dec 2012 07:06:50 PM PST
 * Modified: Sat 29 Dec 2012 10:24:55 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Iterator;
import java.util.List;

import org.lonestar.sdf.locke.libs.dict.DictBanner;

/**
 * DICT dictionary client.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictClient {
    private static final int DEFAULT_PORT = 2628;

    private String _host = null;
    private int _port = 0;

    private Socket _dictSocket = null;
    private PrintWriter _out = null;
    private BufferedReader _in = null;

    private String _serverInfo = null;
    private String _capabilities = null;
    private String _connectionId = null;

    /**
     * Construct a new DictClient.
     *
     * @param host DICT host
     * @param port port number
     */
    public DictClient(String host, int port)
    {
        _host = host;
        _port = port;
    }

    /**
     * Create a new DictClient object and connect to specified host.
     *
     * @param host DICT host to connect to
     * @return new DictClient instance
     */
    public static DictClient connect(String host)
        throws UnknownHostException, IOException
    {
        DictClient dictClient = DictClient.connect(host, DEFAULT_PORT);
        return dictClient;
    }

    /**
     * Create a new DictClient object and connect to specified host and port.
     *
     * @param host DICT host to connect to
     * @param port port number to connect to
     * @return new DictClient instance
     */
    public static DictClient connect(String host, int port)
        throws UnknownHostException, IOException
    {
        DictClient dictClient = new DictClient(host, port);
        dictClient.connect();
        return dictClient;
    }

    /**
     * Open connection to the DICT server.
     *
     * If this instance is not currently connected, attempt to open a
     * connection to the server previously specified.
     */
    public void connect()
        throws UnknownHostException, IOException, DictException
    {
        DictResponse resp;

        if (_dictSocket == null) {
            _dictSocket = new Socket(_host, _port);
            _out = new PrintWriter(_dictSocket.getOutputStream(), true);
            _in  = new BufferedReader(new InputStreamReader(
                        _dictSocket.getInputStream()));

            resp = DictResponse.read(_in);
            if (resp.getStatus() != 220 || resp.getData() == null) {
                throw new DictException(_host, resp.getStatus(),
                        "Connection failed: " + resp.getMessage());
            }

            sendClient();
            resp = DictResponse.read(_in);
            if (resp.getStatus() != 250) {
                throw new DictException(_host, resp.getStatus(),
                        resp.getMessage());
            }
        }
    }

    /**
     * Close connection to the DICT server.
     *
     */
    public void close()
        throws DictException, IOException
    {
        DictResponse resp;

        quit();
        resp = DictResponse.read(_in);

        _dictSocket.close();
        _dictSocket = null;
        _in = null;
        _out = null;

        if (resp.getStatus() != 221) {
            throw new DictException(_host, resp.getStatus(),
                    resp.getMessage());
        }
    }

    /**
     * Get list of available dictionary databases from the server.
     *
     * @return list of dictionaries
     */
    public List<Dictionary> getDictionaries()
        throws IOException
    {
        DictResponse resp;
        _out.println("SHOW DATABASES");
        resp = DictResponse.read(_in);
        return (List<Dictionary>) resp.getData();
    }

    /**
     * Get detailed dictionary info from specified dictionary.
     *
     * @param dictionary the dictionary for which to get information
     *
     * @return same dictionary instance with detailed info set
     */
    public Dictionary getDictionaryInfo(Dictionary dictionary)
        throws IOException
    {
        DictResponse resp;
        _out.println("SHOW INFO " + dictionary.getDatabase());
        resp = DictResponse.read(_in);
        dictionary.setDatabaseInfo((String) resp.getData());
        return dictionary;
    }

    /**
     * Get definition for word from DICT server.
     *
     * @param word the word to define
     *
     * @return a list of definitions for word
     *
     */
    public List<Definition> define(String word)
        throws IOException
    {
        DictResponse resp;
        List definitions;

        _out.println("DEFINE * " + word);
        resp = DictResponse.read(_in);
        return (List<Definition>) resp.getData();
    }

    /**
     * Send client information to DICT server.
     *
     */
    private void sendClient()
    {
        _out.println(
                "CLIENT  " +
                DictClientReleaseInfo.getProject() + " " +
                DictClientReleaseInfo.getVersion()
            );
    }

    /**
     * Send QUIT command to server.
     *
     */
    private void quit()
    {
        _out.println("QUIT");
    }
}
