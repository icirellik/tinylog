/*
 * Copyright 2012 Martin Winandy
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.pmw.tinylog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.util.StoreWriter.LogEntry;

/**
 * Tests for the logging facade API.
 * 
 * @see LogEntryForwarder
 */
public class LogEntryForwarderTest extends AbstractTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Test the default forward methods.
	 */
	@Test
	public final void testLogging() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).maxStackTraceElements(0).level(LoggingLevel.TRACE).formatPattern("{file}|{message}").activate();

		LogEntryForwarder.forward(0, LoggingLevel.INFO, "Hello!");
		assertEquals(new LogEntry(LoggingLevel.INFO, "LogEntryForwarderTest.java|Hello!" + NEW_LINE), writer.consumeLogEntry());

		LogEntryForwarder.forward(0, LoggingLevel.INFO, "Hello {0}!", "World");
		assertEquals(new LogEntry(LoggingLevel.INFO, "LogEntryForwarderTest.java|Hello World!" + NEW_LINE), writer.consumeLogEntry());

		LogEntryForwarder.forward(0, LoggingLevel.ERROR, new Exception(), "Test");
		assertEquals(new LogEntry(LoggingLevel.ERROR, "LogEntryForwarderTest.java|Test: java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());
	}

	/**
	 * Test the the advanced forward methods with given stack trace elements.
	 */
	@Test
	public final void testLoggingWithStackTraceElement() {
		StackTraceElement stackTraceElement = new StackTraceElement("MyClass", "?", "?", -1);
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).maxStackTraceElements(0).level(LoggingLevel.TRACE).formatPattern("{class}|{message}").activate();

		LogEntryForwarder.forward(stackTraceElement, LoggingLevel.INFO, "Hello!");
		assertEquals(new LogEntry(LoggingLevel.INFO, "MyClass|Hello!" + NEW_LINE), writer.consumeLogEntry());

		LogEntryForwarder.forward(stackTraceElement, LoggingLevel.INFO, "Hello {0}!", "World");
		assertEquals(new LogEntry(LoggingLevel.INFO, "MyClass|Hello World!" + NEW_LINE), writer.consumeLogEntry());

		LogEntryForwarder.forward(stackTraceElement, LoggingLevel.ERROR, new Exception(), "Test");
		assertEquals(new LogEntry(LoggingLevel.ERROR, "MyClass|Test: java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());
	}

}
