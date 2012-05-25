package hash.runtime;

import hash.util.Err;

import java.util.Iterator;

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
