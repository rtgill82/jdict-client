/**
 * Created:  Fri 21 Dec 2012 11:03:29 PM PST
 * Modified: Sun 27 Jan 2013 02:37:35 PM PST
 *
 */

package org.lonestar.sdf.locke.libs.dict;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import org.lonestar.sdf.locke.libs.dict.Definition;
import org.lonestar.sdf.locke.libs.dict.JDictClient;
import org.lonestar.sdf.locke.libs.dict.DictException;
import org.lonestar.sdf.locke.libs.dict.Dictionary;

public class Dict {
    public static void main(String[] args)
    {
        JDictClient dictClient;
        List<Definition> definitions;
        Definition definition;

        if (args.length > 0) {
            String word = args[0];

            try {
                dictClient = JDictClient.connect("test.dict.org");
                definitions = dictClient.define(word);
                dictClient.close();

                if (definitions != null) {
                    Iterator itr = definitions.iterator();
                    while (itr.hasNext()) {
                        definition = (Definition) itr.next();
                        System.out.println(definition);
                    }
                } else {
                    System.out.println("No definitions found.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}