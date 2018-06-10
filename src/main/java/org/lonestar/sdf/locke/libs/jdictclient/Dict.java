/*
 * Copyright (C) 2016 Robert Gill <locke@sdf.lonestar.org>
 *
 * This file is part of jdict-client.
 *
 * jdict-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * jdict-client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with jdict-client.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.lonestar.sdf.locke.libs.jdictclient;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Simple command line program that uses features of the library.
 *
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class Dict {
    private static void showHelp() {
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
        System.out.println("\t-database <database>\tlook up word in <database>");
        System.out.println("\t-databases\t\t\tlist databases available");
        System.out.println("\t-databaseinfo <database>\tget information for <database>");
        System.out.println("\t-strategies\t\t\tlist strategies available");
        System.out.println("\t-match <strategy>\t\ttry to match word using strategy");
    }

    private static void showVersion() {
        System.out.printf("%s %s\n", JDictClient.getLibraryName(),
                JDictClient.getLibraryVersion());
    }

    public static void main(String[] args) {
        JDictClient client;
        Hashtable opts;

        List<Database> databases;
        List<Strategy> strategies;

        List<Definition> definitions = null;
        List<Match> matches = null;

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

            client = JDictClient.connect(
                    (String) opts.get("host"),
                    (Integer) opts.get("port")
            );

            if (opts.containsKey("username") && opts.containsKey("secret")) {
                boolean rv = client.authenticate(
                        (String) opts.get("username"),
                        (String) opts.get("secret")
                );
                if (rv == false)
                    System.out.println("Authentication failed.");
                else
                    System.out.println("Authentication success.");
            }

            if (opts.containsKey("help")) {
                System.out.println(client.getHelp());
            } else if (opts.containsKey("banner")) {
                System.out.println(client.getBanner());
            } else if (opts.containsKey("serverinfo")) {
                System.out.println(client.getServerInfo());
            } else if (opts.containsKey("databaseinfo")) {
                String database;
                database = (String) opts.get("databaseinfo");
                System.out.println(client.getDatabaseInfo(database));
            } else if (opts.containsKey("databases")) {
                databases = client.getDictionaries();
                if (databases != null) {
                    for (Database database : databases) {
                        System.out.println(database);
                    }
                } else {
                    System.out.println("No databases found.");
                }
            } else if (opts.containsKey("strategies")) {
                strategies = client.getStrategies();
                if (strategies != null) {
                    for (Strategy strategy : strategies) {
                        System.out.println(strategy);
                    }
                } else {
                    System.out.println("No strategies found.");
                }
            } else if (opts.containsKey("words")) {
                ArrayList<String> words = (ArrayList<String>) opts.get("words");
                for (String word : words) {
                    if (opts.containsKey("match")) {
                        String strategy = (String) opts.get("match");
                        if (opts.containsKey("database")) {
                            String database = (String) opts.get("database");
                            matches = client.match(word, strategy, database);
                        } else {
                            matches = client.match(word, strategy);
                        }
                    } else if (opts.containsKey("database")) {
                        String database = (String) opts.get("database");
                        definitions = client.define(word, database);
                    } else {
                        definitions = client.define(word);
                    }
                    if (matches != null) {
                        for (Match match : matches) {
                            System.out.println(match);
                        }
                    } else if (definitions != null) {
                        for (Definition definition : definitions) {
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
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Hashtable parseOptions(String[] args) {
        String optname = null;
        Hashtable<String, Object> opts = new Hashtable<String, Object>();
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
                            || optname.equals("database")
                            || optname.equals("databaseinfo")
                            || optname.equals("match")) {
                        opts.put(optname, args[++i]);

                    /* Integer options */
                    } else if (optname.equals("port")) {
                        opts.put(optname, Integer.parseInt(args[++i]));

                    /* Flag options */
                    } else if (optname.equals("version")
                            || optname.equals("banner")
                            || optname.equals("serverinfo")
                            || optname.equals("help")
                            || optname.equals("databases")
                            || optname.equals("strategies")) {
                        opts.put(optname, true);
                    }
                } else {
                    words.add(args[i]);
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
