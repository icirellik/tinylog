/*
 * Copyright 2013 Martin Winandy
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

package org.apache.log4j;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Deprecated log4j logging API (use {@link Logger} instead).
 *
 * @see Logger
 */
public abstract class Category {

	private final Category parent;
	private final String name;
	private volatile ResourceBundle bundle;

	/**
	 * @param parent
	 *            Parent category
	 * @param name
	 *            Name for thecategory
	 */
	Category(final Category parent, final String name) {
		this.parent = parent;
		this.name = name;
	}

	/**
	 * @deprecated Replaced by {@link Logger#getRootLogger()}
	 *
	 * @return Root category
	 */
	@Deprecated
	public static final Category getRoot() {
		return LogManager.getRootLogger();
	}

	/**
	 * @deprecated Replaced by {@link Logger#getLogger(String)}
	 *
	 * @param name
	 *            Name of the category
	 * @return Category instance
	 */
	@Deprecated
	public static Category getInstance(final String name) {
		return LogManager.getLogger(name);
	}

	/**
	 * @deprecated Replaced by {@link Logger#getLogger(String)}
	 *
	 * @param clazz
	 *            Class to log
	 * @return Category instance
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static Category getInstance(final Class clazz) {
		return LogManager.getLogger(clazz);
	}

	/**
	 * Get the parent logger.
	 *
	 * @return Parent logger
	 */
	public final Category getParent() {
		return parent;
	}

	/**
	 * Get the name of the logger.
	 *
	 * @return Name of the logger
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Get the (inherited) result bundle.
	 *
	 * @return Effective result bundle
	 */
	public final ResourceBundle getResourceBundle() {
		for (Category category = this; category != null; category = category.getParent()) {
			if (category.bundle != null) {
				return category.bundle;
			}
		}

		return null;
	}

	/**
	 * Set a new result bundle for localized logging methods.
	 *
	 * @param bundle
	 *            New result bundle
	 */
	public final void setResourceBundle(final ResourceBundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * @deprecated Replaced by {@link Category#getLevel()}
	 *
	 * @return Active logging level
	 */
	@Deprecated
	public final Level getPriority() {
		return TinylogBridge.getLevel();
	}

	/**
	 * @deprecated Replaced by {@link Category#getEffectiveLevel()}
	 * @return Active logging level
	 */
	@Deprecated
	public final Priority getChainedPriority() {
		return TinylogBridge.getLevel();
	}

	/**
	 * Get the active logging level for the caller class. In log4j-facade this method does exactly the same as
	 * {@link Category#getEffectiveLevel()}.
	 *
	 * @return Active logging level
	 */
	public final Level getLevel() {
		return TinylogBridge.getLevel();
	}

	/**
	 * Get the active logging level for the caller class. In log4j-facade this method does exactly the same as
	 * {@link Category#getLevel()}.
	 *
	 * @return Active logging level
	 */
	public final Level getEffectiveLevel() {
		return TinylogBridge.getLevel();
	}

	/**
	 * Check if log entries with the logging level debug are output or not.
	 *
	 * @return <code>true</code> if debug log entries will be output, <code>false</code> if not
	 */
	public final boolean isDebugEnabled() {
		return TinylogBridge.isEnabled(Level.DEBUG);
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public final void debug(final Object message) {
		TinylogBridge.log(Level.DEBUG, message);
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public final void debug(final Object message, final Throwable throwable) {
		TinylogBridge.log(Level.DEBUG, throwable, message == throwable ? null : message);
	}

	/**
	 * Check if log entries with the logging level info are output or not.
	 *
	 * @return <code>true</code> if info log entries will be output, <code>false</code> if not
	 */
	public final boolean isInfoEnabled() {
		return TinylogBridge.isEnabled(Level.INFO);
	}

	/**
	 * Create an info log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public final void info(final Object message) {
		TinylogBridge.log(Level.INFO, message);
	}

	/**
	 * Create an info log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public final void info(final Object message, final Throwable throwable) {
		TinylogBridge.log(Level.INFO, throwable, message == throwable ? null : message);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public final void warn(final Object message) {
		TinylogBridge.log(Level.WARN, message);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public final void warn(final Object message, final Throwable throwable) {
		TinylogBridge.log(Level.WARN, throwable, message == throwable ? null : message);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public final void error(final Object message) {
		TinylogBridge.log(Level.ERROR, message);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public final void error(final Object message, final Throwable throwable) {
		TinylogBridge.log(Level.ERROR, throwable, message == throwable ? null : message);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public final void fatal(final Object message) {
		TinylogBridge.log(Level.FATAL, message);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public final void fatal(final Object message, final Throwable throwable) {
		TinylogBridge.log(Level.FATAL, throwable, message == throwable ? null : message);
	}

	/**
	 * Check if a given logging level will be output.
	 *
	 * @param level
	 *            Logging level to test
	 * @return <code>true</code> if log entries with the given logging level will be output, <code>false</code> if not
	 */
	public final boolean isEnabledFor(final Priority level) {
		return TinylogBridge.isEnabled(level);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param assertion
	 *            If <code>false</code> an error log entry will be generated, otherwise nothing will happen
	 * @param message
	 *            Message to log
	 */
	public final void assertLog(final boolean assertion, final String message) {
		if (!assertion) {
			TinylogBridge.log(Level.ERROR, message);
		}
	}

	/**
	 * Create a log entry.
	 *
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Message to log
	 */
	public final void log(final Priority level, final Object message) {
		TinylogBridge.log(level, message);
	}

	/**
	 * Create a log entry.
	 *
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public final void log(final Priority level, final Object message, final Throwable throwable) {
		TinylogBridge.log(level, throwable, message == throwable ? null : message);
	}

	/**
	 * Create a localized log entry.
	 *
	 * @param priority
	 *            Severity level
	 * @param key
	 *            Key of localized message
	 * @param throwable
	 *            Throwable to log (can be null if none)
	 */
	public final void l7dlog(final Priority priority, final String key, final Throwable throwable) {
		String message = getResourceBundleString(key);
		if (message == null) {
			message = key;
		}

		TinylogBridge.log(priority, throwable, message);
	}

	/**
	 * Create a localized log entry. The parameterized message will be formatted by
	 * {@link java.text.MessageFormat#format(String,Object[])}.
	 *
	 * @param priority
	 *            Severity level
	 * @param key
	 *            Key of localized message
	 * @param params
	 *            Parameters for formatting message
	 * @param throwable
	 *            Throwable to log (can be null if none)
	 */
	public final void l7dlog(final Priority priority, final String key, final Object[] params, final Throwable throwable) {
		String message = getResourceBundleString(key);
		if (message == null) {
			message = key;
		} else {
			message = MessageFormat.format(message, params);
		}

		TinylogBridge.log(priority, throwable, message);
	}

	/**
	 * Get a value from resource bundle.
	 *
	 * @param key
	 *            Key of localized value
	 * @return Localized value
	 */
	protected final String getResourceBundleString(final String key) {
		ResourceBundle foundBundle = getResourceBundle();
		if (foundBundle == null) {
			return null;
		} else {
			try {
				return foundBundle.getString(key);
			} catch (MissingResourceException ex) {
				return null;
			}
		}
	}

}
