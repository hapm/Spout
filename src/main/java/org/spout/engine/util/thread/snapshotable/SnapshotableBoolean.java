/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.util.thread.snapshotable;

import java.util.concurrent.atomic.AtomicBoolean;

import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.api.util.thread.annotation.SnapshotRead;

/**
 * A snapshotable object that supports primitive booleans
 */
public class SnapshotableBoolean implements Snapshotable {
	private AtomicBoolean next;
	private boolean snapshot;

	public SnapshotableBoolean(SnapshotManager manager, boolean initial) {
		next = new AtomicBoolean(initial);
		snapshot = initial;
		manager.add(this);
	}

	/**
	 * Sets the next value for the Snapshotable
	 * @param next
	 */
	@DelayedWrite
	public void set(boolean next) {
		this.next.set(next);
	}
	
	/**
	 * Sets the next value but only if the current next value is the given value
	 * 
	 * @param expect
	 * @param next
	 * @return true on success
	 */
	public boolean compareAndSet(boolean expect, boolean next) {
		return this.next.compareAndSet(expect, next);
	}

	/**
	 * Gets the snapshot value for
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public boolean get() {
		return snapshot;
	}

	/**
	 * Gets the live value
	 * @return the unstable Live "next" value
	 */
	@LiveRead
	public boolean getLive() {
		return next.get();
	}

	public boolean isDirty() {
		return snapshot != next.get();
	}

	/**
	 * Copies the next value to the snapshot value
	 */
	@Override
	public void copySnapshot() {
		snapshot = next.get();
	}
}
