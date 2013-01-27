/**
 * Created:  Sun 02 Dec 2012 06:45:31 PM PST
 * Modified: Sat 29 Dec 2012 10:23:32 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

/**
 * Signals that the requested DICT server is unavailable for some reason.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictServerException extends DictException {

    /**
     * Construct a new DictServerException.
     *
     * @param host    the remote host name
     * @param status  the status code returned
     * @param message the entire response string
     */
    DictServerException(String host, int status, String message)
    {
        super(host, status, message);
    }
}
