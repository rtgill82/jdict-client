/**
 * Created:  Sun 02 Dec 2012 06:48:16 PM PST
 * Modified: Sat 29 Dec 2012 10:23:39 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

/**
 * Signals that an invalid command has been sent to the remote DICT server.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictSyntaxException extends DictException {

    /**
     * Construct a new DictSyntaxException.
     *
     * @param host    the remote host name
     * @param status  the status code returned
     * @param message the entire response string
     */
    DictSyntaxException(String host, int status, String message)
    {
        super(host, status, message);
    }
}
