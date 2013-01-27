/**
 * Created:  Sat 29 Dec 2012 03:45:21 PM PST
 * Modified: Sat 29 Dec 2012 10:22:56 PM PST
 *
 */
package org.lonestar.sdf.locke.libs.dict;

/**
 * Simple class that represents a DICT dictionary definition.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Definition extends Object {
    /** The word being defined */
    private String _word;

    /** The dictionary database the definition was retrieved from */
    private Dictionary _dictionary;

    /** The definition provided dictionary */
    private String _definition;

    /**
     * Construct a new Definition.
     *
     * @param word       the word being defined
     * @param dictionary the dictionary the definition was retrieved from
     * @param definition the definition provided by dictionary
     */
    Definition(String word, Dictionary dictionary, String definition)
    {
        super();
        _word = word;
        _dictionary = dictionary;
        _definition = definition;
    }

    /**
     * Get defined word.
     *
     */
    public String getWord()
    {
        return _word;
    }

    /**
     * Get dictionary that defined the word.
     *
     */
    public Dictionary getDictionary()
    {
        return _dictionary;
    }

    /**
     * Get word definition.
     *
     */
    public String getDefinition()
    {
        return _definition;
    }

    public Definition clone()
    {
        Definition definition = new Definition(
                this.getWord(),
                this.getDictionary(),
                this.getDefinition()
            );

        return definition;
    }

    public String toString()
    {
        return _dictionary + "\n" + _definition;
    }
}
