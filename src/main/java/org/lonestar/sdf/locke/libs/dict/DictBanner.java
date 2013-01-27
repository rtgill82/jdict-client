/**
 * Created:  Sun 02 Dec 2012 06:53:24 PM PST
 * Modified: Sat 29 Dec 2012 10:23:10 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the DICT protocol connection banner.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
class DictBanner {
    /** Regex used to match DICT protocol banner */
    private static final String BANNER_REGEX = "^220 (.*) <((\\w+\\.?)+)> <(.*)>$";

    /** Entire DICT protocol banner message */
    public final String message;

    /** Array of capabilities the remote DICT server supports */
    public final ArrayList<String> capabilities;

    /** The remote DICT server's connection ID for this session */
    public final String connectionId;

    /**
     * Parse DICT protocol banner string
     *
     * @param banner  the banner string returned by the remote DICT server
     * @return        a new DictBanner object or null
     */
    static DictBanner parse(String banner)
    {
        DictBanner dictBanner;

        dictBanner = new DictBanner(banner);
        if (dictBanner.connectionId == null)
            return null;
        else
            return dictBanner;
    }

    /**
     * Construct a new DictBanner.
     *
     * @param banner  the banner string returned by the remote DICT server
     */
    DictBanner(String banner)
    {
        String capstring = null;
        String[] caparray = null;
        Pattern pattern = Pattern.compile(BANNER_REGEX);
        Matcher matcher = pattern.matcher(banner);

        if (matcher.find()) {
            message = banner.substring(matcher.start(1), matcher.end(1));
            connectionId = banner.substring(matcher.start(4), matcher.end(4));

            capabilities = new ArrayList<String>();
            capstring = banner.substring(matcher.start(2), matcher.end(2));
            caparray = capstring.split("\\.");
            for (int i = 0; i < caparray.length; i++)
                capabilities.add(caparray[i]);
        } else {
            // can only be assigned once, yet still need to be assigned
            message = null;
            capabilities = null;
            connectionId = null;
        }
    }

    public String toString()
    {
        return message;
    }
}