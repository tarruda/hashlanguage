package org.hashlang.runtime;

public interface Continuation {

	Object resume(Object arg) throws Throwable;	
	
	Object resume() throws Throwable;	
	
	boolean isAlive();
	
}
