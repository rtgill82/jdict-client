/**
 * Created:  Sat 08 Dec 2012 03:18:30 AM PST
 * Modified: Sat 29 Dec 2012 11:17:37 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

import static org.junit.Assert.*;
import org.junit.Test;
import org.lonestar.sdf.locke.libs.dict.DictBanner;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictBannerTest {

    private final String BANNER = "220 dictd 1.12 <auth.mime> <100@dictd.org>";
    private final String INVALID = "220 invalid banner";

	/**
	 * Test method for {@link org.lonestar.sdf.locke.libs.dict.DictBanner#DictBanner(java.lang.String)}.
	 */
	@Test
	public void testDictBanner()
    {
        DictBanner banner = new DictBanner(BANNER);
        assertEquals("dictd 1.12", banner.message);
        assertEquals("100@dictd.org", banner.connectionId);
        assertEquals(2, banner.capabilities.size());
        assertEquals(true, banner.capabilities.contains("auth"));
        assertEquals(true, banner.capabilities.contains("mime"));
    }

	/**
	 * Test method for {@link org.lonestar.sdf.locke.libs.dict.DictBanner#parse(java.lang.String)}.
	 */
    @Test
    public void testParse()
    {
        DictBanner banner = DictBanner.parse(BANNER);
        assertNotNull(banner);
    }

    /**
     * Test parsing an invalid banner string.
     *
     * Test method for {@link org.lonestar.sdf.locke.libs.dict.DictBanner#parse(java.lang.String)}
     * while passing an invalid banner string.
     */
    @Test
    public void testInvalidBanner()
    {
        DictBanner banner = DictBanner.parse(INVALID);
        assertNull(banner);
    }
}
