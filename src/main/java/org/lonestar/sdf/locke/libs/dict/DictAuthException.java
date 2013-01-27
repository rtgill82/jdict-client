/**
 * Created:  Sun 02 Dec 2012 06:36:28 PM PST
 * Modified: Sat 29 Dec 2012 10:23:02 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

/**
 * Signals that an error occurred while authenticating with the remote DICT
 * server.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictAuthException extends DictException {

    /**
     * Construct a new DictAuthException.
     *
     * @param host    the remote host name
     * @param status  the status code returned
     * @param message the entire response string
     */
    DictAuthException(String host, int status, String message)
    {
        super(host, status, message);
    }
}
