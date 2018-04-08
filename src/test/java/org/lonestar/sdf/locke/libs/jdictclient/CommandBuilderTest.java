/*
 * Copyright (C) 2018 Robert Gill <locke@sdf.lonestar.org>
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
package org.lonestar.sdf.locke.libs.jdictclient;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.lonestar.sdf.locke.libs.jdictclient.Command.Type.*;

/**
 * @author Robert Gill &lt;locke@sdf.lonestar.org&gt;
 *
 */
public class CommandBuilderTest {
    @Test
    public void testClientCommand() {
        Command command = new Command.Builder(CLIENT)
                                     .setParamString("CLIENT NAME")
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testClientCommandExceptions() {
        Exception exception = null;
        try {
            Command command = new Command.Builder(CLIENT)
                                         .build();
        } catch (RuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("CLIENT command requires a parameter string.",
                     exception.getMessage());
    }

    @Test
    public void testShowServerCommand() {
        Command command = new Command.Builder(SHOW_SERVER)
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testHelpCommand() {
        Command command = new Command.Builder(HELP)
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testShowDatabasesCommand() {
        Command command = new Command.Builder(SHOW_DATABASES)
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testShowInfoCommand() {
        Command command = new Command.Builder(SHOW_INFO)
                                     .setDatabase("wn")
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testShowInfoCommandExceptions() {
        Exception exception = null;
        try {
            Command command = new Command.Builder(SHOW_INFO)
                                         .build();
        } catch (RuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("SHOW_INFO command requires a database.",
                     exception.getMessage());
    }

    @Test
    public void testShowStrategiesCommand() {
        Command command = new Command.Builder(SHOW_STRATEGIES)
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testDefineCommand() {
        Command command = new Command.Builder(DEFINE)
                                     .setWord("word")
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testDefineCommandExceptions() {
        Exception exception = null;
        try {
            Command command = new Command.Builder(DEFINE)
                                         .build();
        } catch (RuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("DEFINE command requires a word or parameter.",
                     exception.getMessage());
    }

    @Test
    public void testMatchCommand() {
        Command command = new Command.Builder(MATCH)
                                     .setStrategy("exact")
                                     .setParamString("word")
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testMatchCommandExceptions() {
        Exception exception = null;
        try {
            Command command = new Command.Builder(MATCH)
                                         .build();
        } catch (RuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("MATCH command requires a strategy.",
                     exception.getMessage());

        exception = null;
        try {
            Command command = new Command.Builder(MATCH)
                                         .setStrategy("exact")
                                         .build();
        } catch (RuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("MATCH command requires a parameter.",
                     exception.getMessage());
    }

    @Test
    public void testQuitCommand() {
        Command command = new Command.Builder(QUIT)
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testOtherCommand() {
        Command command = new Command.Builder(OTHER)
                                     .setCommandString("CUSTOM COMMAND")
                                     .build();
        assertNotNull(command);
    }

    @Test
    public void testOtherCommandExceptions() {
        Exception exception = null;
        try {
            Command command = new Command.Builder(OTHER)
                                         .build();
        } catch (RuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("OTHER command requires a raw command string.",
                     exception.getMessage());
    }
}
