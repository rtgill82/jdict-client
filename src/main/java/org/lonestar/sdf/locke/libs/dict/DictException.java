/**
 * Created:  Sun 02 Dec 2012 06:00:55 PM PST
 * Modified: Sat 29 Dec 2012 10:23:19 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;
import java.net.ProtocolException;

/**
 * Signals that an error occurred while communicating with the remote DICT
 * server.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictException extends ProtocolException {

    /** DICT protocol response status code */
    private int _status = 0;

    /** Entire DICT protocol response message */
    private String _message = null;

    /**
     * Construct a new DictException.
     *
     * @param host    the remote host name
     * @param status  the status code returned
     * @param message the entire response string
     */
    DictException(String host, int status, String message)
    {
        super(host);
        _status  = status;
        _message = message;
    }

    /**
     * Returns the status code of this DictException
     *
     * @return the status code of this DictException instance.
     */
    public int getStatus()
    {
        return _status;
    }

    public String getMessage()
    {
        return String.format("%s: %s", super.getMessage(), _message);
    }
}
