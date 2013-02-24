/*
 * Created:  Fri 21 Dec 2012 11:03:29 PM PST
 * Modified: Sat 23 Feb 2013 10:17:15 PM PST
 * Copyright © 2013 Robert Gill <locke@sdf.lonestar.org>
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.lonestar.sdf.locke.libs.dict.Definition;
import org.lonestar.sdf.locke.libs.dict.JDictClient;
import org.lonestar.sdf.locke.libs.dict.DictException;
import org.lonestar.sdf.locke.libs.dict.Dictionary;

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
		System.out.println("\t-host <server>\t\t\tDICT server [default: test.dict.org]");
		System.out.println("\t-port <port>\t\t\tserver port [default: 2628]");
		System.out.println("\t-serverinfo\t\t\tshow server information");
		System.out.println("\t-help\t\t\t\tshow server help");
		System.out.println("\t-dictionaries\t\t\tlist dictionaries available");
		System.out.println("\t-dictionaryinfo <dictionary>\tget information for <dictionary>");
	}

	public static void main(String[] args)
	{
		JDictClient dictClient;
		List<Definition> definitions;
		List<Dictionary> dictionaries;
		Definition definition;
		String info;
		Hashtable opts;

		if (args.length == 0) {
			showHelp();
			return;
		}

		opts = parseOptions(args);

		try {
			dictClient = JDictClient.connect(
					(String) opts.get("host"),
					(Integer) opts.get("port")
				);

			if (opts.containsKey("help")) {
				System.out.println(dictClient.getHelp());
				dictClient.close();
			} else if (opts.containsKey("serverinfo")) {
				System.out.println(dictClient.getServerInfo());
				dictClient.close();
			} else if (opts.containsKey("dictionaryinfo")) {
				String dictionary;
				dictionary = (String)opts.get("dictionaryinfo");
				System.out.println(dictClient.getDictionaryInfo(dictionary));
				dictClient.close();
			} else if (opts.containsKey("dictionaries")) {
				dictionaries = dictClient.getDictionaries();
				dictClient.close();

				if (dictionaries != null) {
					Iterator itr = dictionaries.iterator();
					while (itr.hasNext()) {
						Dictionary dictionary = (Dictionary)itr.next();
						System.out.println(dictionary);
					}
				} else {
					System.out.println("No dictionaries found.");
				}
			} else if (opts.containsKey("words")) {
				Iterator words = ((ArrayList)opts.get("words")).iterator();
				while(words.hasNext()) {
					String word = (String)words.next();
					definitions = dictClient.define(word);
					if (definitions != null) {
						Iterator defs = definitions.iterator();
						while (defs.hasNext()) {
							definition = (Definition) defs.next();
							System.out.println(definition);
						}
					} else {
						System.out.println(word + ": No definitions found.");
					}
				}
				dictClient.close();
			} else {
				showHelp();
				return;
			}

		} catch (IOException e) {
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
							|| optname.equals("dictionaryinfo")) {
						opts.put(optname, args[++i]);

						/* Integer options */
					} else if (optname.equals("port")) {
						opts.put(optname, Integer.parseInt(args[++i]));

						/* Flag options */
					} else if (optname.equals("serverinfo")
							|| optname.equals("help")
							|| optname.equals("dictionaries")) {
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
