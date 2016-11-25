/*
 * Created:  Fri 21 Dec 2012 11:03:29 PM PST
 * Modified: Fri 25 Nov 2016 03:09:11 PM PST
 * Copyright (C) 2016 Robert Gill <locke@sdf.lonestar.org>
 *
 * This file is part of JDictClient.
 *
 * JDictClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * JDictClient is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with JDictClient.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.lonestar.sdf.locke.libs.dict;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.lonestar.sdf.locke.libs.dict.Definition;
import org.lonestar.sdf.locke.libs.dict.DictException;
import org.lonestar.sdf.locke.libs.dict.Dictionary;
import org.lonestar.sdf.locke.libs.dict.JDictClient;
import org.lonestar.sdf.locke.libs.dict.Match;
import org.lonestar.sdf.locke.libs.dict.Strategy;

/**
 * Simple command line program that uses features of the library.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Dict {
    private static void showHelp()
    {
        System.out.println("Usage: Dict [options] [word]\n");
        System.out.println("General Options");
        System.out.println("\t-version\t\t\tDisplay program/library version");
        System.out.println("\t-host <server>\t\t\tDICT server [default: test.dict.org]");
        System.out.println("\t-port <port>\t\t\tserver port [default: 2628]");
        System.out.println("\t-username <username>\t\tprovide username for authentication");
        System.out.println("\t-secret <password>\t\tprovide password for authentication");

        System.out.println("\nServer Commands");
        System.out.println("\t-banner\t\t\t\tshow connection banner");
        System.out.println("\t-serverinfo\t\t\tshow server information");
        System.out.println("\t-help\t\t\t\tshow server help");
        System.out.println("\t-dictionary <dictionary>\tlook up word in <dictionary>");
        System.out.println("\t-dictionaries\t\t\tlist dictionaries available");
        System.out.println("\t-dictionaryinfo <dictionary>\tget information for <dictionary>");
        System.out.println("\t-strategies\t\t\tlist strategies available");
        System.out.println("\t-match <strategy>\t\ttry to match word using strategy");
    }

    private static void showVersion()
    {
        System.out.printf("%s %s\n", JDictClient.getLibraryName(), JDictClient.getLibraryVersion());
    }

    public static void main(String[] args)
        throws NoSuchMethodException, InstantiationException,
                          IllegalAccessException, InvocationTargetException
    {
        JDictClient dictClient;
        String info;
        Hashtable opts;

        List<Dictionary> dictionaries = null;
        List<Strategy> strategies = null ;
        List<Definition> definitions = null;
        List<Match> matches = null;
        Definition definition = null;
        Match match = null;

        if (args.length == 0) {
            showHelp();
            return;
        }

        opts = parseOptions(args);

        try {
            if (opts.containsKey("version")) {
                    showVersion();
                    return;
            }

            dictClient = JDictClient.connect(
                    (String) opts.get("host"),
                    (Integer) opts.get("port")
                );

            if (opts.containsKey("username") && opts.containsKey("secret")) {
                boolean rv = dictClient.authenticate(
                        (String) opts.get("username"),
                        (String) opts.get("secret")
                    );
                if (rv == false)
                    System.out.println("Authentication failed.");
                else
                    System.out.println("Authentication success.");
            }

            if (opts.containsKey("help")) {
                System.out.println(dictClient.getHelp());
            } else if (opts.containsKey("banner")) {
                System.out.println(dictClient.getBanner());
            } else if (opts.containsKey("serverinfo")) {
                System.out.println(dictClient.getServerInfo());
            } else if (opts.containsKey("dictionaryinfo")) {
                String dictionary;
                dictionary = (String)opts.get("dictionaryinfo");
                System.out.println(dictClient.getDictionaryInfo(dictionary));
            } else if (opts.containsKey("dictionaries")) {
                dictionaries = dictClient.getDictionaries();
                if (dictionaries != null) {
                    Iterator itr = dictionaries.iterator();
                    while (itr.hasNext()) {
                        Dictionary dictionary = (Dictionary)itr.next();
                        System.out.println(dictionary);
                    }
                } else {
                    System.out.println("No dictionaries found.");
                }
            } else if (opts.containsKey("strategies")) {
                strategies = dictClient.getStrategies();
                if (strategies != null) {
                    Iterator itr = strategies.iterator();
                    while (itr.hasNext()) {
                        Strategy strategy = (Strategy)itr.next();
                        System.out.println(strategy);
                    }
                } else {
                    System.out.println("No strategies found.");
                }
            } else if (opts.containsKey("words")) {
                Iterator words = ((ArrayList)opts.get("words")).iterator();
                while(words.hasNext()) {
                    String word = (String)words.next();
                    if (opts.containsKey("dictionary")) {
                        String dictionary = (String)opts.get("dictionary");
                        definitions = dictClient.define(dictionary, word);
                    } else if (opts.containsKey("match")) {
                        String strategy = (String)opts.get("match");
                        matches = dictClient.match(strategy, word);
                    } else {
                        definitions = dictClient.define(word);
                    }
                    if (matches != null) {
                        Iterator itr = matches.iterator();
                        while (itr.hasNext()) {
                            match = (Match) itr.next();
                            System.out.println(match);
                        }
                    } else if (definitions != null) {
                        Iterator itr = definitions.iterator();
                        while (itr.hasNext()) {
                            definition = (Definition) itr.next();
                            System.out.println(definition);
                        }
                    } else {
                        System.out.println(word + ": No definitions found.");
                    }
                }
            } else {
                showHelp();
                return;
            }

            System.out.flush();
            dictClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Hashtable parseOptions(String[] args)
    {
        String optname = null;
        Hashtable opts = new Hashtable();
        ArrayList<String> words = new ArrayList<String>();

        opts.put("host", "test.dict.org");
        opts.put("port", 2628);

        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].charAt(0) == '-') {
                    optname = args[i].substring(1);

                    /* String options */
                    if (optname.equals("host")
                            || optname.equals("username")
                            || optname.equals("secret")
                            || optname.equals("dictionary")
                            || optname.equals("dictionaryinfo")
                            || optname.equals("match")) {
                        opts.put(optname, args[++i]);

                        /* Integer options */
                    } else if (optname.equals("port")) {
                        opts.put(optname, Integer.parseInt(args[++i]));

                        /* Flag options */
                    } else if  (optname.equals("version")
                            || optname.equals("banner")
                            || optname.equals("serverinfo")
                            || optname.equals("help")
                            || optname.equals("dictionaries")
                            || optname.equals("strategies")) {
                        opts.put(optname, true);
                            }
                } else {
                    words.add(args[i]);
                    opts.put("words", args[i]);
                }
            }

            if (words.size() > 0) {
                opts.put("words", words);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: " + optname + " expects an argument.");
            System.exit(1);
        }

        return opts;
    }
}
