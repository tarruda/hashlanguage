package org.hashlang.runtime;

public class Jump {
	private Continuation continuation;
	private Object arg;

	public Jump(Continuation continuation, Object arg) {
		this.continuation = continuation;
		this.arg = arg;
	}

	public Continuation getContinuation() {
		return continuation;
	}

	public Object getArg() {
		return arg;
	}
}
