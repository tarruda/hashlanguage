package hash.util;

import hash.runtime.exceptions.IllegalArgCountException;

public class Check {
	
	public static void numberOfArgs(Object[] args, int expected) {
		if (args.length != expected)
			throw new IllegalArgCountException();
	}
}
