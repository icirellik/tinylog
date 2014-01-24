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

package org.pmw.tinylog.writers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.labellers.CountLabeller;
import org.pmw.tinylog.labellers.Labeller;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.StartupPolicy;

/**
 * Writes log entries to a file like {@link org.pmw.tinylog.writers.FileWriter FileWriter} but keeps backups of old
 * logging files.
 */
@PropertiesSupport(name = "rollingfile", properties = { @Property(name = "filename", type = String.class), @Property(name = "backups", type = int.class),
		@Property(name = "buffered", type = boolean.class, optional = true), @Property(name = "label", type = Labeller.class, optional = true),
		@Property(name = "policies", type = Policy[].class, optional = true) })
public final class RollingFileWriter implements LoggingWriter {

	private static final int BUFFER_SIZE = 64 * 1024;

	private final String filename;
	private final int backups;
	private final Labeller labeller;
	private final List<? extends Policy> policies;
	private final boolean buffered;

	private final Object mutex;
	private File file;
	private OutputStream stream;

	/**
	 * Rolling log files once at startup.
	 * 
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * 
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups) {
		this(filename, backups, false, null, (Policy[]) null);
	}

	/**
	 * Rolling log files once at startup.
	 * 
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * 
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups, final boolean buffered) {
		this(filename, backups, buffered, null, (Policy[]) null);
	}

	/**
	 * Rolling log files once at startup.
	 * 
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeller
	 *            Labeller for naming backups
	 * 
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups, final Labeller labeller) {
		this(filename, backups, false, labeller, (Policy[]) null);
	}

	/**
	 * Rolling log files once at startup.
	 * 
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param labeller
	 *            Labeller for naming backups
	 * 
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups, final boolean buffered, final Labeller labeller) {
		this(filename, backups, buffered, labeller, (Policy[]) null);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param policies
	 *            Rollover strategies
	 */
	public RollingFileWriter(final String filename, final int backups, final Policy... policies) {
		this(filename, backups, false, null, policies);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param policies
	 *            Rollover strategies
	 */
	public RollingFileWriter(final String filename, final int backups, final boolean buffered, final Policy... policies) {
		this(filename, backups, buffered, null, policies);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeller
	 *            Labeller for naming backups
	 * @param policies
	 *            Rollover strategies
	 */
	public RollingFileWriter(final String filename, final int backups, final Labeller labeller, final Policy... policies) {
		this(filename, backups, false, labeller, policies);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param labeller
	 *            Labeller for naming backups
	 * @param policies
	 *            Rollover strategies
	 */
	public RollingFileWriter(final String filename, final int backups, final boolean buffered, final Labeller labeller, final Policy... policies) {
		this.mutex = new Object();
		this.filename = filename;
		this.backups = Math.max(0, backups);
		this.buffered = buffered;
		this.labeller = labeller == null ? new CountLabeller() : labeller;
		this.policies = policies == null || policies.length == 0 ? Arrays.asList(new StartupPolicy()) : Arrays.asList(policies);
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY);
	}

	/**
	 * Get the filename of the current log file.
	 * 
	 * @return Filename of the current log file
	 */
	public String getFilename() {
		synchronized (mutex) {
			return file == null ? filename : file.getAbsolutePath();
		}
	}

	/**
	 * Check if buffered writing is enabled.
	 * 
	 * @return <code>true</code> if buffered writing is enabled, otherwise <code>false</code>
	 */
	public boolean isBuffered() {
		return buffered;
	}

	/**
	 * Get the maximum number of backups.
	 * 
	 * @return Maximum number of backups
	 */
	public int getNumberOfBackups() {
		return backups;
	}

	/**
	 * Get the labeller for naming backups.
	 * 
	 * @return Labeller for naming backups
	 */
	public Labeller getLabeller() {
		return labeller;
	}

	/**
	 * Get the rollover strategies.
	 * 
	 * @return Rollover strategies
	 */
	public List<? extends Policy> getPolicies() {
		return Collections.unmodifiableList(policies);
	}

	@Override
	public void init() throws Exception {
		file = labeller.getLogFile(new File(filename));
		initCheckPolicies();
		if (buffered) {
			stream = new BufferedOutputStream(new FileOutputStream(file, true), BUFFER_SIZE);
		} else {
			stream = new FileOutputStream(file, true);
		}
	}

	@Override
	public void write(final LogEntry logEntry) throws Exception {
		synchronized (mutex) {
			if (!checkPolicies(logEntry.getLoggingLevel(), logEntry.getRenderedLogEntry())) {
				stream.close();
				file = labeller.roll(file, backups);
				if (buffered) {
					stream = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
				} else {
					stream = new FileOutputStream(file);
				}
			}
			stream.write(logEntry.getRenderedLogEntry().getBytes());
		}
	}

	/**
	 * Close the log file.
	 * 
	 * @throws IOException
	 *             Failed to close the log file
	 */
	public void close() throws IOException {
		synchronized (mutex) {
			stream.close();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	private void initCheckPolicies() throws Exception {
		for (Policy policy : policies) {
			if (!policy.initCheck(file)) {
				resetPolicies();
				file = labeller.roll(file, backups);
				return;
			}
		}
	}

	private boolean checkPolicies(final LoggingLevel level, final String logEntry) {
		for (Policy policy : policies) {
			if (!policy.check(level, logEntry)) {
				resetPolicies();
				return false;
			}
		}
		return true;
	}

	private void resetPolicies() {
		for (Policy policy : policies) {
			policy.reset();
		}
	}

}
