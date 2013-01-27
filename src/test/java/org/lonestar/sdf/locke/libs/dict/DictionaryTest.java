/**
 * Created:  Sat 22 Dec 2012 02:35:06 AM PST
 * Modified: Sun 30 Dec 2012 01:06:44 AM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

import static org.junit.Assert.*;
import org.junit.Test;
import org.lonestar.sdf.locke.libs.dict.Dictionary;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class DictionaryTest {

    private final String DATABASE    = "foldoc";
    private final String DESCRIPTION = "Online Dictionary of Computing";

	/**
	 * Test method for {@link org.lonestar.sdf.locke.libs.dict.Dictionary#Dictionary(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDictionary()
    {
        Dictionary dict = new Dictionary(DATABASE, DESCRIPTION);
        assertEquals(DATABASE, dict.getDatabase());
        assertEquals(DESCRIPTION, dict.getDescription());
        assertNull(dict.getDatabaseInfo());
    }


	/**
	 * Test method for {@link org.lonestar.sdf.locke.libs.dict.Dictionary#setDatabaseInfo(java.lang.String)}.
	 */
    @Test
    public void testSetDatabaseInfo()
    {
        Dictionary dict = new Dictionary(DATABASE, DESCRIPTION);
        assertNull(dict.getDatabaseInfo());
        dict.setDatabaseInfo("info\ninfo");
        assertEquals("info\ninfo", dict.getDatabaseInfo());
    }

	/**
	 * Test method for {@link org.lonestar.sdf.locke.libs.dict.Dictionary#clone()}.
	 */
    @Test
    public void testDictionaryClone()
    {
        Dictionary dict1;
        Dictionary dict2;

        dict1 = new Dictionary(DATABASE, DESCRIPTION);
        dict2 = dict1.clone();
        assertEquals(DATABASE, dict2.getDatabase());
        assertEquals(DESCRIPTION, dict2.getDescription());
        assertNull(dict2.getDatabaseInfo());
    }
}
