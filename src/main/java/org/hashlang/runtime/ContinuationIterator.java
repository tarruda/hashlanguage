package org.hashlang.runtime;


import java.util.Iterator;

import org.hashlang.util.Err;

public class ContinuationIterator implements Iterator {

	private Continuation continuation;

	public ContinuationIterator(Continuation continuation) {
		this.continuation = continuation;
	}

	public boolean hasNext() {
		return continuation.isAlive();
	}

	public Object next() {
		try {
			return continuation.resume();
		} catch (Throwable e) {
			throw Err.ex(e);
		}
	}

	public void remove() {
		throw Err.notImplemented();
	}
}
