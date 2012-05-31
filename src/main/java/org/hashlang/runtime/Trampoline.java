package org.hashlang.runtime;

public class Trampoline implements Continuation {

	private Continuation startingPoint;

	public Trampoline(Continuation startingPoint) {
		this.startingPoint = startingPoint;
	}

	public Object resume() throws Throwable {
		return resume(null);
	}

	public Object resume(Object arg) throws Throwable {
		Continuation current = startingPoint;
		Object rv = current.resume(arg);
		while (rv instanceof Jump) {
			Jump j = (Jump) rv;
			current = j.getContinuation();
			if (current instanceof Trampoline)
				current = ((Trampoline) current).startingPoint;
			if (!current.isAlive())
				break;
			rv = current.resume(j.getArg());
		}
		return rv;
	}

	public boolean isAlive() {
		return startingPoint.isAlive();
	}

}
